package life.nekos.bot.listeners

import life.nekos.bot.framework.annotations.ArgDoc
import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.getAnnotationOrNull
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.DefaultCommandClientAdapter
import me.devoxin.flight.exceptions.BadArgument
import net.dv8tion.jda.api.Permission

class FlightEventAdapter : DefaultCommandClientAdapter() {

    fun rootCauseOf(ex: Throwable): Throwable {
        return ex.cause?.let { rootCauseOf(ex) } ?: ex
    }


    override fun onBadArgument(ctx: Context, command: CommandWrapper, error: BadArgument) {
        val argInfo = command.method.getAnnotationOrNull(ArgDoc::class.java)

        if (argInfo != null && argInfo.name == error.argument.name) {
            ctx.send("`${error.argument.name}` ${argInfo.desc}, nya~")
        } else {
            ctx.send("You must provide an argument for `${error.argument.name}`, nya~")
        }
    }

    override fun onCommandError(ctx: Context, command: CommandWrapper, error: Throwable) {
        val cause = rootCauseOf(error)
        ctx.send("oop\n```\n${cause.message}```")
        error.printStackTrace()
    }

    override fun onCommandPostInvoke(ctx: Context, command: CommandWrapper, failed: Boolean) {
        println("Command ${command.name} finished execution. Failed: $failed")
    }

    override fun onCommandPreInvoke(ctx: Context, command: CommandWrapper): Boolean {
        if (command.method.isAnnotationPresent(DonorOnly::class.java)) {
            // check donor, send error otherwise with emote 475801484282429450
        }

        return true
    }

    override fun onParseError(ctx: Context, command: CommandWrapper, error: Throwable) {
        ctx.send("An error occurred during argument parsing.\n```\n$error```")
        error.printStackTrace()
    }

    override fun onBotMissingPermissions(ctx: Context, command: CommandWrapper, permissions: List<Permission>) {

    }

    override fun onUserMissingPermissions(ctx: Context, command: CommandWrapper, permissions: List<Permission>) {

    }

}