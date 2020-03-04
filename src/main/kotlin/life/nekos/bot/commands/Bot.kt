package life.nekos.bot.commands

import life.nekos.bot.Loader
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.TextUtils
import life.nekos.bot.utils.extensions.thenException
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import java.time.Instant
import java.util.concurrent.TimeUnit

class Bot : Cog {

    private fun isSpam(m: Message) = m.contentDisplay.startsWith('~')
            || m.author.idLong == m.jda.selfUser.idLong

    @Command(aliases = ["cleanup", "purge", "del"], description = "Cleans up all the bot and command messages.",
        botPermissions = [Permission.MESSAGE_MANAGE])
    fun clean(ctx: Context, amount: Int = 100) {
        ctx.messageChannel.iterableHistory
            .takeAsync(amount)
            .thenApply { it.filter(::isSpam) }
            .thenApply(ctx.messageChannel::purgeMessages)
            .thenCompose { ctx.messageChannel.sendMessage("I deleted $amount messages \\o/").submit() }
            .thenAccept { it.delete().queueAfter(5, TimeUnit.SECONDS) }
            .thenException { ctx.send("Some error sry nya~") }
    }

    @Command(aliases = ["join", "oauth", "link", "links", "support"],
        description = "Bot and support guild links -.o")
    fun invite(ctx: Context) {
        // Model.statsUp("invite")
        ctx.send {
            setColor(Colors.getRandomColor())
            setAuthor(ctx.jda.selfUser.name, ctx.jda.selfUser.effectiveAvatarUrl, ctx.jda.selfUser.effectiveAvatarUrl)
            setDescription(Formats.LING_MSG)
            setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }

    @Command(description = "Pong!")
    fun ping(ctx: Context) {
        ctx.send("Hello nya~")
    }

    @Command(description = "My statistics~", guildOnly = true)
    fun stats(ctx: Context) {
        val uptime = System.currentTimeMillis() - Loader.bootTime
        val formattedUptime = TextUtils.toTimeString(uptime)

        val content = StringBuilder(String.format("%12s===== NekoBot =====\n", ""))
        content.append(String.format("%-15s: %s\n", "Uptime", formattedUptime))
        content.append(String.format("%-15s: %d\n", "Threads", Thread.activeCount()))

        content.append(String.format("\n%12s===== Shards  =====\n", ""))
        content.append(String.format("%3s | %-28s | %-3s\n", "ID", "Status", "Latency"))

        for (shard in Loader.bot.shards.reversed()) {
            content.append(String.format("%3d | %-28s | %-3dms\n", shard.shardInfo.shardId, shard.status.name, shard.gatewayPing))
        }

        ctx.send("```prolog\n$content```")
    }

}
