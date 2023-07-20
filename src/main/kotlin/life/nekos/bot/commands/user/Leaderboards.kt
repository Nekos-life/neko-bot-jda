package life.nekos.bot.commands.user

import kotlinx.coroutines.future.await
import life.nekos.bot.NekoBot
import life.nekos.bot.entities.User
import life.nekos.bot.framework.Paginator
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Database
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.WumpDump
import life.nekos.bot.utils.extensions.respondUnit
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Describe
import me.devoxin.flight.api.annotations.SubCommand
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.entities.Cog
import java.time.Instant

class Leaderboards : Cog {
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
        description = "View leaderboard rankings."
    )
    fun leaderboard(ctx: Context) {
        ctx.respond("You need to specify what leaderboards you want to see, nya~\nYou can pick from `nekos` or `levels`." +
            "\n\nFor example~\n`${ctx.asMessageContext?.trigger?.replace(ctx.jda.selfUser.asMention, "@${ctx.jda.selfUser.name}") ?: "/"}leaderboard nekos`")
    }

    @SubCommand(description = "View leaderboards for nekos")
    suspend fun nekos(ctx: Context, @Describe("The page of results to view.") page: Int = 1) {
        showLeaderboards(ctx, page, Database.getTopNekos()) {
            "\n${Formats.USER_EMOTE} **__Name__**: **${findUserById(ctx, it.id)}**\n**${Formats.NEKO_V_EMOTE} __Nekos__**: **${it.nekos}**\n"
        }
    }

    @SubCommand(description = "View leaderboards for levels")
    suspend fun levels(ctx: Context, @Describe("The page of results to view.") page: Int = 1) {
        showLeaderboards(ctx, page, Database.getTopExp()) {
            "\n${Formats.USER_EMOTE} **__Name__**: **${findUserById(ctx, it.id)}**\n${Formats.MAGIC_EMOTE} **__Level__**: **${it.level}** \n" +
                "${Formats.LEVEL_EMOTE} **__Experience__**: **${it.exp}**\n"
        }
    }

    private suspend fun showLeaderboards(ctx: Context, page: Int, entities: List<User>, mapper: (User) -> String) {
        ctx.think().await()

        val items = entities.map(mapper)
        val paginator = Paginator(items) { selectedPage = page }

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
}
