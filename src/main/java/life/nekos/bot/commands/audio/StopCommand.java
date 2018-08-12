package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.audio.GuildMusicManager;
import life.nekos.bot.audio.VoiceHandler;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPrems;

@CommandDescription(
        name = "Stop",
        triggers = {"stop", "quit", "disconnect"},
        attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "dm")},
        description = "Stops playback, clears queue, disconnects"
)
public class StopCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("stop");
        if (!VoiceHandler.inVoice(message)) {
            message
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, You must join a voice channel to use this command. "
                                            + Formats.NEKO_C_EMOTE))
                    .queue();
            return;
        }
        GuildMusicManager musicManager = AudioHandler.getMusicManager(message.getGuild());
        if (musicManager.player.getPlayingTrack() != null) {
            if (!VoiceHandler.sameVoice(message)) {
                message
                        .getChannel()
                        .sendMessage(
                                Formats.error(
                                        "nu nya!~, You must join the channel im in to use this command. "
                                                + Formats.NEKO_C_EMOTE))
                        .queue();
                return;
            }
            if (!audioPrems(message, musicManager.player.getPlayingTrack())) {
                message
                        .getChannel()
                        .sendMessage(
                                Formats.error(
                                        "nu nya!~, You don't have permission to do this. " + Formats.NEKO_C_EMOTE))
                        .queue();
            }
            if (VoiceHandler.inVoice(message)) {
                VoiceHandler.disconnectFromVoice(message.getMember().getVoiceState().getChannel());
                message
                        .getChannel()
                        .sendMessage(
                                Formats.info(
                                        MessageFormat.format(
                                                "Done, Nya~ {0} {1}", Formats.STOP_EMOTE, Formats.getCat())))
                        .queue(
                                m -> {
                                    if (canReact(m)) {
                                        m.addReaction(m.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.STOP_EMOTE)))
                                                .queue();
                                    }
                                });
            }
        } else {
            message
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
                            m -> {
                                if (canReact(m)) {
                                    m.addReaction(m.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                                }
                            });
        }
    }
}
