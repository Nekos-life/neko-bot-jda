package life.nekos.bot.utils

import me.devoxin.flight.api.context.Context
import java.awt.Color
import java.util.*

object Colors {
    private val random = Random()
    private val colorMap = mapOf(
        "black" to 0xFF000000,
        "darkgray" to 0xFF444444,
        "gray" to 0xFF888888,
        "lightgray" to 0xFFCCCCCC,
        "white" to 0xFFFFFFFF,
        "red" to 0xFFFF0000,
        "green" to 0xFF00FF00,
        "blue" to 0xFF0000FF,
        "yellow" to 0xFFFFFF00,
        "cyan" to 0xFF00FFFF,
        "magenta" to 0xFFFF00FF,
        "aqua" to 0xFF00FFFF,
        "fuchsia" to 0xFFFF00FF,
        "lime" to 0xFF00FF00,
        "maroon" to 0xFF800000,
        "navy" to 0xFF000080,
        "olive" to 0xFF808000,
        "purple" to 0xFF800080,
        "silver" to 0xFFC0C0C0,
        "teal" to 0xFF008080,
        "pink" to 0xFFFFC0CB,
        "peach" to 0xFFffe5b4,
        "darkgrey" to 0xFF444444,
        "grey" to 0xFF888888,
        "lightgrey" to 0xFFCCCCCC
    )

    fun getEffectiveColor(ctx: Context) = ctx.member?.color ?: Color(255, 0, 128)
    fun getRandomColor() = random.nextInt(0xffffff)

    fun decodeOrNull(nm: String) = try {
        Color.decode(nm)
    } catch (e: NumberFormatException) {
        null
    }

    fun parse(color: String): Color? {
        val lowered = color.lowercase()

        if (lowered == "random") {
            return Color(getRandomColor())
        }

        return colorMap[lowered]?.let { Color(it.toInt()) }
            ?: decodeOrNull(color)
    }
}
