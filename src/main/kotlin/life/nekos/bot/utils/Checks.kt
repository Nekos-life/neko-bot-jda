package life.nekos.bot.utils

import life.nekos.bot.Loader
import me.devoxin.flight.api.context.Context
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member

object Checks {
    fun hasRole(userId: Long, roleId: Long) = Loader.bot.home?.getMemberById(userId)
        ?.roles?.any { it.idLong == roleId } ?: false

    fun isDonor(userId: Long) = hasRole(userId, 392350099331743765L)
    fun isDonorPlus(userId: Long) = hasRole(userId, 475508839266123786L)

    fun isMessageRemovable(ctx: Context) = ctx.member?.hasPermission(ctx.guildChannel!!, Permission.MESSAGE_MANAGE)
        ?: false

    fun isBotOwner(ctx: Context) = ctx.commandClient.ownerIds.contains(ctx.author.idLong)
    fun isMod(ctx: Context) = isBotOwner(ctx) || ctx.member?.hasPermission(Permission.MESSAGE_MANAGE) ?: false

    fun isDj(member: Member) = member.roles.any { it.name.equals("dj", false) }
    fun isAlone(member: Member) = member.voiceState?.channel?.members?.filterNot { it.user.isBot }?.size?.equals(1)
        ?: false

    fun audioChecks(ctx: Context) = isMod(ctx) || isDj(ctx.member!!) || isAlone(ctx.member!!)
}
