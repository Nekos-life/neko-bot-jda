package life.nekos.bot.commands.`fun`

import kotlinx.coroutines.future.await
import life.nekos.bot.apis.AlexFlipnote
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.annotations.CommandHelp
import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Database
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.extensions.respondUnit
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Describe
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.context.MessageContext
import me.devoxin.flight.api.entities.Cog
import me.devoxin.flight.internal.parsers.MemberParser
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.awt.Color
import java.awt.Font
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class Fun : Cog {
    @Command(aliases = ["ava", "pfp", "avi"], description = "Shows your, or another user's avatar")
    @CommandHelp(
        """
        options:
          --dm : Sends the image to you privately.
          --new: Sends you a new avatar.
    """
    )
    suspend fun avatar(ctx: Context, user: User, @Describe("Specify '--dm' for DM, '--new' for a new avatar.") @Greedy options: String = "") {
        val isDm = options.contains("--dm")
        val response = EmbedBuilder().setColor(Colors.getEffectiveColor(ctx))

        if (options.contains("--new")) {
            response.setDescription("${Formats.INFO_EMOTE} Hey ${ctx.author.name}! Here is a new avatar, nya~ ${Formats.randomCat()}")
                .setImage(NekosLife.avatar.await())
        } else {
            val fsAvatarUrl = user.effectiveAvatarUrl + "?size=2048"
            response.setDescription("${Formats.INFO_EMOTE} Here is ${user.name}'s avatar, nya~ ${Formats.randomCat()}\n\n[**Link**]($fsAvatarUrl)")
                .setImage(fsAvatarUrl)
        }

        if (isDm) {
            ctx.asSlashContext?.deferAsync()

            return ctx.author.openPrivateChannel()
                .flatMap { it.sendMessage(MessageCreateData.fromEmbeds(response.build())) }
                .flatMap { it.channel.delete() }
                .queue()
        }

        ctx.respond {
            setEmbeds(response.build())
        }
    }

    @Command(aliases = ["8", "8ball", "8b"], description = "random why?")
    suspend fun ball(ctx: Context, @Describe("The question to ask the 8ball.") @Greedy question: String) {
        if (!question.endsWith('?')) {
            return ctx.respondUnit("\uD83D\uDEAB Nuu, nya! That doesn't look like a question? didn't anyone teach you punctuation??")
        }

        ctx.asSlashContext?.deferAsync()
        val res = NekosLife.eightBall.await()
        val answer = res.getString("response")
        val imageUrl = res.getString("url")

        ctx.respond {
            embed {
                Colors.getEffectiveColor(ctx)
                setAuthor("Magic \uD83C\uDFB1", ctx.jda.getInviteUrl(), ctx.author.effectiveAvatarUrl)
                setDescription("❓: $question\nℹ: $answer")
                setImage(imageUrl)
            }
        }
    }

    @Command(description = "random coffee ^^")
    suspend fun coffee(ctx: Context) {
        ctx.asSlashContext?.deferAsync()

        val coffee = AlexFlipnote.coffee().await()

        ctx.respond {
            embed {
                setColor(Colors.getEffectiveColor(ctx))
                setDescription("coffee \\o/")
                setImage(coffee)
                setFooter("API provided by AlexFlipnote")
            }
        }
    }

    @Command(aliases = ["colour"], description = "See information about a color")
    suspend fun color(ctx: MessageContext, @Greedy color: String) {
        val parsedColor = memberConverter.parse(ctx, color)
            .map { it.color }
            .orElseGet { Colors.parse(color) }
            ?: return ctx.respondUnit("Nu nya, that doesn't look like a color to me. Try a hex `#0000ff`, or a name `Blue`")

        ctx.asSlashContext?.deferAsync()
        val hex = String.format("%02x%02x%02x", parsedColor.red, parsedColor.green, parsedColor.blue)
        val info = AlexFlipnote.color(hex).await()

        ctx.respond {
            embed {
                setColor(parsedColor)
                setDescription(Formats.info("Color info for $color"))
                setImage(info.imageUrl)
                addField("Name", info.name, true)
                addField("Hex", info.hex, true)
                addField("RGB", info.rgb, true)
                setFooter("API provided by AlexFlipnote")
            }
        }
    }

    @Command(description = "Cuddle someone \\o/", guildOnly = true)
    suspend fun cuddle(ctx: Context, @Greedy who: User?) {
        genericActionCommand(ctx, who, "cuddle", NekosLife.cuddle)
    }

    @Command(aliases = ["dym", "did_you_mean"], description = "Did you mean? \"something | else\"")
    suspend fun didyoumean(ctx: Context, @Greedy dym: String) {
        if (!dym.contains('|')) {
            return ctx.respondUnit("You need to separate the top and bottom text with `|` nya~")
        }

        ctx.asSlashContext?.deferAsync()
        val (top, bottom) = dym.split("|")
        val result = AlexFlipnote.didYouMean(top, bottom).await()
        ctx.respond(FileUpload.fromData(result, "didyoumean.png"))
    }

    @Command(description = "Flip a coin", guildOnly = true, developerOnly = true)
    fun flip(ctx: Context, @Describe("'heads' or 'tails'.") side: String, bet: Int) {
        if (!sides.contains(side.lowercase())) {
            return ctx.respondUnit("You must pick heads or tails, nya~")
        }

        if (bet < 1) {
            return ctx.respondUnit("You must bet higher than 0 nya~")
        }

        val data = Database.getUser(ctx.author.id)

        if (data.nekos < 1) {
            return ctx.respondUnit("You don't have enough to bet nya~")
        }

        if (bet > data.nekos) {
            return ctx.respondUnit("You only have **${data.nekos}** nekos nya~")
        }

        val selectedSide = sides.indexOf(side.lowercase()) + 1
        val roll = (1..2).random()

        if (roll == selectedSide) {
            val betRounded = (bet.toDouble() * 0.4).roundToInt()
            ctx.respond("The coin landed on ${sides[roll - 1]} nya~\nYou won **$betRounded** nekos owo")
            data.update { nekos += betRounded }
        } else {
            ctx.respond("The coin landed on ${sides[roll - 1]} nya~\nYou lost **$bet** nekos (=‘ｘ‘=)")
            data.update { nekos -= bet }
        }
    }

    @Command(description = "Hug someone \\o/", guildOnly = true)
    suspend fun hug(ctx: Context, @Greedy who: User?) {
        genericActionCommand(ctx, who, "hug", NekosLife.hug)
    }

    @Command(description = "Kiss someone \\o/", guildOnly = true)
    suspend fun kiss(ctx: Context, @Greedy who: User?) {
        genericActionCommand(ctx, who, "kiss", NekosLife.kiss)
    }

    @Command(description = "Pat someone \\o/", guildOnly = true)
    suspend fun pat(ctx: Context, @Greedy who: User?) {
        genericActionCommand(ctx, who, "pat", NekosLife.pat)
    }

    @Command(description = "Pat someone \\o/", guildOnly = true)
    suspend fun slap(ctx: Context, @Greedy who: User?) {
        genericActionCommand(ctx, who, "slap", NekosLife.slap)
    }

    @Command(aliases = ["huh", "hmmmmm"], description = "random why?")
    suspend fun why(ctx: Context) {
        ctx.asSlashContext?.deferAsync()

        val nekoWhy = NekosLife.why.await()

        ctx.respond {
            embed {
                setColor(Colors.getEffectiveColor(ctx))
                setAuthor("Why??", ctx.jda.getInviteUrl(), ctx.jda.selfUser.effectiveAvatarUrl)
                setDescription(nekoWhy)
            }
        }
    }

    @Command(aliases = ["gecg"], description = "random gecg owO", nsfw = true)
    suspend fun meme(ctx: Context) {
        ctx.asSlashContext?.deferAsync()

        val nekoMeme = NekosLife.meme.await()

        ctx.respond {
            embed {
                setColor(Colors.getEffectiveColor(ctx))
                setDescription("owo")
                setImage(nekoMeme)
            }
        }
    }

    private suspend fun genericActionCommand(
        ctx: Context, who: User?, action: String,
        img: CompletableFuture<String>
    ) {
        if (who == null) {
            return ctx.respondUnit("Who do you want to $action?? ${Formats.NEKO_C_EMOTE}")
        }

        if (who.idLong == ctx.author.idLong) {
            return ctx.respondUnit("oh? why you want to $action yourself? Find a friend nya~")
        }

        if (who.idLong == ctx.jda.selfUser.idLong) {
            return ctx.respondUnit("Nyaaaaaaaaaa, nu dun $action mee~")
        }

        ctx.asSlashContext?.deferAsync()

        val imgUrl = img.await()

        ctx.respond {
            embed {
                setColor(Colors.getEffectiveColor(ctx))
                setDescription("${who.asMention}, you got a $action from ${ctx.author.name} ${Formats.randomCat()}")
                setImage(imgUrl)
            }
        }
    }

    @DonorOnly
    @Command(aliases = ["lf", "sf"], description = "You want sum fuk?")
    suspend fun sumfuk(ctx: Context, who: User) {
        when (who.idLong) {
            ctx.jda.selfUser.idLong -> return ctx.respondUnit("Nu nya, your tail's not big enough for me~ >.<")
            ctx.author.idLong -> return ctx.respondUnit("Nu nya, I don't think you need to ask to fuk yourself~")
        }

        ctx.think().await()
        val bg = ImageIO.read(this.javaClass.getResource("/sf.jpg"))
        val font = Font("Whitney", Font.BOLD, 30)
        val g = bg.createGraphics().also {
            it.color = Color.BLACK
            it.font = font
        }

        g.drawString(who.name, 228, 116)
        g.drawString(ctx.author.name, 248, 165)
        g.dispose()

        ByteArrayOutputStream().use {
            ImageIO.setUseCache(false)
            ImageIO.write(bg, "png", it)
            ctx.respond(FileUpload.fromData(it.toByteArray(), "sumfuk.png"))
        }
    }

    companion object {
        private val memberConverter = MemberParser()
        private val sides = listOf("heads", "tails")
    }
}
