package life.nekos.bot.utils.extensions

import me.devoxin.flight.api.context.Context
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel

// Only in NSFW text channels, or DMs.
val Context.isNsfw: Boolean get() = (messageChannel as? TextChannel)?.isNSFW ?: (messageChannel is PrivateChannel)

fun Context.respondUnit(content: String) {
    respond(content)
}