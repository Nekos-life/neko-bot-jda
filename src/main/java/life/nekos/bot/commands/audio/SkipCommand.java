package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.text.MessageFormat;

import static life.nekos.bot.audio.AudioHandler.getTimestamp;
import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPrems;

@CommandDescription(
        name = "skip",
        triggers = {"skip", "s", "next"},
        attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "dm")},
        description = "skips the current track."
)
public class SkipCommand implements Command {
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
                            message -> {
                                if (canReact(message)) {
                                    message
                                            .addReaction(
                                                    message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                                }
                            });
            return;
        }
        AudioTrack Track = AudioHandler.getMusicManager(event.getGuild()).player.getPlayingTrack();

        if (!audioPrems(event, Track)) {
            event
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, You don't have permission to do this. " + Formats.NEKO_C_EMOTE))
                    .queue();
            return;
        }

        AudioHandler.getMusicManager(event.getGuild()).scheduler.nextTrack();
        event
                .getChannel()
                .sendMessage("Next Please! " + Formats.getCat())
                .queue(
                        message -> {
                            if (canReact(message)) {
                                message
                                        .addReaction(
                                                message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.NEXT_EMOTE)))
                                        .queue();
                            }
                        });
        AudioTrack Trackk = AudioHandler.getMusicManager(event.getGuild()).player.getPlayingTrack();
        User user = (User) Trackk.getUserData();
        MessageEmbed msg =
                new EmbedBuilder()
                        .setAuthor(
                                event.getJDA().getSelfUser().getName(),
                                event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                        .addField(
                                Formats.info("Now Playing " + Formats.PLAY_EMOTE),
                                MessageFormat.format(
                                        "Track: {0}\nLength: {1}/{2}\nQueued by: {3}",
                                        Trackk.getInfo().title,
                                        getTimestamp(Trackk.getPosition()),
                                        getTimestamp(Trackk.getDuration()),
                                        user.getName()),
                                false)
                        .build();
        event
                .getChannel()
                .sendMessage(msg)
                .queue(
                        message -> {
                            if (canReact(message)) {
                                message
                                        .addReaction(
                                                message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                        .queue();
                            }
                        });
    }
}
