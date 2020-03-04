package life.nekos.bot.utils

import me.devoxin.flight.api.Context
import java.util.concurrent.CompletableFuture


fun String.toReactionString(): String {
    if (!this.contains(':')) {
        throw IllegalStateException("String does not appear to be a valid emote!")
    }

    return this.substringAfter(':').substringBefore('>')
}

fun <T> CompletableFuture<T>.thenException(block: (Throwable) -> Unit): CompletableFuture<T> {
    exceptionally {
        block(it)
        return@exceptionally null
    }

    return this
}

fun Context.isNsfw(): Boolean {
    return this.textChannel?.isNSFW ?: this.privateChannel != null
    // Only in NSFW text channels, or DMs.
}
