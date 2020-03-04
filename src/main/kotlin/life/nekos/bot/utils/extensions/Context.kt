package life.nekos.bot.utils.extensions

import me.devoxin.flight.api.Context

fun Context.isNsfw(): Boolean {
    return this.textChannel?.isNSFW ?: this.privateChannel != null
    // Only in NSFW text channels, or DMs.
}
