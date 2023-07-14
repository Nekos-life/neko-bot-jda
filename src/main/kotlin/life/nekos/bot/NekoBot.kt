package life.nekos.bot

import de.mxro.metrics.jre.Metrics
import life.nekos.bot.listeners.EventHandler
import life.nekos.bot.listeners.MessageHandler
import life.nekos.bot.utils.IntentHelper
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag

class NekoBot(private val sm: ShardManager) : ShardManager by sm {
    val home: Guild?
        get() = sm.getGuildById(333713662739218433L)

    companion object {
        val metrics = Metrics.create()!!
        val simpleLbCache: HashMap<String, String> = HashMap()

        fun new(options: DefaultShardManagerBuilder.() -> Unit): NekoBot {
            val shardManager = DefaultShardManagerBuilder.create(IntentHelper.enabledIntents)
                .disableCache(
                    CacheFlag.ACTIVITY,
                    CacheFlag.CLIENT_STATUS,
                    CacheFlag.EMOJI,
                    CacheFlag.FORUM_TAGS,
                    CacheFlag.ONLINE_STATUS,
                    CacheFlag.ROLE_TAGS,
                    CacheFlag.SCHEDULED_EVENTS,
                    CacheFlag.STICKER
                )
                .apply(options)
                .addEventListeners(EventHandler(), MessageHandler())
                .build()

            return NekoBot(shardManager)
        }
    }
}
