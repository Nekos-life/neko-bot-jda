package life.nekos.bot

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import life.nekos.bot.framework.parsers.stringorbool.StringBool
import life.nekos.bot.framework.parsers.stringorbool.StringOrBool
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
        val token = if (isDebug) "NDIzODU4MDE2NDEzMjg2NDAw.XfAkqA.8k_nQZsZwh42OwPQZBG_mTSU-m4" else ""

        commandClient = CommandClientBuilder()
            .setPrefixes()
            .setOwnerIds(248294452307689473L, 180093157554388993L)
            .setPrefixProvider(NekoPrefixProvider())
            .registerDefaultParsers()
            //.addCustomParser(StringBool::class.java, StringOrBool())
            .addEventListeners(FlightEventAdapter())
            .build()

        bot = NekoBot.new {
            addEventListeners(commandClient)
            setActivity(Activity.playing("https://nekos.life"))
            setAudioSendFactory(NativeAudioSendFactory())
            setToken(token)
            setShardsTotal(-1)
        }

        commandClient.registerCommands("life.nekos.bot.commands")
    }

}
