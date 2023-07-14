package life.nekos.bot.utils.extensions

import net.dv8tion.jda.api.entities.emoji.Emoji

fun String.toEmoji(): Emoji {
    if (!this.contains(':')) {
        throw IllegalStateException("String does not appear to be a valid emote!")
    }

    return Emoji.fromFormatted(this)
}
