package life.nekos.bot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.TextUtils
import life.nekos.bot.utils.extensions.send
import me.devoxin.flight.api.context.Context
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer
import java.util.*

class Player(audioPlayer: AudioPlayer) : AudioEventAdapter(), AudioPlayer by audioPlayer, AudioSendHandler {
    // Send System Properties
    private var audioFrame: AudioFrame? = null

    // Player Properties
    var lastTrack: AudioTrack? = null
    val queue = LinkedList<AudioTrack>()
    var loopSetting = LoopMode.NONE

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

    fun load(ctx: Context, identifier: String, search: Boolean) {
        ctx.asSlashContext?.defer()

        PlayerRegistry.searchFor(if (search) "ytsearch:$identifier" else identifier, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                playOrEnqueue(track.apply { userData = ctx.author })

                ctx.send {
                    setColor(Colors.getEffectiveColor(ctx))
                    setAuthor(ctx.jda.selfUser.name, ctx.jda.getInviteUrl(), ctx.jda.selfUser.effectiveAvatarUrl)
                    addField(
                        Formats.info("Queued ${Formats.PLAY_EMOTE}"),
                        "Track: **${track.info.title}**\nDuration: **${TextUtils.toTimeString(track.info.length)}**",
                        false
                    )
                }
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if (playlist.isSearchResult) {
                    return trackLoaded(playlist.tracks.first())
                } else {
                    for (track in playlist.tracks) {
                        playOrEnqueue(track.apply { userData = ctx.author })
                    }
                }

                ctx.send {
                    setColor(Colors.getEffectiveColor(ctx))
                    setAuthor(ctx.jda.selfUser.name, ctx.jda.getInviteUrl(), ctx.jda.selfUser.effectiveAvatarUrl)
                    addField(
                        Formats.info("Info"),
                        "Added **${playlist.tracks.size}** from playlist **${playlist.name}**, nya~",
                        false
                    )
                }
            }

            override fun loadFailed(exception: FriendlyException) {
                ctx.send(Formats.error("nuu i cant nya~ something exploded: ${exception.localizedMessage}"))
            }

            override fun noMatches() {
                ctx.send(Formats.error("I couldn't find anything for `$identifier`, nya~"))
            }
        })
    }

    fun cleanup() {
        queue.clear()
        stopTrack()
        removeListener(this)
        destroy()
    }

    // Player Events
    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        lastTrack = track

        if (!endReason.mayStartNext) {
            return
        }

        when (loopSetting) {
            LoopMode.NONE -> nextTrack()
            LoopMode.SINGLE -> playTrack(track.makeClone())
            LoopMode.ALL -> playOrEnqueue(track.makeClone())
        }
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        nextTrack()
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
