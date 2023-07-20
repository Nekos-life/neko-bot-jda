package life.nekos.bot.utils

import life.nekos.bot.Loader
import me.devoxin.flight.api.entities.PrefixProvider
import net.dv8tion.jda.api.entities.Message

class NekoPrefixProvider(private val isDebug: Boolean) : PrefixProvider {
    override fun provide(message: Message): List<String> {
        val selfUserId = message.jda.selfUser.id
        val prefixes = mutableListOf<String>()

        prefixes.add("<@$selfUserId> ")
        prefixes.add("<@!$selfUserId> ")

        if (isDebug) {
            prefixes.add("~~~")
        } else {
            if (message.isFromGuild) {
                val botPrefix = Database.getPrefix(message.guild.id) ?: "~"
                prefixes.add(botPrefix)
            } else {
                prefixes.add("~")
            }
        }

        return prefixes.toList()
    }
}
