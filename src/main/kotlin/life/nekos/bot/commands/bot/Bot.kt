package life.nekos.bot.commands.bot

import com.sun.management.OperatingSystemMXBean
import life.nekos.bot.Loader
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.TextUtils
import life.nekos.bot.utils.extensions.thenException
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import java.time.Instant

class Bot : Cog {
    private fun isSpam(m: Message) = m.contentDisplay.startsWith('~')
            || m.author.idLong == m.jda.selfUser.idLong

    @Command(
        aliases = ["cleanup", "purge", "del"], description = "Cleans up all the bot and command messages.",
        botPermissions = [Permission.MESSAGE_MANAGE]
    )
    fun clean(ctx: Context, amount: Int = 100) {
        ctx.asSlashContext?.defer(ephemeral = true)

        ctx.messageChannel.iterableHistory
            .takeAsync(amount)
            .thenApply { it.filter(::isSpam) }
            .thenApply(ctx.messageChannel::purgeMessages)
            .thenAccept { ctx.respond("I deleted $amount messages \\o/") }
            .thenException { ctx.respond("Some error sry nya~") }
    }

    @Command(
        aliases = ["join", "oauth", "link", "links", "support"],
        description = "Bot and support guild links -.o"
    )
    fun invite(ctx: Context) {
        ctx.respond {
            setColor(Colors.getRandomColor())
            setAuthor(ctx.jda.selfUser.name, ctx.jda.selfUser.effectiveAvatarUrl, ctx.jda.selfUser.effectiveAvatarUrl)
            setDescription(Formats.LING_MSG)
            setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }

    @Command(description = "Pong!")
    fun ping(ctx: Context) {
        ctx.respond("Hello nya~")
    }

    @Command(description = "My statistics~", guildOnly = true)
    fun stats(ctx: Context) {
        /* Uptime */
        val uptime = System.currentTimeMillis() - Loader.bootTime
        val formattedUptime = TextUtils.toTimeString(uptime)

        /* CPU usage */
        val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
        val procCpuUsage = dpFormatter.format(osBean.processCpuLoad * 100)
        val sysCpuUsage = dpFormatter.format(osBean.systemCpuLoad * 100)

        /* RAM usage */
        val usedRaw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val usedPercent = dpFormatter.format(usedRaw.toDouble() / Runtime.getRuntime().totalMemory() * 100)
        val usedMb = dpFormatter.format(usedRaw.toDouble() / 1048576)

        /* Shards */
        val alive = Loader.bot.shardsRunning
        val dead = Loader.bot.shardsTotal - Loader.bot.shardsRunning
        val avgLatency = Loader.bot.averageGatewayPing.toInt()

        /* Output Builder: Bot */
        val content = StringBuilder(String.format("%12s===== NekoBot =====\n", ""))
        content.append(String.format("%-15s: %s\n", "Uptime", formattedUptime))
        content.append(String.format("%-15s: %d\n", "Threads", Thread.activeCount()))
        content.append(String.format("%-15s: %s%%\n", "CPU Usage [SYS]", sysCpuUsage))
        content.append(String.format("%-15s: %s%%\n", "CPU Usage [JVM]", procCpuUsage))
        content.append(String.format("%-15s: %sMB (%s%%)\n", "RAM Usage", usedMb, usedPercent))

        /* Output Builder: Shards */
        content.append(String.format("\n%12s===== Shards  =====\n", ""))
        content.append(String.format("%3s | %-27s | %-3s\n", "ID", "Status", "Latency"))

        for (shard in Loader.bot.shards.reversed()) {
            content.append(
                String.format(
                    "%3d | %-27s | %-3dms\n",
                    shard.shardInfo.shardId,
                    shard.status.name,
                    shard.gatewayPing
                )
            )
        }

        content.append(String.format("\nAlive: %d | Dead: %d | Avg. Latency: %dms", alive, dead, avgLatency))

        ctx.respond("```prolog\n$content```")
    }

    companion object {
        private val dpFormatter = DecimalFormat("0.00")
    }
}
