package life.nekos.bot.commands

import life.nekos.bot.utils.Database
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.toReactionString
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Greedy
import me.devoxin.flight.arguments.Optional
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.entities.Member
import java.time.Instant

class User : Cog {

    override fun localCheck(ctx: Context, command: CommandWrapper): Boolean {
        // Model.statsUp(command.name)
        return true
    }

    @Command(aliases = ["lb", "top", "ranks"], description = "Global leaderboard. Category can be either \"nekos\" or \"levels\"")
    fun leaderboard(ctx: Context, category: String) {

    }

    @Command(aliases = ["rank", "exp"], description = "Shows your, or another user's profile.")
    fun profile(ctx: Context, @Greedy @Optional user: Member?) {
        val target = user ?: ctx.member
        val targetUser = target!!.user

        if (targetUser.isBot) {
            return ctx.embed {
                setDescription("Bots don't have profiles ;p")
            }
        }

        val profile = Database.getUser(target.id)
        ctx.message.addReaction(Formats.USER_EMOTE.toReactionString()).queue()

        ctx.embed {
            //setColor() // effective
            setAuthor("Profile for ${targetUser.name}", targetUser.effectiveAvatarUrl, targetUser.effectiveAvatarUrl)
            setThumbnail(targetUser.effectiveAvatarUrl)
            setFooter("Profile for ${targetUser.name}", "https://media.discordapp.net/attachments/333742928218554368/374966699524620289/profile.png")
            setTimestamp(Instant.now())
            addField("${Formats.LEVEL_EMOTE} Level", "**${profile.level}**", false)
            addField("${Formats.MAGIC_EMOTE} Total Experience", "**${profile.exp}**", false)
            addField("${Formats.NEKO_V_EMOTE} Total Nekos Caught", "**${profile.nekosAll}**", false)
            addField("${Formats.NEKO_C_EMOTE} Current Nekos", "**${profile.nekos}**", false)
            addField("${Formats.DATE_EMOTE} Date Registered", "**${profile.registerDate}**", false)
        }

        // isDonor
        // isDonorPlus
    }

    @Command(aliases = ["free", "catch"], description = "Releases one of your nekos for others to catch >.< (You cannot catch a neko you released)")
    fun release(ctx: Context, @Greedy @Optional user: Member?) {
        val target = user ?: ctx.member

    }

    @Command(description = "Send someone a neko image.", guildOnly = true)
    fun send(ctx: Context, @Greedy @Optional user: Member?) {
        val target = user ?: ctx.member

    }

}