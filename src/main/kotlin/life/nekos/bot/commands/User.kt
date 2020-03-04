package life.nekos.bot.commands

import life.nekos.bot.framework.Paginator
import life.nekos.bot.framework.annotations.CommandHelp
import life.nekos.bot.utils.*
import life.nekos.bot.utils.extensions.toReactionString
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Greedy
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.entities.Member
import java.time.Instant

class User : Cog {

    override fun localCheck(ctx: Context, command: CommandWrapper): Boolean {
        // Model.statsUp(command.name)
        return true
    }

    fun findUserById(ctx: Context, id: String): String {
        return ctx.jda.shardManager!!.getUserById(id)?.asTag ?: "Unknown User#0000"
    }

    @Command(aliases = ["lb", "top", "ranks"], description = "Global leaderboard. Category must be either \"nekos\" or \"levels\"")
    @CommandHelp("""
        category:
          nekos
          levels
    """)
    fun leaderboard(ctx: Context, category: String, page: Int = 1) {
        val data = when (category) {
            "nekos" -> Database.getTopNekos()
            "levels" -> Database.getTopExp()
            else -> {
                ctx.send(Formats.error("**Use `lb nekos` or `lb levels`**"))
                return
            }
        }

        val items = data.map { "${Formats.USER_EMOTE} **__Name__**: **${findUserById(ctx, it.id)}**\n" +
                "**%${Formats.NEKO_V_EMOTE} __Nekos__**: **${it.nekos}**\n\n"
        }

        val paginator = Paginator(items) {
            selectedPage = page
        }

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx))
            setTitle("${Formats.MAGIC_EMOTE} **Global leaderboard for Nekos** ${Formats.NEKO_C_EMOTE}")
            setDescription(paginator.display())
            setFooter("Page ${paginator.page()}/${paginator.maxPages}")
        }
    }

    @Command(aliases = ["rank", "exp"], description = "Shows your, or another user's profile.")
    fun profile(ctx: Context, @Greedy user: Member = ctx.member!!) {
        val targetUser = user.user

        if (targetUser.isBot) {
            ctx.send {
                setDescription("Bots don't have profiles ;p")
            }
            return
        }

        val profile = Database.getUser(user.id)
        ctx.message.addReaction(Formats.USER_EMOTE.toReactionString()).queue()

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx))
            setAuthor("Profile for ${targetUser.name}", targetUser.effectiveAvatarUrl, targetUser.effectiveAvatarUrl)
            setThumbnail(targetUser.effectiveAvatarUrl)
            setFooter("Profile for ${targetUser.name}", "https://media.discordapp.net/attachments/333742928218554368/374966699524620289/profile.png")
            setTimestamp(Instant.now())
            addField("${Formats.LEVEL_EMOTE} Level", "**${profile.level}**", false)
            addField("${Formats.MAGIC_EMOTE} Total Experience", "**${profile.exp}**", false)
            addField("${Formats.NEKO_V_EMOTE} Total Nekos Caught", "**${profile.nekosAll}**", false)
            addField("${Formats.NEKO_C_EMOTE} Current Nekos", "**${profile.nekos}**", false)
            addField("${Formats.DATE_EMOTE} Date Registered", "**${profile.registerDate}**", false)

            if (Checks.isDonor(user.id)) {
                addField("${Formats.PATRON_EMOTE} Donor", "**Commands unlocked**", false)
            }

            if (Checks.isDonorPlus(user.id)) {
                addField("${Formats.PATRON_EMOTE} Donor+", "**Commands, 2x exp and nekos unlocked**", false)
            }
        }
    }

    @Command(aliases = ["free", "catch"], description = "Releases one of your nekos for others to catch >.< (You cannot catch a neko you released)")
    fun release(ctx: Context) {
        val data = Database.getUser(ctx.author.id)

        if (data.nekos == 0L) {
            ctx.send("Nya~ You do not have any nekos to release nya~")
            return
        }

        data.update {
            nekos = data.nekos - 1
        }

        // release

        if (Checks.isMessageRemovable(ctx)) {
            ctx.message.delete().queue()
        }
    }

    @Command(description = "Send someone a neko image.", guildOnly = true)
    fun send(ctx: Context, @Greedy user: Member = ctx.member!!) {

    }

}