package life.nekos.bot.utils

import life.nekos.bot.Loader
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.Permission

object Checks {
    fun hasRole(userId: String, roleId: Long) = Loader.bot.home()?.getMemberById(userId)
        ?.roles?.any { it.idLong == roleId } ?: false

    fun isDonor(userId: String) = hasRole(userId, 392350099331743765L)
    fun isDonorPlus(userId: String) = hasRole(userId, 475508839266123786L)

    fun isMessageRemovable(ctx: Context) = ctx.member?.hasPermission(ctx.textChannel!!, Permission.MESSAGE_MANAGE)
        ?: false
}
