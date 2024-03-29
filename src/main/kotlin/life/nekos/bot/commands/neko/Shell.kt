package life.nekos.bot.commands.neko

import kotlinx.coroutines.delay
import me.devoxin.flight.api.context.MessageContext
import net.dv8tion.jda.api.entities.Message

object Shell {
    suspend fun slideshow(ctx: MessageContext, cycles: Int = 20, nextImage: suspend (Message, Int, Int) -> Unit) {
        val m = ctx.sendAsync("\u200b")

        for (i in 0 until cycles) {
            nextImage(m, i+1, cycles)
            delay(5000)
        }
    }
}
