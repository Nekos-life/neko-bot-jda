package life.nekos.bot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer
import java.util.*

class Player(audioPlayer: AudioPlayer) : AudioEventAdapter(), AudioPlayer by audioPlayer, AudioSendHandler {
    // Send System Properties
    private var audioFrame: AudioFrame? = null

    // Player Properties
    val queue = LinkedList<AudioTrack>()

    init {
        audioPlayer.addListener(this)
    }

    // Player Methods
    fun playOrEnqueue(track: AudioTrack) {
        if (!startTrack(track, true)) {
            queue.add(track)
        }
    }

    fun nextTrack() {
        playTrack(queue.poll())
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {

    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {

    }

    // Send Handler methods
    override fun provide20MsAudio(): ByteBuffer {
        return ByteBuffer.wrap(audioFrame!!.data)
    }

    override fun canProvide(): Boolean {
        audioFrame = provide()
        return audioFrame != null
    }

    override fun isOpus() = true
}
