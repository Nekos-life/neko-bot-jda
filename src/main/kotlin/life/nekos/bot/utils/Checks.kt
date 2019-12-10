package life.nekos.bot.utils

import life.nekos.bot.Loader

object Checks {
    fun hasRole(userId: String, roleId: Long) = Loader.bot.home()?.getMemberById(userId)
        ?.roles?.any { it.idLong == roleId } ?: false

    fun isDonor(userId: String) = hasRole(userId, 392350099331743765L)
    fun isDonorPlus(userId: String) = hasRole(userId, 475508839266123786L)
}
