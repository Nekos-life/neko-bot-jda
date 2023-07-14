package life.nekos.bot.commands.neko

import kotlinx.coroutines.future.await
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.extensions.send
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.context.MessageContext
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData
import java.awt.Color

class Neko : Cog {
    fun embed(
        ctx: Context, description: String, imageUrl: String,
        embedOptions: EmbedBuilder.() -> Unit = {}
    ) {
        ctx.send {
            setColor(Colors.getEffectiveColor(ctx))
            setDescription(description)
            setImage(imageUrl)
            apply(embedOptions)
        }
    }

    @Command(description = "Random fox girl", aliases = ["fox_girl", "fg"])
    fun fox(ctx: Context) {
        NekosLife.fox.thenAccept {
            embed(ctx, "Fox girls \\o/", it)
        }
    }

    @Command(description = "Random nekos owO", aliases = ["owo", "wew", "nyaaaa"], nsfw = true)
    fun lewd(ctx: Context) {
        NekosLife.lewd.thenAccept {
            embed(ctx, "Nekos owo", it) {
                setColor(Color.magenta)
            }
        }
    }

    @Command(description = "Random nekos owO")
    fun neko(ctx: Context) {
        NekosLife.neko.thenAccept {
            embed(ctx, "Nekos \\o/", it)
        }
    }

    @Command(description = "Mew!")
    fun nya(ctx: Context) {
        ctx.send("${ctx.author.asMention}, Mew!!~ ${Formats.cats.random()}")
    }

    @DonorOnly
    @Command(description = "Neko slideshow \\o//", aliases = ["lewdslideshow", "lss", "mewww", "o.o"], nsfw = true)
    suspend fun lewds(ctx: MessageContext) =
        Shell.slideshow(ctx) { m, cycle, total ->
            val image = NekosLife.lewd.await()

            m.editMessage(
                MessageEditData.fromEmbeds(
                    EmbedBuilder()
                        .setColor(Colors.getEffectiveColor(ctx))
                        .setDescription("$cycle of $total")
                        .setImage(image)
                        .build()
                )
            ).submit().await()
        }

    @Command(description = "Neko slideshow", aliases = ["slideshow", "ss", "mew", "nyaa"])
    suspend fun nekos(ctx: MessageContext) =
        Shell.slideshow(ctx) { m, cycle, total ->
            val image = NekosLife.neko.await()

            m.editMessage(
                MessageEditData.fromEmbeds(
                    EmbedBuilder()
                        .setColor(Colors.getRandomColor())
                        .setDescription("$cycle of $total")
                        .setImage(image)
                        .build()
                )
            ).submit().await()
        }
}
