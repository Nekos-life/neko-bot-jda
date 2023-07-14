package life.nekos.bot.listeners

import de.mxro.metrics.jre.Metrics
import life.nekos.bot.Loader.bot
import life.nekos.bot.NekoBot
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.WebhookManager
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent
import net.dv8tion.jda.api.events.session.SessionRecreateEvent
import net.dv8tion.jda.api.events.session.SessionResumeEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color

class EventHandler : EventListener {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(EventHandler::class.java)
        private val avatar = NekosLife.avatar.get()
    }

    override fun onEvent(event: GenericEvent) {
        NekoBot.metrics.record(Metrics.happened(event.javaClass.simpleName))

        when (event) {
            is ReadyEvent -> onReady(event)
            is SessionRecreateEvent -> onReconnect(event)
            is SessionResumeEvent -> onResume(event)
            is SessionDisconnectEvent -> onDisconnect(event)
            is GuildJoinEvent -> onGuildJoin(event)
            is GuildLeaveEvent -> onGuildLeave(event)
        }
    }

    private fun onReady(event: ReadyEvent) {
        log.info(
            "Logged in as {} on {} ({}/{} guilds available)",
            event.jda.selfUser.asTag, event.jda.shardInfo, event.guildAvailableCount, event.guildTotalCount
        )

        if (event.jda.shardInfo.shardId + 1 == event.jda.shardInfo.shardTotal) {
            log.info(Formats.getReadyFormat(event.jda, bot.home!!))

            return WebhookManager.sendShard(avatar) {
                setTitle(
                    "ALL SHARDS CONNECTED",
                    "https://discordapp.com/oauth2/authorize?permissions=8&client_id=${event.jda.selfUser.id}&scope=bot"
                )
                setDescription("Average Shard Ping: ${bot.averageGatewayPing}ms")
                setThumbnail(event.jda.selfUser.effectiveAvatarUrl)
                addField("Ready Info", "```\n${Formats.getReadyFormat(event.jda, bot.home!!)}```", false)
            }
        }

        WebhookManager.sendShard(avatar) {
            setTitle(
                "SHARD READY [${event.jda.shardInfo.shardId}]",
                "https://discordapp.com/oauth2/authorize?permissions=8&client_id=${event.jda.selfUser.id}&scope=bot"
            )
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    private fun onReconnect(event: SessionRecreateEvent) {
        log.info("Reconnected on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD RECONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    private fun onResume(event: SessionResumeEvent) {
        log.info("Resumed on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD RESUMED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    private fun onDisconnect(event: SessionDisconnectEvent) {
        log.info("Disconnect on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD DISCONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms") // will probably be -1 or something lol
        }
    }

    private fun onGuildJoin(event: GuildJoinEvent) {
        val guild = event.guild

        WebhookManager.sendJoin {
            setColor(Color.green)
            setTitle("Guild Joined: ${guild.name}")
            setDescription(
                "${Formats.info("info")}\n" +
                        "On Shard: ${event.jda.shardInfo.shardId}\n" +
                        "Total Guilds: ${event.jda.shardManager!!.guilds.size}\n\n" +
                        "OwnerID: ${guild.ownerId})\n" +
                        "Members: ${guild.memberCount}"
            )
            setThumbnail(guild.iconUrl)
        }
    }

    private fun onGuildLeave(event: GuildLeaveEvent) {
        val guild = event.guild

        WebhookManager.sendLeave {
            setColor(Color.red)
            setTitle("Guild Left: ${guild.name}")
            setDescription(
                "${Formats.info("info")}\n" +
                        "On Shard: ${event.jda.shardInfo.shardId}\n" +
                        "Total Guilds: ${event.jda.shardManager!!.guilds.size}\n\n" +
                        "OwnerID: ${guild.ownerId})\n" +
                        "Members: ${guild.memberCount}"
            )
            setThumbnail(guild.iconUrl)
        }
    }
}
