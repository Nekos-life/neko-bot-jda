package life.nekos.bot.commands.audio

import life.nekos.bot.audio.LoopMode
import life.nekos.bot.audio.PlayerRegistry
import life.nekos.bot.framework.annotations.DonorOnly
import life.nekos.bot.utils.Checks
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.TextUtils
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Greedy
import me.devoxin.flight.models.Cog
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User

class Audio : Cog {

    private fun checkVoice(ctx: Context): Boolean {
        val me = ctx.guild!!.selfMember
        val invoker = ctx.member!!

        return when {
            me.voiceState!!.channel == null -> {
                ctx.send("nu nya!~ I'm not playing anything.")
                false
            }
            invoker.voiceState!!.channel != me.voiceState!!.channel -> {
                ctx.send("nu nya!~ You must be in my voice channel to use this command.")
                false
            }
            else -> true
        }
    }

    private fun connectToVoice(ctx: Context): Boolean {
        val me = ctx.guild!!.selfMember.voiceState!!
        val invoker = ctx.member!!.voiceState!!

        if (me.channel != null) {
            if (invoker.channel != me.channel) {
                ctx.send("Nu nya~ You need to join my voice channel")
            }
            return true
        }

        return when {
            invoker.channel == null -> {
                ctx.send("You need to join a voice channel, nya~")
                false
            }
            invoker.channel!!.userLimit > 0
                    && invoker.channel!!.members.size >= invoker.channel!!.userLimit
                    && !ctx.guild!!.selfMember.hasPermission(invoker.channel!!, Permission.VOICE_MOVE_OTHERS) -> {
                ctx.send("Nu nya~ Your voice channel is full!")
                false
            }
            !ctx.guild!!.selfMember.hasPermission(invoker.channel!!, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK) -> {
                ctx.send("Nu nya~ I don't have permission to join your voice channel~")
                false
            }
            else -> {
                ctx.guild!!.audioManager.openAudioConnection(invoker.channel)
                true
            }
        }
    }

    @Command(aliases = ["previous", "b"], description = "Play previous song, or restarts current",
        guildOnly = true)
    fun back(ctx: Context) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.send("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)
        val track = player.playingTrack
            ?: player.lastTrack
            ?: return ctx.send("nu! nya~ I wasn't playing anything before")

        player.playTrack(track.makeClone())
        ctx.send {
            setColor(Colors.getEffectiveColor(ctx))
            setAuthor(ctx.jda.selfUser.name, ctx.jda.getInviteUrl(), ctx.jda.selfUser.effectiveAvatarUrl)
            addField(
                "${Formats.INFO_EMOTE}  Now Playing ${Formats.BACK_EMOTE}",
                "Track: ${track.info.title}\nDuration: ${TextUtils.toTimeString(track.info.length)}",
                false
            )
        }
    }

    @Command(aliases = ["np", "playing", "now_playing", "song"], description = "Shows info on the playing track",
        guildOnly = true)
    fun nowplaying(ctx: Context) {
        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)
        val track = player.playingTrack
            ?: return ctx.send("oh? the queue is empty! play something first nya~")

        ctx.send {
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

    @Command(aliases = ["p"], description = "Play a song from a URL, or search.",
        guildOnly = true)
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

    // Queue

    @DonorOnly
    @Command(aliases = ["loop"], description = "Set repeat for a track.",
        guildOnly = true)
    fun repeat(ctx: Context, loop: String) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.send("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (player.playingTrack == null) {
            return ctx.send("oh? the queue is empty! play something first nya~")
        }

        when (loop) {
            "all", "queue" -> player.loopSetting = LoopMode.ALL
            "current", "single" -> player.loopSetting = LoopMode.SINGLE
            "off", "none" -> player.loopSetting = LoopMode.NONE
            else -> return ctx.send("You need to specify `all`, `current` or `none` nya~")
        }

        ctx.send("Alright nya, I have set repeat to `${player.loopSetting.name.toLowerCase()}` " +
                Formats.randomCat())
    }

    @Command(aliases = ["shuffle", "mix"], description = "Shuffles the current queue.",
        guildOnly = true)
    fun shuffle(ctx: Context) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.send("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (player.queue.isEmpty()) {
            return ctx.send("oh? the queue is empty! play something first nya~")
        }

        player.queue.shuffle()
        ctx.send("owo I mixed them all up ${Formats.randomCat()}")
    }

    @Command(aliases = ["s", "next"], description = "Skips the current track.",
        guildOnly = true)
    fun skip(ctx: Context) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.send("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (player.playingTrack == null) {
            return ctx.send("oh? the queue is empty! play something first nya~")
        }

        player.nextTrack()

        val nextTrack = player.playingTrack
            ?: return ctx.send("There's nothing left to play, nya~")

        ctx.send {
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

    @Command(aliases = ["quit", "disconnect"], description = "Stops playback, clears queue and disconnects.",
        guildOnly = true)
    fun stop(ctx: Context) {
        if (!Checks.audioChecks(ctx)) {
            return ctx.send("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        PlayerRegistry.destroyPlayer(ctx.guild!!.idLong)
        ctx.send("Done, Nya~ ${Formats.STOP_EMOTE} ${Formats.randomCat()}")
    }

    @DonorOnly
    @Command(aliases = ["vol", "v"], description = "Sets the volume.", guildOnly = true)
    fun volume(ctx: Context, vol: Int? = null) {
        if (!checkVoice(ctx)) {
            return
        }

        if (!Checks.audioChecks(ctx)) {
            return ctx.send("nu nya!~ You don't have permission to do this. ${Formats.NEKO_C_EMOTE}")
        }

        val player = PlayerRegistry.playerFor(ctx.guild!!.idLong)

        if (vol == null) {
            return ctx.send("My volume is currently " +
                    "${Formats.getVolEmote(player.volume)}**${player.volume}** nya~ ${Formats.randomCat()}")
        }

        player.volume = vol.coerceIn(0, 100)
        ctx.send("I set the volume to " +
                "${Formats.getVolEmote(player.volume)}**${player.volume}**, nya~ ${Formats.randomCat()}")
    }

}
