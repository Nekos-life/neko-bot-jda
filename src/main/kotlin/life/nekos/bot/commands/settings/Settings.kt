package life.nekos.bot.commands.settings

import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Database
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.context.MessageContext
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel

class Settings : Cog {
    @Command(description = "Sets the guild prefix owo", guildOnly = true, userPermissions = [Permission.MANAGE_SERVER])
    fun prefix(ctx: MessageContext, newPrefix: String) {
        val settings = Database.getGuild(ctx.guild!!.id)

        settings.update {
            prefix = newPrefix
        }

        ctx.respond("Prefix set to `${ctx.cleanContent(newPrefix)}`, nya~")
    }

    @Command(description = "Sets the channel where nekos can be caught \\o", guildOnly = true, userPermissions = [Permission.MANAGE_SERVER])
    fun nekospawn(ctx: Context, channel: TextChannel?) {
        val settings = Database.getGuild(ctx.guild!!.id)

        settings.update {
            nekoChannel = channel?.id
        }

        val msg = channel?.let { "will now spawn in ${it.asMention}. I heard they're attracted to active channels" }
            ?: "will no longer spawn"
        ctx.respond("Nekos $msg, nya~")
    }

    @Command(
        description = "Shows the guild config info", guildOnly = true,
        userPermissions = [Permission.MANAGE_SERVER]
    )
    fun status(ctx: Context) {
        val settings = Database.getGuild(ctx.guild!!.id)
        val nekoChannel = settings.nekoChannel?.let { ctx.guild!!.getTextChannelById(it)?.asMention }
            ?: "None"

        ctx.respond {
            setColor(Colors.getEffectiveColor(ctx))
            setTitle("Guild Settings for ${ctx.guild!!.name}")
            addField("Prefix", settings.prefix ?: "Default", true)
            addField("Neko Channel", nekoChannel, true)
            addBlankField(true)
        }
    }
}
