package life.nekos.bot.listeners


import de.mxro.metrics.jre.Metrics
import life.nekos.bot.NekoBot
import life.nekos.bot.utils.Checks
import life.nekos.bot.utils.Database
import life.nekos.bot.utils.Send
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.floor
import kotlin.math.sqrt

class MessageHandler : ListenerAdapter() {
    private val threadCounter = AtomicInteger()
    private val messageExecutorPool = Executors.newCachedThreadPool {
        Thread(it, "Command-Executor-${threadCounter.getAndIncrement()}")
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {

        if (event.author.isBot or !event.isFromGuild) {
            return
        }

        messageExecutorPool.execute {
            processMessageEvent(event)
        }
        processUser(event)

    }

    private fun processMessageEvent(event: MessageReceivedEvent) {

        if (event.message.mentionsEveryone()) {
            NekoBot.metrics.record(Metrics.happened("atEveryoneSeen"))
        }

        if (!event.textChannel.canTalk()) {
            return
        }

        val guild = Database.getGuild(event.message.guild.id)

        if (guild.nekoChannel != null
            && guild.nekoChannel.equals(event.message.channel.id, ignoreCase = true)
        ) {
            guild.update { msgCnt++ }
            if (guild.msgCnt > (40..150).random()) {
                val messages = event.message.channel.iterableHistory.limit(10).submit().get()
                val userMessageCount = messages.map { it.author }.toSet().count { !it.isBot }
                if (userMessageCount >= 3) {
                    guild.update { msgCnt = 0 }
                    Send(event.message, true).neko(event.message.author.idLong)
                }
            }
        }
    }

    private fun processUser(event: MessageReceivedEvent) {

        val user = Database.getUser(event.message.author.id)

        if (user.coolDownCount >= (0..10).random()) {
            val curLevel = floor(0.1 * sqrt(user.exp.toDouble()))
            if (Checks.isDonorPlus(event.message.author.id)) {
                user.update {
                    exp += 2
                    level = curLevel.toLong()
                    coolDownCount = (0..10).random()
                }
            } else {
                user.update {
                    exp += 1
                    level = curLevel.toLong()
                    coolDownCount = (0..10).random()
                }
            }
        }
    }
}
