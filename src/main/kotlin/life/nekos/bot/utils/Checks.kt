package life.nekos.bot.utils

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import life.nekos.bot.Loader
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

object Checks {
    fun hasRole(userId: String, roleId: Long) = Loader.bot.home()?.getMemberById(userId)
        ?.roles?.any { it.idLong == roleId } ?: false

    fun isDonor(userId: String) = hasRole(userId, 392350099331743765L)
    fun isDonorPlus(userId: String) = hasRole(userId, 475508839266123786L)

    fun isMessageRemovable(ctx: Context) = ctx.member?.hasPermission(ctx.textChannel!!, Permission.MESSAGE_MANAGE)
        ?: false

    fun isBotOwner(ctx: Context) = ctx.commandClient.ownerIds.contains(ctx.author.idLong)
    fun isMod(ctx: Context) = isBotOwner(ctx) || ctx.member?.hasPermission(Permission.MESSAGE_MANAGE) ?: false

    fun isDj(member: Member) = member.roles.any { it.name.equals("dj", false) }
    fun isAlone(member: Member) = member.voiceState?.channel?.members?.filterNot { it.user.isBot }?.size?.equals(1)
        ?: false

    fun audioChecks(ctx: Context) = isMod(ctx) || isDj(ctx.member!!) || isAlone(ctx.member!!)
}
