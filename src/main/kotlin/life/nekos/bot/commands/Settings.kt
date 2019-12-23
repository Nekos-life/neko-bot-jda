package life.nekos.bot.commands

import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Database
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel

class Settings : Cog {

    @Command(description = "Sets the guild prefix owo", guildOnly = true,
        userPermissions = [Permission.MANAGE_SERVER])
    fun prefix(ctx: Context, newPrefix: String) {
        val settings = Database.getGuild(ctx.guild!!.id)

        settings.update {
            prefix = newPrefix
        }

        ctx.send("Prefix set to `${ctx.cleanContent(newPrefix)}`, nya~")
    }

    @Command(description = "Sets the channel where nekos can be caught \\o", guildOnly = true,
        userPermissions = [Permission.MANAGE_SERVER])
    fun nekos(ctx: Context, channel: TextChannel?) {
        val settings = Database.getGuild(ctx.guild!!.id)

        settings.update {
            nekoChannel = channel?.id
        }

        val msg = channel?.let { "will now spawn in ${it.asMention}. I heard they're attracted to active channels" }
            ?: "will no longer spawn"
        ctx.send("Nekos $msg, nya~")
    }

    @Command(description = "Shows the guild config info", guildOnly = true,
        userPermissions = [Permission.MANAGE_SERVER])
    fun status(ctx: Context) {
        val settings = Database.getGuild(ctx.guild!!.id)
        val nekoChannel = settings.nekoChannel?.let { ctx.guild!!.getTextChannelById(it)?.asMention }
            ?: "None"

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx))
            setTitle("Guild Settings for ${ctx.guild!!.name}")
            addField("Prefix", settings.prefix ?: "Default", true)
            addField("Neko Channel", nekoChannel, true)
            addBlankField(true)
        }
    }

}
