package life.nekos.bot.utils

import java.lang.reflect.Method

fun String.toReactionString(): String {
    if (!this.contains(':')) {
        throw IllegalStateException("String does not appear to be a valid emote!")
    }

    return this.substringAfter(':').substringBefore('>')
}
