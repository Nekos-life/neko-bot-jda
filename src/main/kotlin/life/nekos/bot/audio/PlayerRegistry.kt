package life.nekos.bot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import life.nekos.bot.Loader
import java.util.concurrent.ConcurrentHashMap

object PlayerRegistry {
    private val playerManager = DefaultAudioPlayerManager()
    private val players = ConcurrentHashMap<Long, Player>()

    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
        // NicoAudio

    }

    fun playerFor(guildId: Long): Player {
        return players.computeIfAbsent(guildId) {
            val guild = Loader.bot.getGuildById(guildId)
                ?: throw IllegalStateException("Creating player for a guild that doesn't exist! GuildId: $guildId")

            val audioPlayer = playerManager.createPlayer()
            return@computeIfAbsent Player(audioPlayer).apply {
                guild.audioManager.sendingHandler = this
            }
        }
    }

    fun destroyPlayer(guildId: Long, disconnect: Boolean = true) {
        Loader.bot.getGuildById(guildId)?.audioManager?.let {
            it.sendingHandler = null
            it.closeAudioConnection()
        }
        players[guildId]?.cleanup()
    }

    fun searchFor(identifier: String, handler: AudioLoadResultHandler) {
        playerManager.loadItem(identifier, handler)
    }
}
