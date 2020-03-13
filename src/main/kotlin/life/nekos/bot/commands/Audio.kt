package life.nekos.bot.commands

import life.nekos.bot.audio.PlayerRegistry
import life.nekos.bot.utils.Checks
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.TextUtils
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.models.Cog
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

    @Command(aliases=["np", "playing", "now_playing", "song"], description = "Shows info on the playing track",
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

}
