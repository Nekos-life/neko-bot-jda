package life.nekos.bot.commands.neko

import kotlinx.coroutines.future.await
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.extensions.respondUnit
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.context.MessageContext
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData
import java.util.concurrent.CompletableFuture

class Neko : Cog {
    private suspend fun embed(ctx: Context, description: String, imageUrl: CompletableFuture<String>,
                      embedOptions: EmbedBuilder.() -> Unit = {}) {
        ctx.asSlashContext?.deferAsync()
        val image = imageUrl.await()

        ctx.respond {
            embed {
                setColor(Colors.getEffectiveColor(ctx))
                setDescription(description)
                setImage(image)
                apply(embedOptions)
            }
        }
    }

    @Command(description = "Random fox girl", aliases = ["fox_girl", "fg"])
    suspend fun fox(ctx: Context) = embed(ctx, "Fox girls \\o/", NekosLife.fox)

    @Command(description = "Random nekos owO")
    suspend fun neko(ctx: Context) = embed(ctx, "Nekos \\o/", NekosLife.neko)

    @Command(description = "Mew!")
    fun nya(ctx: Context) = ctx.respondUnit("${ctx.author.asMention}, Mew!!~ ${Formats.cats.random()}")

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
