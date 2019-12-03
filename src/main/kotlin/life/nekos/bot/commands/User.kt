package life.nekos.bot.commands

import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Greedy
import me.devoxin.flight.arguments.Optional
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.entities.Member

class User : Cog {

    override fun localCheck(ctx: Context, command: CommandWrapper): Boolean {
        // Model.statsUp(command.name)
        return true
    }

    @Command(aliases = ["lb", "top", "ranks"], description = "Global leaderboard. Category can be either \"nekos\" or \"levels\"")
    fun leaderboard(ctx: Context, category: String) {

    }

    @Command(aliases = ["rank", "exp"], description = "Shows your, or another user's profile.")
    fun profile(ctx: Context, @Greedy @Optional user: Member?) {
        val target = user ?: ctx.member

    }

    @Command(aliases = ["free", "catch"], description = "Releases one of your nekos for others to catch >.< (You cannot catch a neko you released)")
    fun release(ctx: Context, @Greedy @Optional user: Member?) {
        val target = user ?: ctx.member

    }

    @Command(description = "Send someone a neko image.", guildOnly = true)
    fun send(ctx: Context, @Greedy @Optional user: Member?) {
        val target = user ?: ctx.member

    }

}