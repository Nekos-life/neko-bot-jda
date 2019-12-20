package life.nekos.bot.utils

import java.lang.reflect.Method

fun String.toReactionString(): String {
    if (!this.contains(':')) {
        throw IllegalStateException("String does not appear to be a valid emote!")
    }

    return this.substringAfter(':').substringBefore('>')
}

fun <T : Annotation> Method.getAnnotationOrNull(annotation: Class<T>): T? {
    if (this.isAnnotationPresent(annotation)) {
        return this.getAnnotation(annotation)
    }

    return null
}
