package life.nekos.bot.utils

import life.nekos.bot.Loader.bot
import life.nekos.bot.Loader.commandClient
import life.nekos.bot.NekoBot
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.apis.PokeApi
import life.nekos.bot.utils.extensions.isNsfw
import life.nekos.bot.utils.extensions.thenException
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class Send(private val message: Message) {
    private fun fetchContextualNeko(): CompletableFuture<String> {
        return if (message.textChannel.isNSFW) NekosLife.lewd() else NekosLife.neko()
    }

    fun neko(dropper: Long) {
        if (!message.guild.selfMember.hasPermission(message.textChannel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)) {
            return
        }

        val keyword = PREFIXES.random() + SUFFIXES.random()

        fetchContextualNeko()
            .thenCompose {
                message.textChannel.sendMessage(EmbedBuilder().apply {
                    setColor(Colors.getRandomColor())
                    setImage(it)
                    setFooter("${Formats.randomCat()} A wild neko has appeared! Use $keyword to catch it before it gets away \\o/")
                }.build()).submit()
            }
            .thenAccept { drop ->
                commandClient.waitFor(DEFAULT_PREDICATE(drop, dropper, keyword), DEFAULT_TIMEOUT)
                    .thenAccept { handleAccept(drop, it) }
                    .thenException { handleException(it, "Neko") }
            }
            .thenException { log.error("[Neko:Drop]", it) }
    }

    fun poke(dropper: Long) {
        if (!message.guild.selfMember.hasPermission(message.textChannel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)) {
            return
        }

        val pokeNum = (1..350).random()

        PokeApi.getPokemon(pokeNum)
            .thenCompose {
                message.textChannel.sendMessage(EmbedBuilder().apply {
                    setColor(Colors.getRandomColor())
                    setImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${it.id}.png")
                    setFooter("${Formats.randomCat()} A wild ${it.name.capitalize()} has appeared! Use >catch to catch it before it gets away \\o/")
                }.build()).submit()
            }
            .thenAccept { drop ->
                commandClient.waitFor(DEFAULT_PREDICATE(drop, dropper, ">catch"), DEFAULT_TIMEOUT)
                    .thenAccept { handleAccept(drop, it) }
                    .thenException { handleException(it, "Pokemon") }
            }
            .thenException { log.error("[Pokemon:Drop]", it) }
    }

    private fun handleAccept(drop: Message, event: MessageReceivedEvent) {
        drop.delete().queue()
        event.message.textChannel.sendMessage("${event.message.author.asMention} Caught it! ${Formats.randomCat()}").queue()
        event.message.delete().queue()
        if (Checks.isDonorPlus(event.message.author.id)) {
            Database.getUser(event.author.id).update {
                nekos += 2
                nekosAll += 2
            }
        } else {
            Database.getUser(event.author.id).update {
                nekos += 1
                nekosAll += 1
            }
        }
    }

    private fun handleException(ex: Throwable, type: String) {
        if (ex is TimeoutException) {
            message.textChannel.sendMessage("Time's up! The $type escaped!").submit()
                .thenAccept { it.delete().queueAfter(15, TimeUnit.SECONDS) }
        } else {
            log.error("[$type:Waiter]", ex)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Send::class.java)

        private val PREFIXES = listOf('!', '.', '>', '<', 'n', '/')
        private val SUFFIXES = listOf("catch", "gimme", "mine", "yus", "owo", "give", "nya", "mew", "nyaaa")

        private val DEFAULT_TIMEOUT = TimeUnit.MINUTES.toMillis(2)

        private val DEFAULT_PREDICATE = { drop: Message, dropper: Long, keyword: String ->
            { it: MessageReceivedEvent ->
                it.channel.idLong == drop.channel.idLong && it.author.idLong != dropper
                        && it.message.contentRaw.toLowerCase() == keyword
            }
        }
    }
}
