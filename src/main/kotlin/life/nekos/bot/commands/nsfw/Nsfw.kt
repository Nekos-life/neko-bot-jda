package life.nekos.bot.commands.nsfw

import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.CommandWrapper
import me.devoxin.flight.api.Context
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.EmbedBuilder

class Nsfw : Cog {

    override fun localCheck(ctx: Context, command: CommandWrapper): Boolean {
        // Model.statsUp(command.name)
        return true
    }

    fun embed(ctx: Context, description: String, imageUrl: String,
              embedOptions: EmbedBuilder.() -> Unit = {}) {
        ctx.send {
            setColor(Colors.getEffectiveColor(ctx))
            setDescription(description)
            setImage(imageUrl)
            apply(embedOptions)
        }
    }

    @Command(description = "Random anal", nsfw = true)
    fun anal(ctx: Context) {
        NekosLife.anal().thenAccept {
            embed(ctx, Formats.LEWD_EMOTE, it)
        }
    }

    @DonorOnly
    @Command(description = "Random kuni owO", nsfw = true)
    fun kuni(ctx: Context) {
        NekosLife.kuni().thenAccept {
            embed(ctx, "kuni owo", it)
        }
    }

    @DonorOnly
    @Command(description = "Random pussy uwu", nsfw = true)
    fun pussy(ctx: Context) {
        NekosLife.pussy().thenAccept {
            embed(ctx, "uwu pussy", it)
        }
    }

}
