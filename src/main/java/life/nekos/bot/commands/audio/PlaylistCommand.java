package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

@CommandDescription(
        name = "playlist",
        triggers = {"playlist", "pl"},
        attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "dm")},
        description = "play some playlist url | search term"
)
public class PlaylistCommand implements Command {
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
        if (args.isEmpty()) {
            event
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setAuthor(
                                            Formats.error("Missing Args"),
                                            event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                            event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .addField(
                                            Formats.info("Info"),
                                            MessageFormat.format(
                                                    "{0}playlist <some playlist url>", Models.getPrefix(event)),
                                            false)
                                    .build())
                    .queue();
        } else {
            AudioHandler.loadAndPlay(event, args, true);
            event
                    .addReaction(event.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAYLIST_EMOTE)))
                    .queue();
        }
    }
}
