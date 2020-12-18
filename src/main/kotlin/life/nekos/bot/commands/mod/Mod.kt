package life.nekos.bot.commands.mod

import life.nekos.bot.utils.Formats
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member

class Mod : Cog {
    @Command(aliases = ["gtfo"], description = "Bans an asshat", guildOnly = true,
        botPermissions = [Permission.BAN_MEMBERS], userPermissions = [Permission.BAN_MEMBERS])
    fun ban(ctx: Context, member: Member, reason: String = "None specified") {
        member.ban(7, reason).queue {
            ctx.send("${Formats.INFO_EMOTE} Banned `${member.user.asTag}`, nya~")
        }
    }

    @Command(description = "Kicks an asshat", guildOnly = true, botPermissions = [Permission.KICK_MEMBERS],
        userPermissions = [Permission.KICK_MEMBERS])
    fun kick(ctx: Context, member: Member, reason: String = "None specified") {
        member.kick(reason).queue {
            ctx.send("${Formats.INFO_EMOTE} Kicked `${member.user.asTag}`, nya~")
        }
    }
}
