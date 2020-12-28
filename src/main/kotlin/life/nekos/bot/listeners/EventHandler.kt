package life.nekos.bot.listeners

import life.nekos.bot.Loader.bot
import life.nekos.bot.NekoBot
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.WebhookManager
import net.dv8tion.jda.api.events.DisconnectEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.ReconnectedEvent
import net.dv8tion.jda.api.events.ResumedEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color


class EventHandler : ListenerAdapter() {
    private var avatar: String? = NekosLife.avatar().get().toString()

    override fun onReady(event: ReadyEvent) {
        NekoBot.log.info(
            "Logged in as {} on {} ({}/{} guilds available)",
            event.jda.selfUser.asTag, event.jda.shardInfo, event.guildAvailableCount, event.guildTotalCount
        )
        if (event.jda.shardInfo.shardId + 1 == event.jda.shardInfo.shardTotal) {
            NekoBot.log.info(Formats.getReadyFormat(event.jda, bot.home()!!))
            WebhookManager.sendShard(avatar) {
                setTitle(
                    "ALL SHARDS CONNECTED",
                    "https://discordapp.com/oauth2/authorize?permissions=8&client_id=${event.jda.selfUser.id}&scope=bot"
                )
                setDescription("Average Shard Ping: ${bot.averageGatewayPing}ms")
                setThumbnail(event.jda.selfUser.effectiveAvatarUrl)
                addField("Ready Info", "```\n${Formats.getReadyFormat(event.jda, bot.home()!!)}```", false)
            }
            return

        }
        WebhookManager.sendShard(avatar) {
            setTitle(
                "SHARD READY [${event.jda.shardInfo.shardId}]",
                "https://discordapp.com/oauth2/authorize?permissions=8&client_id=${event.jda.selfUser.id}&scope=bot"
            )
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    override fun onReconnect(event: ReconnectedEvent) {
        NekoBot.log.info("Reconnected on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")
        WebhookManager.sendShard(avatar) {
            setTitle("SHARD RECONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    override fun onResume(event: ResumedEvent) {
        NekoBot.log.info("Resumed on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")
        WebhookManager.sendShard(avatar) {
            setTitle("SHARD RESUMED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    override fun onDisconnect(event: DisconnectEvent) {
        NekoBot.log.info("Disconnect on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD DISCONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms") // will probably be -1 or something lol
        }
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
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

    override fun onGuildLeave(event: GuildLeaveEvent) {
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
