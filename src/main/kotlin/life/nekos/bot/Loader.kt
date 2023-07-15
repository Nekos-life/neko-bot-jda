package life.nekos.bot

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import life.nekos.bot.framework.CustomHelpCommand
import life.nekos.bot.framework.parsers.stringorbool.StringBool
import life.nekos.bot.framework.parsers.stringorbool.StringOrBool
import life.nekos.bot.listeners.FlightEventAdapter
import life.nekos.bot.utils.NekoPrefixProvider
import life.nekos.bot.utils.Server
import life.nekos.bot.utils.extensions.UserParser
import me.devoxin.flight.api.CommandClient
import me.devoxin.flight.api.CommandClientBuilder
import net.dv8tion.jda.api.entities.Activity

object Loader {
    val bootTime = System.currentTimeMillis()

    var isDebug = false
        private set

    lateinit var bot: NekoBot
    lateinit var commandClient: CommandClient

    @ExperimentalStdlibApi
    @JvmStatic
    fun main(args: Array<String>) {
        Server().server(7000)
        isDebug = args.any { it == "--debug" }
        val token = if (isDebug) Config["debug_token"] else Config["token"]

        commandClient = CommandClientBuilder()
            .setOwnerIds(248294452307689473L, 180093157554388993L, 596330574109474848L) // Tails, devo, dyna
            .setPrefixProvider(NekoPrefixProvider())
            .registerDefaultParsers()
            .addCustomParser(StringBool::class.java, StringOrBool())
            .addEventListeners(FlightEventAdapter())
            .addCustomParser(UserParser())
            .configureDefaultHelpCommand { enabled = false }
            .build()

        bot = NekoBot.new {
            addEventListeners(commandClient)
            setActivity(Activity.playing("https://nekos.life"))
            setAudioSendFactory(NativeAudioSendFactory())
            setToken(token)
            setShardsTotal(-1)
        }

        commandClient.commands.apply {
            register(CustomHelpCommand())
            register("life.nekos.bot.commands")
        }
    }
}

/**
 * TODO: Extensively test the Database entity system and port the User entity over.
 * TODO: Finish stats integration
 * TODO: Handlers
 * TODO: Web server.
 */
