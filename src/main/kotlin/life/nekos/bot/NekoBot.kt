package life.nekos.bot

import de.mxro.metrics.jre.Metrics
import life.nekos.bot.listeners.EventHandler
import life.nekos.bot.listeners.MessageHandler
import life.nekos.bot.utils.IntentHelper
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class NekoBot(private val sm: ShardManager) : EventListener, ShardManager by sm {
    init {
        sm.addEventListener(this, EventHandler(), MessageHandler())
    }

    override fun onEvent(event: GenericEvent) {
        metrics.record(Metrics.happened(event.javaClass.simpleName))
    }

    fun home() = sm.getGuildById(333713662739218433L)

    companion object {
        val log: Logger = LoggerFactory.getLogger(NekoBot::class.java)
        val metrics = Metrics.create()!!
        val simpleLbCache: HashMap<String, String> = HashMap()
        fun new(options: DefaultShardManagerBuilder.() -> Unit): NekoBot {
            val shardManager = DefaultShardManagerBuilder.create(IntentHelper.enabledIntents)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ROLE_TAGS)
                .apply(options)
                .build()

            return NekoBot(shardManager)
        }
    }
}
