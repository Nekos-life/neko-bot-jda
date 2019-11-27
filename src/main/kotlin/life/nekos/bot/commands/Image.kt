package life.nekos.bot.commands

import kotlinx.coroutines.future.await
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.annotations.DonorOnly
import me.devoxin.flight.annotations.Async
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import java.util.concurrent.Executors

class Image : Cog {
    private val scheduler = Executors.newSingleThreadExecutor()

    fun embed(ctx: Context, description: String, imageUrl: String,
              embedOptions: EmbedBuilder.() -> Unit = {}) {
        ctx.embed {
            //setColor()
            setDescription(description)
            setImage(imageUrl)
            apply(embedOptions)
        }
    }

    override fun localCheck(ctx: Context, command: CommandWrapper): Boolean {
        // Model.statsUp(command.name)
        return true
    }

    @Command(description = "Random fox girl", aliases = ["fox_girl", "fg"])
    fun fox(ctx: Context) {
        NekosLife.fox().thenAccept {
            embed(ctx, "Fox girls \\o/", it)
        }.join() // Exceptions thrown upstream are returned here
        // which are caught by the FlightEventAdapter so we can handle
        // them all together rather than individually.
    }

    @Command(description = "Random nekos OwO", aliases = ["owo", "wew", "nyaaaa"], nsfw = true)
    fun lewd(ctx: Context) {
        NekosLife.lewd().thenAccept {
            embed(ctx, "Nekos owo", it) {
                setColor(Color.magenta)
            }
        }.join()
    }

    @Async
    @DonorOnly
    @Command(
        description = "Neko slideshow \\o//",
        aliases = ["lewdslideshow", "lss", "mewww", "o.o"],
        nsfw = true
    )
    suspend fun lewds(ctx: Context) {
        //val color = getEffectiveColor()
        val m = ctx.sendAsync("\u200b")

        for (i in 1 until 21) {
            val lewd = NekosLife.lewd().await()
            editMessage(m) {
                //setColor(color)
                setTitle("Lewd Nekos \\o/") // @TODO emotes
                setDescription("$i of 20")
                setImage(lewd)
            }
        }
    }

    suspend fun editMessage(m: Message, builder: EmbedBuilder.() -> Unit) {
        m.editMessage(EmbedBuilder().apply(builder).build())
            .submit()
            .await()
    }



}
