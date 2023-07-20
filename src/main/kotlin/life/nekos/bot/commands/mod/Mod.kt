package life.nekos.bot.commands.mod

import kotlinx.coroutines.future.await
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.extensions.respondUnit
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import java.util.concurrent.TimeUnit

class Mod : Cog {
    @Command(
        aliases = ["gtfo"], description = "Bans an asshat", guildOnly = true,
        botPermissions = [Permission.BAN_MEMBERS], userPermissions = [Permission.BAN_MEMBERS]
    )
    suspend fun ban(ctx: Context, member: Member, reason: String = "None specified") {
        if (!ctx.member!!.canInteract(member)) {
            return ctx.respondUnit("Nu nya, you don't have permission to ban this member~")
        }

        if (!ctx.guild!!.selfMember.canInteract(member)) {
            return ctx.respondUnit("Nu nya, I don't have permission to ban this member~")
        }

        member.ban(7, TimeUnit.DAYS).reason(reason).submit().await()
        ctx.respond("${Formats.INFO_EMOTE} Banned `${member.user.name}`, nya~")
    }

    @Command(
        description = "Kicks an asshat", guildOnly = true, botPermissions = [Permission.KICK_MEMBERS],
        userPermissions = [Permission.KICK_MEMBERS]
    )
    suspend fun kick(ctx: Context, member: Member, reason: String = "None specified") {
        if (!ctx.member!!.canInteract(member)) {
            return ctx.respondUnit("Nu nya, you don't have permission to kick this member~")
        }

        if (!ctx.guild!!.selfMember.canInteract(member)) {
            return ctx.respondUnit("Nu nya, I don't have permission to kick this member~")
        }

        member.kick().reason(reason).submit().await()
        ctx.respond("${Formats.INFO_EMOTE} Kicked `${member.user.name}`, nya~")
    }
}
