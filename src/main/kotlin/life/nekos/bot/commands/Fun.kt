package life.nekos.bot.commands

import kotlinx.coroutines.future.await
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.annotations.CommandHelp
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Greedy
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User

class Fun : Cog {

    @Command(aliases = ["ava", "pfp", "avi"], description = "Shows your, or another user's avatar")
    @CommandHelp("""
        options:
          --dm : Sends the image to you privately.
          --new: Sends you a new avatar. You can specify "--nsfw" for a new NSFW avatar.
    """)
    suspend fun avatar(ctx: Context, user: User, @Greedy options: String = "") {
        val isDm = options.contains("--dm")
        val response = EmbedBuilder()
            .setColor(Colors.getEffectiveColor(ctx))

        if (options.contains("--new")) {
            if (options.contains("--nsfw")) {
                if (!isDm && (ctx.message.isFromGuild && !ctx.textChannel!!.isNSFW)) {
                    ctx.send("Nu, nya use this in an nsfw channel or add `--dm`")
                    return
                }
                response
                    .setDescription("${Formats.INFO_EMOTE} Hey ${ctx.author.name}! Here is a new nsfw avatar, nya~ ${Formats.randomCat()}")
                    .setImage(NekosLife.nsfwAvatar().await())
            } else {
                response
                    .setDescription("${Formats.INFO_EMOTE} Hey ${ctx.author.name}! Here is a new avatar, nya~ ${Formats.randomCat()}")
                    .setImage(NekosLife.avatar().await())
            }
        } else {
            val fsAvatarUrl = user.effectiveAvatarUrl + "?size=2048"
            response
                .setDescription("${Formats.INFO_EMOTE} Here is ${user.name}'s avatar, nya~ ${Formats.randomCat()}\n\n" +
                        "[**Link**]($fsAvatarUrl)")
                .setImage(fsAvatarUrl)
        }

        if (isDm) {
            val channel = ctx.author.openPrivateChannel().submit().await()
            channel.sendMessage(response.build()).submit().await()
            channel.close().queue()
        } else {
            ctx.messageChannel.sendMessage(response.build()).queue()
        }
    }

    @Command(aliases = ["8", "8ball", "8b"], description = "random why?")
    suspend fun ball(ctx: Context, @Greedy question: String) {
        if (!question.endsWith('?')) {
            ctx.send("\uD83D\uDEAB Nuu, nya! That doesn't look like a question? didn't anyone teach you punctuation??")
            return
        }

        val res = NekosLife.eightBall().await()
        val answer = res.getString("response")
        val imageUrl = res.getString("url")

        ctx.send {
            Colors.getEffectiveColor(ctx)
            setAuthor("Magic \uD83C\uDFB1", ctx.jda.getInviteUrl(), ctx.author.effectiveAvatarUrl)
            setDescription("❓: $question\nℹ: $answer")
            setImage(imageUrl)
        }
    }

}
