package life.nekos.bot.utils

import life.nekos.bot.apis.NekosLife
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class Send(private val ctx: Context) {
    fun fetchContextualNeko(): CompletableFuture<String> {
        return if (ctx.isNsfw()) NekosLife.lewd() else NekosLife.neko()
    }

    fun neko(dropper: Long) {
        if (!ctx.guild!!.selfMember.hasPermission(ctx.textChannel!!, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)) {
            return
        }

        val keyword = PREFIXES.random() + SUFFIXES.random()

        fetchContextualNeko()
            .thenCompose {
                ctx.send {
                    setColor(Colors.getEffectiveColor(ctx))
                    setImage(it)
                    setFooter("${Formats.randomCat()} A wild neko has appeared! Use $keyword to catch it before it gets away \\o/")
                }
            }
            .thenCompose { drop ->
                ctx.commandClient.waitFor<MessageReceivedEvent>({
                    it.channel.idLong == drop.channel.idLong && it.author.idLong != dropper &&
                            it.message.contentRaw.toLowerCase() == keyword
                }, TimeUnit.MINUTES.toMillis(2))
            }
            .thenAccept(::handleNekoAccept)
            .thenException {
                if (it is TimeoutException) {
                    ctx.send("Time's up! The Neko escaped!")
                        .thenAccept { t -> t.delete().queueAfter(15, TimeUnit.SECONDS) }
                }
            }
    }

    fun poke(dropper: Long) {
        if (!ctx.guild!!.selfMember.hasPermission(ctx.textChannel!!, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS)) {
            return
        }

        val pokeNum = (1..350).random()
        val randomPoke = null // @TODO

        fetchContextualNeko()
            .thenCompose {
                ctx.send {
                    setColor(Colors.getEffectiveColor(ctx))
                    setImage(it)
                    //setFooter("${Formats.randomCat()} A wild neko has appeared! Use $keyword to catch it before it gets away \\o/")
                }
            }
            .thenCompose { drop ->
                ctx.commandClient.waitFor<MessageReceivedEvent>({
                    it.channel.idLong == drop.channel.idLong && it.author.idLong != dropper //&&
                            //it.message.contentRaw.toLowerCase() == keyword
                }, TimeUnit.MINUTES.toMillis(2))
            }
            .thenAccept(::handleNekoAccept)
            .thenException {
                if (it is TimeoutException) {
                    ctx.send("Time's up! The Neko escaped!")
                        .thenAccept { t -> t.delete().queueAfter(15, TimeUnit.SECONDS) }
                }
            }
    }

    private fun handleNekoAccept(event: MessageReceivedEvent) {
        event.message.delete().queue()
        Database.getUser(event.author.id).update {
            nekos += 1
        }
    }

    companion object {
        private val PREFIXES = listOf('!', '.', '>', '<', 'n', '/')
        private val SUFFIXES = listOf("catch", "gimme", "mine", "yus", "owo", "give", "nya", "mew", "nyaaa")
    }
}
