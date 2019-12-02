package life.nekos.bot.commands

import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.parsers.stringorbool.StringBool
import life.nekos.bot.utils.RequestUtil
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Optional
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.entities.Icon

class Owner : Cog {

    @Command(description = "Force-send a neko.", developerOnly = true)
    fun coin(ctx: Context) {

    }

    @Command(aliases = ["debug"], description = "Eval, duh.", developerOnly = true)
    fun eval(ctx: Context) {

    }

    @Command(description = "Sets the bot's avatar. Pass a URL for custom, or `y` for random.")
    fun setavatar(ctx: Context, urlOrRandom: StringBool) {
        if (urlOrRandom.isBool && (urlOrRandom.entity as Boolean)) {
            NekosLife.avatar()
                .thenAccept { setAvatarWithUrl(ctx, it) }
        } else {
            setAvatarWithUrl(ctx, urlOrRandom.entity as String)
        }
    }

    fun setAvatarWithUrl(ctx: Context, url: String) {
        RequestUtil.request { url(url) }
            .submit()
            .thenCompose { RequestUtil.request { url(url) }.submit() }
            .thenCompose {
                val icon = Icon.from(it.body()!!.byteStream())
                ctx.jda.selfUser.manager.setAvatar(icon).submit()
            }
            .exceptionally {
                ctx.send("Failed to set avatar:\n```${it.localizedMessage}```")
                return@exceptionally null
            }
    }

}
