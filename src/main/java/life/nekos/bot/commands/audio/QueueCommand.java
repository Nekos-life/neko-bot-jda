package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static life.nekos.bot.NekoBot.waiter;
import static life.nekos.bot.audio.AudioHandler.getTimestamp;

@CommandDescription(
        name = "queue",
        triggers = {"q", "queue"},
        attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "dm")},
        description = "Shows the audio queue"
)
public class QueueCommand implements Command {
    Paginator.Builder pbuilder =
            new Paginator.Builder()
                    .setColumns(1)
                    .setItemsPerPage(10)
                    .showPageNumbers(true)
                    .waitOnSinglePage(true)
                    .useNumberedItems(true)
                    .setEventWaiter(waiter)
                    .setTimeout(1, TimeUnit.MINUTES);

    public void execute(Message event, String args) {
        Guild guild = event.getGuild();
        Queue<AudioTrack> queue = AudioHandler.getMusicManager(guild).scheduler.queue;
        {
            if (queue.isEmpty()) {
                event.getChannel().sendMessage("The queue is currently empty!").queue();
            } else {
                long queueLength = 0;
                pbuilder.clearItems();
                for (AudioTrack track : queue) {
                    queueLength += track.getDuration();
                    User user = (User) track.getUserData();
                    pbuilder.addItems(
                            MessageFormat.format(
                                    "{1} `[{0}]`\nQueued by: {2}",
                                    AudioHandler.getTimestamp(track.getDuration()),
                                    track.getInfo().title,
                                    user.getName()));
                }
                AudioTrack Track = AudioHandler.getMusicManager(event.getGuild()).player.getPlayingTrack();
                User user = (User) Track.getUserData();
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
                                                Track.getInfo().title,
                                                getTimestamp(Track.getPosition()),
                                                getTimestamp(Track.getDuration()),
                                                user.getName()),
                                        false)
                                .build();
                Paginator p =
                        pbuilder
                                .setColor(Colors.getRndColor())
                                .setText(
                                        MessageFormat.format(
                                                "Current Playlist: Total Tracks: `{0}` Total Length: `{1}`",
                                                queue.size(), AudioHandler.getTimestamp(queueLength)))
                                .setUsers(event.getAuthor())
                                .setFinalAction(
                                        m -> {
                                            try {
                                                m.clearReactions().queue();
                                                m.editMessage(msg).queue();
                                            } catch (PermissionException ex) {
                                                m.delete().queue();
                                            }
                                        })
                                .build();
                p.paginate(event.getChannel(), 1);
            }
        }
    }
}
