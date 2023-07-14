package life.nekos.bot.utils.extensions

import me.devoxin.flight.api.context.Context
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.utils.messages.MessageCreateData

// Only in NSFW text channels, or DMs.
val Context.isNsfw: Boolean get() = (messageChannel as? TextChannel)?.isNSFW ?: (messageChannel is PrivateChannel)

fun Context.send(embedBuilder: EmbedBuilder.() -> Unit) {
    val embed = EmbedBuilder().apply(embedBuilder).build()
    respond(MessageCreateData.fromEmbeds(embed))
}