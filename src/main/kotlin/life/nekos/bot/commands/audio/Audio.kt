package life.nekos.bot.commands.audio

import life.nekos.bot.audio.LoopMode
import life.nekos.bot.audio.PlayerRegistry
import life.nekos.bot.framework.Paginator
import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.Checks
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.TextUtils
import life.nekos.bot.utils.extensions.respondUnit
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong

class Audio : Cog {
    private fun checkVoice(ctx: Context): Boolean {
        val selfChannel = ctx.guild!!.selfMember.voiceState!!.channel
        val targetChannel = ctx.member!!.voiceState!!.channel

        return when {
            selfChannel == null -> {
                ctx.respond("nu nya!~ I'm not playing anything...")
                false
            }
            targetChannel != selfChannel -> {
                ctx.respond("nu nya!~ You must be in my voice channel to use this command~")
                false
            }
            else -> true
        }
    }

    private fun connectToVoice(ctx: Context): Boolean {
        val me = ctx.guild!!.selfMember.voiceState!!
        val targetChannel = ctx.member!!.voiceState!!.channel
        val userLimit = (targetChannel as? VoiceChannel)?.userLimit ?: -1

        if (me.channel != null) {
            if (targetChannel != me.channel) {
                ctx.respond("Nu nya~ You need to join my voice channel")
            }

            return true
        }

        return when {
            targetChannel == null -> {
                ctx.respond("You need to join a voice channel, nya~")
                false
            }
            userLimit > 0 && targetChannel.members.size >= userLimit
                && !ctx.guild!!.selfMember.hasPermission(targetChannel, Permission.VOICE_MOVE_OTHERS) -> {
                    ctx.respondUnit("Nu nya~ Your voice channel is full!")
                false
            }
            !ctx.guild!!.selfMember.hasPermission(targetChannel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK) -> {
                ctx.respondUnit("Nu nya~ I don't have permission to join your voice channel~")
                false
            }
            else -> {
                ctx.guild!!.audioManager.openAudioConnection(targetChannel)
                true
            }
        }
    }

    @Command(aliases = ["previous", "b"], description = "Play previous song, or restarts current", guildOnly = true)
    fun back(ctx: Context) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.respondUnit("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)
        val track = player.playingTrack
            ?: player.lastTrack
            ?: return ctx.respondUnit("nu! nya~ I wasn't playing anything before")

        player.playTrack(track.makeClone())
        ctx.respond {
            setColor(Colors.getEffectiveColor(ctx))
            setAuthor(ctx.jda.selfUser.name, ctx.jda.getInviteUrl(), ctx.jda.selfUser.effectiveAvatarUrl)
            addField(
                "${Formats.INFO_EMOTE}  Now Playing ${Formats.BACK_EMOTE}",
                "Track: ${track.info.title}\nDuration: ${TextUtils.toTimeString(track.info.length)}",
                false
            )
        }
    }

    @Command(aliases = ["np", "playing", "now_playing", "song"], description = "Shows info on the playing track", guildOnly = true)
    fun nowplaying(ctx: Context) {
        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)
        val track = player.playingTrack
            ?: return ctx.respondUnit("oh? the queue is empty! play something first nya~")

        ctx.respond {
            setAuthor(ctx.jda.selfUser.name, ctx.jda.getInviteUrl(), ctx.jda.selfUser.effectiveAvatarUrl)
            addField(
                "${Formats.INFO_EMOTE}  Now Playing ${Formats.PLAY_EMOTE}",
                "Track: ${track.info.title}\n" +
                        "Duration: ${TextUtils.toTimeString(track.position)}/${TextUtils.toTimeString(track.info.length)}\n" +
                        "Requested by: ${track.getUserData(User::class.java).name}",
                false
            )
        }
    }

    @Command(aliases = ["p"], description = "Play a song from a URL, or search.", guildOnly = true)
    fun play(ctx: Context, @Greedy query: String) {
        if (!connectToVoice(ctx)) {
            return
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if ("https://" in query || "http://" in query) {
            player.load(ctx, query.removePrefix("<").removeSuffix(">"), false)
        } else {
            player.load(ctx, query, true)
        }
    }

    @Command(aliases = ["q"], description = "Shows the audio queue")
    fun queue(ctx: Context, page: Int = 1) {
        val ah = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (ah.queue.isEmpty()) {
            return ctx.respondUnit("The queue is currently empty, nya~")
        }

        val current = ah.playingTrack
        val items = ah.queue.map {
            "${it.info.title} `[${TextUtils.toTimeString(it.duration)}]`\nQueued by: ${(it.userData as User).name}"
        }
        val totalDuration = ah.queue.sumByLong { it.duration }
        val paginator = Paginator(items).apply { page(page) }

        ctx.respond {
            setColor(Colors.getRandomColor())
            setTitle("Now Playing ${Formats.PLAY_EMOTE}")
            setDescription(
                "Track: ${current.info.title} `[${TextUtils.toTimeString(current.duration)}]`\n" +
                        "Queued by: ${(current.userData as User).name}"
            )
            addField("Queue", paginator.display(), false)
            setFooter("Current Playlist: Total Tracks: ${ah.queue.size}, Total Length: ${TextUtils.toTimeString(totalDuration)}")
        }
    }

    @DonorOnly
    @Command(aliases = ["loop"], description = "Set repeat for a track.", guildOnly = true)
    fun repeat(ctx: Context, loop: String) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.respondUnit("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (player.playingTrack == null) {
            return ctx.respondUnit("oh? the queue is empty! play something first nya~")
        }

        when (loop) {
            "all", "queue" -> player.loopSetting = LoopMode.ALL
            "current", "single", "track" -> player.loopSetting = LoopMode.SINGLE
            "off", "none" -> player.loopSetting = LoopMode.NONE
            else -> return ctx.respondUnit("You need to specify `all`, `current` or `none` nya~")
        }

        ctx.respond("Alright nya, I have set repeat to `${player.loopSetting.name.lowercase()}` ${Formats.randomCat()}")
    }

    @Command(aliases = ["shuffle", "mix"], description = "Shuffles the current queue.", guildOnly = true)
    fun shuffle(ctx: Context) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.respondUnit("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (player.queue.isEmpty()) {
            return ctx.respondUnit("oh? the queue is empty! play something first nya~")
        }

        player.queue.shuffle()
        ctx.respond("owo I mixed them all up ${Formats.randomCat()}")
    }

    @Command(aliases = ["s", "next"], description = "Skips the current track.", guildOnly = true)
    fun skip(ctx: Context) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.respondUnit("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (player.playingTrack == null) {
            return ctx.respondUnit("oh? the queue is empty! play something first nya~")
        }

        player.nextTrack()

        val nextTrack = player.playingTrack
            ?: return ctx.respondUnit("There's nothing left to play, nya~")

        ctx.respond {
            setAuthor(ctx.jda.selfUser.name, ctx.jda.getInviteUrl(), ctx.jda.selfUser.effectiveAvatarUrl)
            addField(
                "${Formats.INFO_EMOTE}  Now Playing ${Formats.PLAY_EMOTE}",
                "Track: ${nextTrack.info.title}\n" +
                        "Duration: ${TextUtils.toTimeString(nextTrack.position)}/${TextUtils.toTimeString(nextTrack.info.length)}\n" +
                        "Requested by: ${nextTrack.getUserData(User::class.java).name}",
                false
            )
        }
    }

    @Command(aliases = ["quit", "disconnect"], description = "Stops playback, clears queue and disconnects.", guildOnly = true)
    fun stop(ctx: Context) {
        if (!Checks.audioChecks(ctx)) {
            return ctx.respondUnit("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        PlayerRegistry.destroyPlayer(ctx.guild!!.idLong)
        ctx.respond("Done, Nya~ ${Formats.STOP_EMOTE} ${Formats.randomCat()}")
    }

    @DonorOnly
    @Command(aliases = ["vol", "v"], description = "Sets the volume.", guildOnly = true)
    fun volume(ctx: Context, vol: Int? = null) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.respondUnit("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (vol == null) {
            return ctx.respondUnit("My volume is currently ${Formats.getVolEmote(player.volume)}**${player.volume}** nya~ ${Formats.randomCat()}")
        }

        player.volume = vol.coerceIn(0, 100)
        ctx.respond("I set the volume to ${Formats.getVolEmote(player.volume)}**${player.volume}**, nya~ ${Formats.randomCat()}")
    }

}
