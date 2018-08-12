package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.checks.UserChecks;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.text.MessageFormat;

import static life.nekos.bot.audio.AudioHandler.getTimestamp;

@CommandDescription(
        name = "Back",
        triggers = {"back", "previous", "b"},
        attributes = {
                @CommandAttribute(key = "music"),
        },
        description = "plays previous song or restarts current song"
)
public class BackCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        if (!message.getMember().getVoiceState().inVoiceChannel()) {
            message
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, You must join a voice channel to use the command. "
                                            + Formats.NEKO_C_EMOTE))
                    .queue();
            return;
        }
        AudioTrack Track = AudioHandler.getMusicManager(message.getGuild()).player.getPlayingTrack();
        if (!UserChecks.audioPrems(message, Track)) {
            message
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, You don't have permission to do this. " + Formats.NEKO_C_EMOTE))
                    .queue();
            return;
        }

        AudioTrack track = AudioHandler.getMusicManager(message.getGuild()).player.getPlayingTrack();
        if (track == null) track = AudioHandler.getMusicManager(message.getGuild()).scheduler.lastTrack;

        if (track != null) {
            MessageEmbed em =
                    new EmbedBuilder()
                            .setAuthor(
                                    message.getJDA().getSelfUser().getName(),
                                    message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                    message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                            .addField(
                                    Formats.info("Now Playing " + Formats.BACK_EMOTE),
                                    MessageFormat.format(
                                            "Track: {0}\nLength: {1}",
                                            track.getInfo().title, getTimestamp(track.getInfo().length)),
                                    false)
                            .setColor(Colors.getEffectiveColor(message))
                            .build();
            message.getChannel().sendMessage(em).queue();
            AudioTrack newTrack = track.makeClone();
            newTrack.setUserData(track.getUserData());
            AudioHandler.getMusicManager(message.getGuild()).player.playTrack(newTrack);
        } else {
            message.getChannel().sendMessage("nu! nya~, i wasn't playing anything before").queue();
        }
    }
}
