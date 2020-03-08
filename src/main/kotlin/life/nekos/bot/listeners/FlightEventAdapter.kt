package life.nekos.bot.listeners

import life.nekos.bot.framework.annotations.CommandHelp
import life.nekos.bot.framework.annotations.DonorOnly
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.DefaultCommandClientAdapter
import me.devoxin.flight.exceptions.BadArgument
import net.dv8tion.jda.api.Permission
import org.slf4j.LoggerFactory
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class FlightEventAdapter : DefaultCommandClientAdapter() {

    fun rootCauseOf(ex: Throwable): Throwable {
        return ex.cause?.let(::rootCauseOf) ?: ex
    }


    override fun onBadArgument(ctx: Context, command: CommandWrapper, error: BadArgument) {
        val extraInfo = command.method.findAnnotation<CommandHelp>()?.let {
            "\n\n```\n${it.help.trimIndent()}```"
        } ?: ""

        ctx.send("You must provide an argument for `${error.argument.name}`, nya~$extraInfo")
    }

    override fun onCommandError(ctx: Context, command: CommandWrapper, error: Throwable) {
        val cause = rootCauseOf(error)
        val type = cause::class.java.simpleName
        log.error("Command {} encountered an error!", command.name, error)

        ctx.send("There was a problem processing the command\n" +
                "The error has been logged, nya~\n```\n$type: ${cause.message}```")
    }

    override fun onCommandPostInvoke(ctx: Context, command: CommandWrapper, failed: Boolean) {
        log.debug("Executed command {}, failed: {}", command.name, failed)
    }

    @ExperimentalStdlibApi
    override fun onCommandPreInvoke(ctx: Context, command: CommandWrapper): Boolean {
        if (command.method.hasAnnotation<DonorOnly>()) {
            // check donor, send error otherwise with emote 475801484282429450
        }

        return true
    }

    override fun onParseError(ctx: Context, command: CommandWrapper, error: Throwable) {
        log.error("Failed to parse argument for command {}", command.name, error)
    }

    override fun onBotMissingPermissions(ctx: Context, command: CommandWrapper, permissions: List<Permission>) {
        val gimme = permissions.joinToString(prefix = "`", postfix = "`", separator = "`\n`") { it.getName() }
        ctx.send("You need to give me these permissions, nya~\n$gimme")
    }

    override fun onUserMissingPermissions(ctx: Context, command: CommandWrapper, permissions: List<Permission>) {
        val missing = permissions.joinToString(prefix = "`", postfix = "`", separator = "`\n`") { it.getName() }
        ctx.send("You need to have these permissions, nya~\n$missing")
    }

    companion object {
        private val log = LoggerFactory.getLogger(FlightEventAdapter::class.java)
    }

}
