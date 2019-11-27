package life.nekos.bot.listeners

import me.devoxin.flight.api.CommandError
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.DefaultCommandClientAdapter
import me.devoxin.flight.exceptions.BadArgument
import net.dv8tion.jda.api.Permission

class FlightEventAdapter : DefaultCommandClientAdapter() {

    fun rootCauseOf(ex: Throwable): Throwable {
        val cause = ex.cause

        if (cause != null) {
            return rootCauseOf(cause)
        }

        return ex
    }


    override fun onBadArgument(ctx: Context, error: BadArgument) {

    }

    override fun onCommandError(ctx: Context, error: CommandError) {
        val cause = rootCauseOf(error.original)
        ctx.send("oop\n```\n${cause.message}```")
    }

    override fun onCommandPostInvoke(ctx: Context, command: CommandWrapper, failed: Boolean) {

    }

    override fun onCommandPreInvoke(ctx: Context, command: CommandWrapper): Boolean {
        return true
    }

    override fun onParseError(ctx: Context, error: Throwable) {

    }

    override fun onBotMissingPermissions(ctx: Context, command: CommandWrapper, permissions: List<Permission>) {

    }

    override fun onUserMissingPermissions(ctx: Context, command: CommandWrapper, permissions: List<Permission>) {

    }

}