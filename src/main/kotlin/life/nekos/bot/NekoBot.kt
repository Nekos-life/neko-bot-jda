package life.nekos.bot

import life.nekos.bot.utils.Checks
import life.nekos.bot.utils.Database
import life.nekos.bot.utils.Formats.NEKO_BOOT_BANNER
import life.nekos.bot.utils.IntentHelper
import life.nekos.bot.utils.Send
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory
import kotlin.math.floor
import kotlin.math.sqrt


class NekoBot(private val sm: ShardManager) : EventListener, ShardManager by sm {
    init {
        sm.addEventListener(this)
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is ReadyEvent -> onReadyEvent(event)
            is GuildMessageReceivedEvent -> onMessage(event.message)
        }
    }

    private fun onReadyEvent(event: ReadyEvent) {
        log.info(
            "Logged in as {} on {} ({}/{} guilds available)",
            event.jda.selfUser.asTag, event.jda.shardInfo, event.guildAvailableCount, event.guildTotalCount
        )
        if (event.jda.shardInfo.shardId + 1 == event.jda.shardInfo.shardTotal) {
            log.info("Fully Ready\n{}", NEKO_BOOT_BANNER)
        }
    }

    private fun onMessage(message: Message) {
        if (message.author.isBot) {
            return
        }

        val guild = Database.getGuild(message.guild.id)
        if (guild.nekoChannel != null
            && guild.nekoChannel.equals(message.channel.id, ignoreCase = true)
        ) {
            guild.update { msgCnt++ }
            if (guild.msgCnt > (40..150).random()) {
                guild.update { msgCnt = 0 }
                Send(message).neko(message.author.idLong)
            }
        }

        val user = Database.getUser(message.author.id)
        if (user.coolDownCount >= (0..10).random()) {
            val curLevel = floor(0.1 * sqrt(user.exp.toDouble()))
            if (Checks.isDonorPlus(message.author.id)) {
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
