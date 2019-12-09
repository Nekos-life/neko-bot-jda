package life.nekos.bot.commands

import kotlinx.coroutines.future.await
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.Formats
import me.devoxin.flight.annotations.Async
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import java.util.concurrent.Executors

class Neko : Cog {
    private val scheduler = Executors.newSingleThreadExecutor()

    override fun localCheck(ctx: Context, command: CommandWrapper): Boolean {
        // Model.statsUp(command.name)
        return true
    }

    fun embed(ctx: Context, description: String, imageUrl: String,
              embedOptions: EmbedBuilder.() -> Unit = {}) {
        ctx.embed {
            //setColor()
            setDescription(description)
            setImage(imageUrl)
            apply(embedOptions)
        }
    }

    @Command(description = "Random fox girl", aliases = ["fox_girl", "fg"])
    fun fox(ctx: Context) {
        NekosLife.fox().thenAccept {
            embed(ctx, "Fox girls \\o/", it)
        }
    }

    @Command(description = "Random nekos owO", aliases = ["owo", "wew", "nyaaaa"], nsfw = true)
    fun lewd(ctx: Context) {
        NekosLife.lewd().thenAccept {
            embed(ctx, "Nekos owo", it) {
                setColor(Color.magenta)
            }
        }
    }

    @Command(description = "Random nekos owO")
    fun neko(ctx: Context) {
        NekosLife.neko().thenAccept {
            embed(ctx, "Nekos \\o/", it)
        }
    }

    @Command(description = "mew!")
    fun nya(ctx: Context) {
        ctx.send("${ctx.author.asMention}, Mew!!~ ${Formats.cats.random()}")
    }

    @Async
    @DonorOnly
    @Command(
        description = "Neko slideshow \\o//",
        aliases = ["lewdslideshow", "lss", "mewww", "o.o"],
        nsfw = true
    )
    suspend fun lewds(ctx: Context) = Shell.slideshow(ctx) { m, cycle, total ->
        val image = NekosLife.lewd().await()

        m.editMessage(EmbedBuilder()
            //.setColor()
            .setDescription("$cycle of $total")
            .setImage(image)
            .build()
        ).submit().await()
    }

    @Async
    @Command(description = "Neko slideshow", aliases = ["slideshow", "ss", "mew", "nyaa"])
    suspend fun nekos(ctx: Context) = Shell.slideshow(ctx) { m, cycle, total ->
        val image = NekosLife.neko().await()

        m.editMessage(EmbedBuilder()
            //.setColor()
            .setDescription("$cycle of $total")
            .setImage(image)
            .build()
        ).submit().await()
    }
}