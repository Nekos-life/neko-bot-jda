package life.nekos.bot.utils

import net.dv8tion.jda.api.requests.GatewayIntent

object IntentHelper {
    private val disabledIntents = setOf(
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGE_TYPING,
        GatewayIntent.GUILD_MODERATION,
        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
        GatewayIntent.GUILD_INVITES,
        GatewayIntent.GUILD_MESSAGE_TYPING,
        GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.SCHEDULED_EVENTS,
        GatewayIntent.GUILD_WEBHOOKS,

        // Privileged.
        GatewayIntent.MESSAGE_CONTENT,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES
    )

    val allIntents = GatewayIntent.ALL_INTENTS
    val disabledIntentsInt = GatewayIntent.getRaw(disabledIntents)
    val enabledIntentsInt = allIntents and disabledIntentsInt.inv()
    val enabledIntents = GatewayIntent.getIntents(enabledIntentsInt)
}
