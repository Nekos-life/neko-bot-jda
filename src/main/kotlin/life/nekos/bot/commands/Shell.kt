package life.nekos.bot.commands

import kotlinx.coroutines.delay
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.entities.Message

object Shell {

    suspend fun slideshow(ctx: Context, cycles: Int = 20, nextImage: suspend (Message, Int, Int) -> Unit) {
        val m = ctx.sendAsync("\u200b")

        for (i in 1 until cycles) {
            nextImage(m, i, cycles)
            delay(5000)
        }
    }

}
