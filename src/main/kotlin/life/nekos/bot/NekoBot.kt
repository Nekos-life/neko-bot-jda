package life.nekos.bot

import life.nekos.bot.utils.IntentHelper
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory

class NekoBot(private val sm: ShardManager) : EventListener, ShardManager by sm {
    init {
        sm.addEventListener(this)
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is ReadyEvent -> onReadyEvent(event)
        }
    }

    private fun onReadyEvent(event: ReadyEvent) {
        log.info("Logged in as {} ({}/{} guilds available)",
            event.jda.selfUser.asTag, event.guildAvailableCount, event.guildTotalCount)
    }

    fun home() = sm.getGuildById(333713662739218433L)

    companion object {
        private val log = LoggerFactory.getLogger(NekoBot::class.java)

        fun new(options: DefaultShardManagerBuilder.() -> Unit): NekoBot {
            val shardManager = DefaultShardManagerBuilder.create(IntentHelper.enabledIntents)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ROLE_TAGS)
                .apply(options)
                .build()

            return NekoBot(shardManager)
        }
    }
}
