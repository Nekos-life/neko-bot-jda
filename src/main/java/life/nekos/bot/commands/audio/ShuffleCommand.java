package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.entities.Message;

import java.util.Collections;
import java.util.List;

import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPrems;

@CommandDescription(
        name = "Shuffle",
        triggers = {"shuffle", "mix"},
        attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "dm")},
        description = "shuffles the current queue."
)
public class ShuffleCommand implements Command {
    public void execute(Message event, String args) {
        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            event
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, You must join a voice channel to use the command. "
                                            + Formats.NEKO_C_EMOTE))
                    .queue();
            return;
        }
        if (AudioHandler.getMusicManager(event.getGuild()).scheduler.queue.isEmpty()) {
            event
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
                            m -> {
                                if (canReact(m))
                                    m.addReaction(m.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                            });
            return;
        }
        if (!audioPrems(
                event, AudioHandler.getMusicManager(event.getGuild()).player.getPlayingTrack())) {
            event
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, You don't have permission to do this. " + Formats.NEKO_C_EMOTE))
                    .queue();
        }
        Collections.shuffle((List<?>) AudioHandler.getMusicManager(event.getGuild()).scheduler.queue);
        event
                .getChannel()
                .sendMessage("owo i mixed them all up " + Formats.getCat())
                .queue(
                        message -> {
                            if (canReact(message)) {
                                message
                                        .addReaction(
                                                message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.SHUFFLE_EMOTE)))
                                        .queue();
                            }
                        });
    }
}
