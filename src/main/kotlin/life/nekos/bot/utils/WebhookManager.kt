package life.nekos.bot.utils


import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.exception.HttpException
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import life.nekos.bot.Config
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color
import java.time.Instant

fun MessageEmbed.toWebhookEmbed(): WebhookEmbed {
    return WebhookEmbed(
        this.timestamp,
        this.colorRaw,
        this.description,
        this.thumbnail?.url,
        this.image?.url,
        if (this.footer != null) WebhookEmbed.EmbedFooter(this.footer!!.text ?: "", this.footer!!.iconUrl) else null,
        WebhookEmbed.EmbedTitle(this.title ?: "", this.url),
        if (this.author != null) WebhookEmbed.EmbedAuthor(
            this.author!!.name ?: "",
            this.author!!.iconUrl,
            this.author!!.url
        ) else null,
        this.fields.map { WebhookEmbed.EmbedField(it.isInline, it.name ?: "", it.value ?: "") }
    )
}

object WebhookManager {
    private val log: Logger = LoggerFactory.getLogger(WebhookManager::class.java)

    private val shardHook = WebhookClientBuilder(Config["ready_wh"]).build()
    private val leaveHook = WebhookClientBuilder(Config["guilds_wh"]).build()
    private val joinHook = WebhookClientBuilder(Config["guilds_wh"]).build()

    private fun safeSend(whClient: WebhookClient, avatar: String?, builder: EmbedBuilder.() -> Unit) {
        val message = WebhookMessageBuilder()
            .setUsername("Neko")
            .setAvatarUrl(avatar)
            .addEmbeds(
                EmbedBuilder()
                    .apply {
                        setColor(Color.magenta)
                        setAuthor("Neko", avatar, avatar)
                        setTimestamp(Instant.now())
                    }
                    .apply(builder)
                    .build()
                    .toWebhookEmbed()
            )
            .build()

        try {
            whClient.send(message)

        } catch (e: HttpException) {
            if (!e.localizedMessage.contains("Unknown Webhook")) {
                log.error("Failed to send message to webhook", e)
            }
        }
    }

    fun sendShard(avatar: String?, builder: EmbedBuilder.() -> Unit) = safeSend(shardHook, avatar, builder)

    fun sendLeave(builder: EmbedBuilder.() -> Unit) = safeSend(leaveHook, null, builder)

    fun sendJoin(builder: EmbedBuilder.() -> Unit) = safeSend(joinHook, null, builder)
}
