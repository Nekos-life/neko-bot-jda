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
    override fun localCheck(ctx: Context, command: CommandFunction): Boolean {
        // Model.statsUp(command.name)
        return true
    }

    private fun findUserById(ctx: Context, id: String): String {
        if (NekoBot.simpleLbCache.containsKey(id)) {
            return NekoBot.simpleLbCache[id]!!
        }

        val tag = ctx.jda.shardManager!!.retrieveUserById(id).submit().get()?.name ?: "Unknown User"
        NekoBot.simpleLbCache[id] = tag
        return tag
    }

    @Command(
        aliases = ["lb", "top", "ranks"],
        description = "Global leaderboard. Category must be either \"nekos\" or \"levels\""
    )
    @CommandHelp(
        """
        category:
          nekos
          levels
    """
    )
    suspend fun leaderboard(ctx: Context, category: String, page: Int = 1) {
        val items = when (category) {
            "nekos" -> Database.getTopNekos().map {
                "\n${Formats.USER_EMOTE} **__Name__**: **${findUserById(ctx, it.id)}**\n**${Formats.NEKO_V_EMOTE} __Nekos__**: **${it.nekos}**\n"
            }
            "levels" -> Database.getTopExp().map {
                "\n${Formats.USER_EMOTE} **__Name__**: **${findUserById(ctx, it.id)}**\n${Formats.MAGIC_EMOTE} **__Level__**: **${it.level}** \n" +
                    "${Formats.LEVEL_EMOTE} **__Experience__**: **${it.exp}**\n"
            }
            else -> return ctx.respondUnit(Formats.error("**Use `lb nekos` or `lb levels`**"))
        }

        val paginator = Paginator(items) {
            selectedPage = page
        }

        ctx.asSlashContext?.deferAsync()

        val link = WumpDump.paste(
            items.toString()
                .replace(Formats.NEKO_V_EMOTE, "")
                .replace(Formats.USER_EMOTE, "")
                .replace(Formats.LEVEL_EMOTE, "")
                .replace(Formats.MAGIC_EMOTE, "")
                .replace("_", "")
                .replace("*", "")
        ).await()

        ctx.respond {
            embed {
                setColor(Colors.getEffectiveColor(ctx))
                setTitle("${Formats.MAGIC_EMOTE} **Global leaderboard for Nekos** ${Formats.NEKO_C_EMOTE}", link)
                setDescription(paginator.display())
                setFooter("Page ${paginator.page()}/${paginator.maxPages}")
                setTimestamp(Instant.now())
            }
        }
    }

    @Command(aliases = ["rank", "exp"], description = "Shows your, or another user's profile.", guildOnly = true)
    fun profile(ctx: Context, @Greedy user: Member = ctx.member!!) {
        val targetUser = user.user.takeIf { !it.isBot }
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
