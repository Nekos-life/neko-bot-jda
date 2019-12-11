package life.nekos.bot.utils

import me.devoxin.flight.api.Context
import java.awt.Color
import java.util.*

object Colors {
    private val random = Random()

    fun getEffectiveColor(ctx: Context) = ctx.member?.color ?: Color(255, 0, 128)
    fun getRandomColor() = random.nextInt(0xffffff)
}
