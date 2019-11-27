package life.nekos.bot.commands

import life.nekos.bot.apis.NekosLife
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.models.Cog

class Image : Cog {

    fun embed(ctx: Context, description: String, imageUrl: String) {
        ctx.embed {
            //setColor()
            setDescription(description)
            setImage(imageUrl)
        }
    }

    @Command(description = "Random fox girl", aliases = ["fox_girl", "fg"])
    fun fox(ctx: Context) {
        NekosLife.fox().thenAccept {
            embed(ctx, "Fox girls \\o/", it)
        }
    }

}
