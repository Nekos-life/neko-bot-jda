package life.nekos.bot.commands

import life.nekos.bot.apis.NekosLife
import life.nekos.bot.framework.parsers.stringorbool.StringBool
import life.nekos.bot.utils.RequestUtil
import life.nekos.bot.utils.WumpDump
import life.nekos.bot.utils.extensions.thenException
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Greedy
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.entities.Icon
import java.io.InputStreamReader

class Owner : Cog {

    @Command(description = "Force-send a neko.", developerOnly = true)
    fun coin(ctx: Context) {

    }

    @Command(aliases = ["debug"], description = "Eval, duh.", developerOnly = true)
    fun eval(ctx: Context) {

    }

    @Command(description = "Sets the bot's avatar. Pass a URL for custom, or `y` for random.", developerOnly = true)
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

    @Command(aliases = ["exec"], description = "Runs a shell command.", developerOnly = true)
    fun ssh(ctx: Context, command: String, @Greedy args: String?) {
        val commandArgs = args?.split(" ")?.toTypedArray() ?: arrayOf()

        val proc = ProcessBuilder()
            .command(command, *commandArgs)
            .start()

        val stdout = InputStreamReader(proc.inputStream).readText()
        val stderr = InputStreamReader(proc.errorStream).readText()
        val content = String.format("- - - - - STDOUT - - - - -\n{}\n\n- - - - - STDERR - - - - -\n{}", stdout, stderr)

        WumpDump.paste(content)
            .thenAccept {
                ctx.send("**Shell output:** <$it>")
            }
            .thenException {
                ctx.send(it.localizedMessage)
            }
    }

}
