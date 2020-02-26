package life.nekos.bot.utils

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
