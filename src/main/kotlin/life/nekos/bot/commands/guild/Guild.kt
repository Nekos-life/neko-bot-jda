package life.nekos.bot.commands.guild

import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.WumpDump
import life.nekos.bot.utils.extensions.send
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.context.MessageContext
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.streams.toList

class Guild : Cog {
    @Command(
        aliases = ["nsfw", "toggle"], description = "Toggles the current channel's NSFW setting", guildOnly = true,
        botPermissions = [Permission.MANAGE_CHANNEL], userPermissions = [Permission.MANAGE_CHANNEL]
    )
    fun nsfwtoggle(ctx: Context) {
        val tc = ctx.messageChannel as? TextChannel
            ?: return ctx.send("Nya, I can't toggle the NSFW status of this channel!")

        val newSetting = !tc.isNSFW
        tc.manager.setNSFW(newSetting).queue {
            val str = if (newSetting) "enabled" else "disabled"
            ctx.send("Nya, I have $str NSFW on this channel! ${Formats.randomCat()}")
        }
    }

    @DonorOnly
    @Command(
        aliases = ["rc"], description = "Changes all hoisted roles to a random color", guildOnly = true,
        botPermissions = [Permission.MANAGE_ROLES], userPermissions = [Permission.MANAGE_ROLES]
    )
    suspend fun recolor(ctx: MessageContext) {
        ctx.sendAsync("This command will change the color of all hoisted roles to something random! Do you want to continue? (yes/no)")

        try {
            ctx.waitFor(
                MessageReceivedEvent::class.java,
                { it.author.idLong == ctx.author.idLong && it.channel.idLong == ctx.messageChannel.idLong && it.message.contentRaw.lowercase() == "yes" },
                60000
            )
        } catch (ex: TimeoutException) {
            return
        }

        val modifiableRoles = ctx.guild!!.roleCache.stream()
            .filter(Role::isHoisted)
            .filter(ctx.guild!!.selfMember::canInteract)
            .toList()

        val msg = ctx.sendAsync("This may take a while, nya~")
        val tasks = modifiableRoles.map { it.manager.setColor(Colors.getRandomColor()).submit() }.toTypedArray()

        CompletableFuture.allOf(*tasks).handle { _, _ ->
            val updated = tasks.filter { !it.isCompletedExceptionally }.size
            msg.editMessage("Done nya! I have set the color of $updated/${tasks.size} roles \\o/").queue()
        }
    }

    @Command(aliases = ["r"], description = "Shows server role info", guildOnly = true)
    fun roles(ctx: Context, role: Role?) {
        if (role == null) {
            return ctx.send("You must provide the name of the role whose information you want to see, nya~")
        }

        val color = role.color
        val hexStr = color?.let { String.format("%02x%02x%02x", it.red, it.green, it.blue) } ?: "1FFFFF"
        val permissions = role.permissionsExplicit.joinToString("\n") { it.getName() }
        val permissionsUrl = WumpDump.paste(permissions).get(5, TimeUnit.SECONDS)

        ctx.send {
            setColor(role.color)
            setTitle("${role.name} (${role.id}) | ${role.asMention}")
            addField("Color", "#$hexStr", true)
            addField("Hoisted", role.isHoisted.toString(), true)
            addField("Managed", role.isManaged.toString(), true)
            addField("Mentionable", role.isMentionable.toString(), true)
            addField("Permissions", "[Click to view]($permissionsUrl)", true)
        }
    }
}
