package life.nekos.bot.commands

import kotlinx.coroutines.future.await
import life.nekos.bot.apis.AlexFlipnote
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.annotations.CommandHelp
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Greedy
import me.devoxin.flight.models.Attachment
import me.devoxin.flight.models.Cog
import me.devoxin.flight.parsers.MemberParser
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
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
                    return ctx.send("Nu, nya use this in an nsfw channel or add `--dm`")
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
            return ctx.send("\uD83D\uDEAB Nuu, nya! That doesn't look like a question? didn't anyone teach you punctuation??")
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

    @Command(description = "random coffee ^^")
    suspend fun coffee(ctx: Context) {
        val coffee = AlexFlipnote.coffee().await()

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx))
            setDescription("coffee \\o/")
            setImage(coffee)
            setFooter("API provided by AlexFlipnote")
        }
    }

    @Command(aliases = ["colour"], description = "See information about a color")
    suspend fun color(ctx: Context, @Greedy color: String) {
        val m = memberConverter.parse(ctx, color)
        val parsedColor = m.map { it.color }
            .orElseGet { Colors.parse(color) }
            ?: return ctx.send("Nu nya, that doesn't look like a color to me. Try a hex `#0000ff`, or a name `Blue`")

        val hex = String.format("%02x%02x%02x", parsedColor.red, parsedColor.green, parsedColor.blue)
        val info = AlexFlipnote.color(hex).await()

        ctx.send {
            setColor(parsedColor)
            setDescription(Formats.info("Color info for $color"))
            setImage(info.imageUrl)
            addField("Name", info.name, true)
            addField("Hex", info.hex, true)
            addField("RGB", info.rgb, true)
            setFooter("API provided by AlexFlipnote")
        }
    }

    @Command(description = "Cuddle someone \\o/", guildOnly = true)
    suspend fun cuddle(ctx: Context, @Greedy who: Member?) {
        if (who == null) {
            return ctx.send("Who do you want to cuddle?? ${Formats.NEKO_C_EMOTE}")
        }

        if (who.idLong == ctx.author.idLong) {
            return ctx.send("oh? why you want to cuddle yourself? Find a friend nya~")
        }

        if (who.idLong == ctx.jda.selfUser.idLong) {
            return ctx.send("Nyaaaaaaaaaa, nu dun touch mee~")
        }

        val cuddleImage = NekosLife.cuddle().await()

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx))
            setDescription("${who.effectiveName}, you got cuddles from ${ctx.author.name} ${Formats.randomCat()}")
            setImage(cuddleImage)
        }
    }

    @Command(aliases = ["dym", "did_you_mean"], description = "Did you mean? \"something | else\"")
    suspend fun didyoumean(ctx: Context, @Greedy dym: String) {
        if (!dym.contains('|')) {
            return ctx.send("You need to separate the top and bottom text with `|` nya~")
        }

        val (top, bottom) = dym.split("|")
        val result = AlexFlipnote.didYouMean(top, bottom).await()

        ctx.send(Attachment.Companion.from(result, "didyoumean.png"))
    }

    companion object {
        private val memberConverter = MemberParser()
    }

}
