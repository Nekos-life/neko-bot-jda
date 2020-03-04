package life.nekos.bot

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import life.nekos.bot.apis.PokeApi
import life.nekos.bot.framework.CustomHelpCommand
import life.nekos.bot.framework.parsers.stringorbool.StringBool
import life.nekos.bot.framework.parsers.stringorbool.StringOrBool
import life.nekos.bot.listeners.FlightEventAdapter
import me.devoxin.flight.api.CommandClient
import me.devoxin.flight.api.CommandClientBuilder
import net.dv8tion.jda.api.entities.Activity
import life.nekos.bot.utils.NekoPrefixProvider

object Loader {

    val bootTime = System.currentTimeMillis()

    var isDebug = false
        private set

    lateinit var bot: NekoBot
    lateinit var commandClient: CommandClient

    @ExperimentalStdlibApi
    @JvmStatic
    fun main(args: Array<String>) {
        isDebug = args.any { it == "--debug" }
        val token = Config["token"]

        commandClient = CommandClientBuilder()
            .setOwnerIds(248294452307689473L, 180093157554388993L)
            .setPrefixProvider(NekoPrefixProvider())
            .registerDefaultParsers()
            .addCustomParser(StringBool::class.java, StringOrBool())
            .addEventListeners(FlightEventAdapter())
            .configureDefaultHelpCommand { enabled = false }
            .build()

        bot = NekoBot.new {
            addEventListeners(commandClient)
            setActivity(Activity.playing("https://nekos.life"))
            setAudioSendFactory(NativeAudioSendFactory())
            setToken(token)
            setShardsTotal(-1)
        }

        bot.retrieveApplicationInfo().queue {
            println(it.name)
        }

        commandClient.registerCommands(CustomHelpCommand())
        commandClient.registerCommands("life.nekos.bot.commands")
    }

}

/**
 * TODO: Extensively test the Database entity system and port the User entity over.
 */

/**
 * TODO: Fun category
 * TODO: Bot category
 * TODO: Audio category
 * TODO: Finish user category
 * TODO: Finish owner category
 * TODO: Finish stats integration
 * TODO: Neko Spawning
 * TODO: Handlers
 * TODO: Checks (@FlightEventAdapter).
 * TODO: Web server.
 */
