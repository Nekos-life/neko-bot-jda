package life.nekos.bot.commands.nsfw

import kotlinx.coroutines.future.await
import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.EmbedBuilder
import java.util.concurrent.CompletableFuture

class Nsfw : Cog {
    override fun localCheck(ctx: Context, command: CommandFunction): Boolean {
        // Model.statsUp(command.name)
        return true
    }

    suspend fun embed(ctx: Context, description: String, imageUrl: CompletableFuture<String>,
                      embedOptions: EmbedBuilder.() -> Unit = {}) {
        ctx.asSlashContext?.deferAsync()
        val image = imageUrl.await()

        ctx.respond {
            setColor(Colors.getEffectiveColor(ctx))
            setDescription(description)
            setImage(image)
            apply(embedOptions)
        }
    }

    @Command(description = "Random anal", nsfw = true)
    suspend fun anal(ctx: Context) = embed(ctx, Formats.LEWD_EMOTE, NekosLife.anal)

    @DonorOnly
    @Command(description = "Random kuni owO", nsfw = true)
    suspend fun kuni(ctx: Context) = embed(ctx, "kuni owo", NekosLife.kuni)

    @DonorOnly
    @Command(description = "Random pussy uwu", nsfw = true)
    suspend fun pussy(ctx: Context) = embed(ctx, "uwu pussy", NekosLife.pussy)
}
