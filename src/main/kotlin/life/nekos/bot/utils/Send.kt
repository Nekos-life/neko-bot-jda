package life.nekos.bot.utils

import life.nekos.bot.Loader
import life.nekos.bot.Loader.commandClient
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.apis.PokeApi
import life.nekos.bot.utils.extensions.thenException
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class Send(private val channel: GuildMessageChannel, private val organic: Boolean) {
    private val lock = ReentrantLock()

    fun neko(dropperUserId: Long) {
        drop("Neko", dropperUserId, { NekosLife.neko }) {
            setImage(it)
            setDescription("${Formats.randomCat()} A wild neko has appeared!")
        }
    }

    fun poke(dropperUserId: Long) {
        drop("Pokemon", dropperUserId, { PokeApi.getPokemon((1..350).random()) }) {
            setImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${it.id}.png")
            setDescription("A wild ${it.name.capitalize()} has appeared!")
        }
    }

    fun <T> drop(type: String, dropperUserId: Long,
                 supplier: () -> CompletableFuture<T>, embedBuilder: EmbedBuilder.(T) -> Unit) {
        if (!channel.guild.selfMember.hasPermission(channel, Permission.MESSAGE_SEND, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE)) {
            return
        }

        val dropId = UUID.randomUUID().toString().replace("-", "").substring(0, 6)
        val buttons = createButtons(10, "$dropId:$dropperUserId")
        val buttonRows = buttons.chunked(5, ActionRow::of)

        val winningIndex = buttons.indices.random()
        val winnerButton = buttons[winningIndex]

        supplier().thenCompose {
            val embed = EmbedBuilder()
                .setColor(Colors.getRandomColor())
                .apply { embedBuilder(this, it) }
                .setFooter("Click the ${_COLOUR_MAPPING[winnerButton.style]} \"${winnerButton.label}\" button to catch it!")
                .build()
            val createData = MessageCreateBuilder().setEmbeds(embed).setComponents(buttonRows).build()
            channel.sendMessage(createData).submit()
        }.thenAccept { drop ->
            Loader.buttonWaiter.newCapture {
                lifespan = DEFAULT_TIMEOUT
                test = { it.componentId.startsWith("$dropId:$dropperUserId") }
                timeout = {
                    drop.delete().queue()
                    channel.sendMessage("Time's up! The $type escaped!").queue { r -> r.delete().queueAfter(15, TimeUnit.SECONDS) }
                }
                handle = { it, removeCapture ->
                    val checkOrganic = organicCheck(organic, dropperUserId, it)
                    val idParts = it.componentId.split(':')
                    val buttonIndex = idParts[2].toInt()

                    when {
                        buttonIndex != winningIndex -> it.reply("Nu nya! That wasn't the right button...").setEphemeral(true).queue()
                        !checkOrganic -> it.reply("Nu nya! You can't claim this...").setEphemeral(true).queue()
                        !lock.tryLock() -> it.reply("Nu nya! You were too slow~")
                        else -> {
                            removeCapture()
                            drop.delete().queue()
                            it.reply("${it.user.asMention} caught it! ${Formats.randomCat()}").queue { r -> r.deleteOriginal().queueAfter(5, TimeUnit.SECONDS) }

                            val multiplier = when {
                                Checks.isDonorPlus(it.user.idLong) -> 2
                                else -> 1
                            }

                            Database.getUser(it.user.id).update {
                                nekos += 1 * multiplier
                                nekosAll += 1 * multiplier
                            }
                        }
                    }
                }
            }
        }.thenException { log.error("[$type:Drop]", it) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Send::class.java)

        private val DEFAULT_TIMEOUT = TimeUnit.MINUTES.toMillis(1)

        private val PREFIXES = listOf('!', '.', '>', '<', 'n', '/')
        private val SUFFIXES = listOf("catch", "gimme", "mine", "yus", "owo", "give", "nya", "mew", "nyaaa")
        private val BUTTON_STYLES = listOf(ButtonStyle.PRIMARY, ButtonStyle.SECONDARY, ButtonStyle.SUCCESS, ButtonStyle.DANGER)

        private val _COLOUR_MAPPING = mapOf(
            ButtonStyle.PRIMARY to "blue-purple",
            ButtonStyle.SECONDARY to "grey",
            ButtonStyle.SUCCESS to "green",
            ButtonStyle.DANGER to "red"
        )

        private fun createButtons(amount: Int = 10, idFormat: String): List<Button> {
            val buttons = mutableListOf<Button>()

            while (buttons.size < amount) {
                val button = Button.of(BUTTON_STYLES.random(), "$idFormat:${buttons.size}", "${PREFIXES.random()}${SUFFIXES.random()}")

                if (buttons.none { it.style == button.style && it.label == button.label }) {
                    buttons.add(button)
                }
            }

            return buttons
        }

        private fun organicCheck(organic: Boolean, dropper: Long, catcher: ButtonInteractionEvent): Boolean {
            return commandClient.ownerIds.contains(catcher.user.idLong) || organic || catcher.user.idLong != dropper
        }
    }
}
