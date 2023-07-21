package life.nekos.bot.commands.user

import kotlinx.coroutines.future.await
import life.nekos.bot.NekoBot
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.Paginator
import life.nekos.bot.framework.annotations.CommandHelp
import life.nekos.bot.utils.*
import life.nekos.bot.utils.extensions.respondUnit
import life.nekos.bot.utils.extensions.toEmoji
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.context.MessageContext
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.time.Instant

class User : Cog {
    @Command(aliases = ["rank", "exp"], description = "Shows your, or another user's profile.", guildOnly = true)
    fun profile(ctx: Context, @Greedy user: User = ctx.author) {
        val targetUser = user.takeIf { !it.isBot }
            ?: return ctx.respondUnit("Bots don't have profiles ;p")

        val profile = Database.getUser(user.id)
        ctx.asMessageContext?.message?.addReaction(Formats.USER_EMOTE.toEmoji())?.queue()

        ctx.respond {
            embed {
                setColor(Colors.getEffectiveColor(ctx))
                setAuthor(
                    "Profile for ${targetUser.name}",
                    targetUser.effectiveAvatarUrl,
                    targetUser.effectiveAvatarUrl
                )
                setThumbnail(targetUser.effectiveAvatarUrl)
                setFooter(
                    "Profile for ${targetUser.name}",
                    "https://media.discordapp.net/attachments/333742928218554368/374966699524620289/profile.png"
                )
                setTimestamp(Instant.now())
                addField("${Formats.LEVEL_EMOTE} Level", "**${profile.level}**", false)
                addField("${Formats.MAGIC_EMOTE} Total Experience", "**${profile.exp}**", false)
                addField("${Formats.NEKO_V_EMOTE} Total Nekos Caught", "**${profile.nekosAll}**", false)
                addField("${Formats.NEKO_C_EMOTE} Current Nekos", "**${profile.nekos}**", false)
                addField("${Formats.DATE_EMOTE} Date Registered", "**${profile.registerDate}**", false)

                if (Checks.isDonor(user.idLong)) {
                    addField("${Formats.PATRON_EMOTE} Donor", "**Commands unlocked**", false)
                }

                if (Checks.isDonorPlus(user.idLong)) {
                    addField("${Formats.PATRON_EMOTE} Donor+", "**Commands, 2x exp and nekos unlocked**", false)
                }
            }
        }
    }

    @Command(
        aliases = ["free", "catch"],
        description = "Releases one of your nekos for others to catch >.< (You cannot catch a neko you released)",
        guildOnly = true
    )
    fun release(ctx: Context) {
        val data = Database.getUser(ctx.author.id).takeIf { it.nekos > 0 }
            ?: return ctx.respondUnit("Nya~ You do not have any nekos to release nya~")

        data.update { nekos-- }

        Send(ctx.guildChannel!!, false).neko(ctx.author.idLong)

        if (Checks.isMessageRemovable(ctx)) {
            ctx.asMessageContext?.message?.delete()?.queue()
        }
    }

    @Command(description = "Send someone a neko image.", guildOnly = true)
    suspend fun sendNeko(ctx: Context, type: String, @Greedy user: User = ctx.author) {
        if (user.isBot) {
            return ctx.respondUnit("Nu nya, bots can't appreciate pictures of nekos~")
        }

        ctx.asSlashContext?.deferAsync()
        val image = NekosLife.neko.await()

        val embed = EmbedBuilder().setColor(Colors.getEffectiveColor(ctx))
            .setTitle("hey ${user.name}, ${ctx.author.name} has sent you a $type")
            .setDescription(Formats.NEKO_C_EMOTE)
            .setImage(image)
            .build()

        user.openPrivateChannel()
            .flatMap { it.sendMessage(MessageCreateData.fromEmbeds(embed)) }
            .flatMap { it.delete() }
            .queue(
                { ctx.respond("Good job ${ctx.author.asMention}") },
                { ctx.respond("${user.name} has me blocked or their filter turned on \uD83D\uDD95") }
            )
    }
}
