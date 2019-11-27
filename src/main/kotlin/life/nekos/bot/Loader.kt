package life.nekos.bot

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import life.nekos.bot.listeners.FlightEventAdapter
import me.devoxin.flight.api.CommandClient
import me.devoxin.flight.api.CommandClientBuilder
import net.dv8tion.jda.api.entities.Activity
import life.nekos.bot.utils.NekoPrefixProvider

object Loader {

    var isDebug = false
        private set

    lateinit var bot: NekoBot
    lateinit var commandClient: CommandClient

    @JvmStatic
    fun main(args: Array<String>) {
        isDebug = args.any { it == "--debug" }
        val token = if (isDebug) "" else ""

        commandClient = CommandClientBuilder()
            .setPrefixes()
            .setOwnerIds(248294452307689473L, 180093157554388993L)
            .setPrefixProvider(NekoPrefixProvider())
            .registerDefaultParsers()
            .addEventListeners(FlightEventAdapter())
            .build()

        bot = NekoBot.new {
            addEventListeners(commandClient)
            setActivity(Activity.playing("https://nekos.life"))
            setAudioSendFactory(NativeAudioSendFactory())
            setToken(token)
            setShards(-1)
        }

        commandClient.registerCommands("life.nekos.bot.commands")
    }

}
