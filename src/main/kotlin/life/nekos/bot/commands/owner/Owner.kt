package life.nekos.bot.commands.owner

import life.nekos.bot.apis.NekosLife
import life.nekos.bot.utils.*
import life.nekos.bot.utils.extensions.thenException
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.entities.Icon
import java.io.InputStreamReader

class Owner : Cog {
    @Command(description = "Force-send a neko.", developerOnly = true)
    fun coin(ctx: Context) {
        Send(ctx.message, false).poke(ctx.author.idLong)
        ctx.message.delete().queue()
    }

    @Command(description = "Sets the bot's avatar.", developerOnly = true)
    fun setavatar(ctx: Context) {
        NekosLife.avatar().thenAccept { setAvatarWithUrl(ctx, it) }
        ctx.send(Formats.info("Set Avatar"))
    }

    private fun setAvatarWithUrl(ctx: Context, url: String) {
        RequestUtil.request { url(url) }
            .submit()
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
        val content = String.format("- - - - - STDOUT - - - - -\n%s\n\n- - - - - STDERR - - - - -\n%s", stdout, stderr)

        WumpDump.paste(content)
            .thenAccept {
                ctx.send("**Shell output:** <$it>")
            }
            .thenException {
                ctx.send(it.localizedMessage)
            }
    }

    @Command(developerOnly = true)
    fun cheat(ctx: Context, code: String) {
        when (code) {
            notACheatCommand -> `inaccessible command 1`(ctx)
        }
    }

    private fun `inaccessible command 1`(ctx: Context) {
        Database.getUser(ctx.author.id).update {
            nekos += 500
        }
        ctx.send("Tell no one, nya~")
    }

    companion object {
        private val notACheatCommand = listOf(
            0x2191, 0x2191, 0x2193, 0x2193, 0x2190, 0x2192, 0x2190, 0x2192
        ).map(Int::toChar).joinToString("", transform = Char::toString)
    }
}
