package life.nekos.bot.utils.extensions

import java.util.concurrent.CompletableFuture

fun <T> CompletableFuture<T>.thenException(block: (Throwable) -> Unit): CompletableFuture<T> {
    exceptionally {
        block(it)
        return@exceptionally null
    }

    return this
}
