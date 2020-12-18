package life.nekos.bot.utils

import net.dv8tion.jda.api.requests.GatewayIntent

object IntentHelper {
    private val disabledIntents = setOf(
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGE_TYPING,
        GatewayIntent.GUILD_BANS,
        GatewayIntent.GUILD_EMOJIS,
        GatewayIntent.GUILD_INVITES,
        GatewayIntent.GUILD_MESSAGE_TYPING,
        GatewayIntent.GUILD_WEBHOOKS

        // Priviledged.
        //GatewayIntent.GUILD_MEMBERS,
        //GatewayIntent.GUILD_PRESENCES
    )

    val allIntents = GatewayIntent.ALL_INTENTS
    val disabledIntentsInt = GatewayIntent.getRaw(disabledIntents)
    val enabledIntentsInt = allIntents and disabledIntentsInt.inv()
    val enabledIntents = GatewayIntent.getIntents(enabledIntentsInt)
}
