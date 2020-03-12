package life.nekos.bot.audio

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import life.nekos.bot.Loader
import java.util.concurrent.ConcurrentHashMap

object PlayerRegistry {
    private val playerManager = DefaultAudioPlayerManager()
    private val players = ConcurrentHashMap<Long, Player>()

    init {

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
}
