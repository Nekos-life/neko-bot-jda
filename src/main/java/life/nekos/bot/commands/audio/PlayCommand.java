package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.audio.GuildMusicManager;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

@CommandDescription(
        name = "play",
        triggers = {"play", "p"},
        attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "dm")},
        description = "play some url || search term"
)
public class PlayCommand implements Command {
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
        if (!BotChecks.canConnect(message.getMember().getVoiceState().getChannel())) {
            message
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, i dont have permissions to connect. " + Formats.NEKO_C_EMOTE))
                    .queue();
            return;
        }
        System.out.print(args);
        System.out.print(args.toLowerCase().contains("redtube"));
        if (args.toLowerCase().contains("redtube") && !message.getTextChannel().isNSFW()
                || args.toLowerCase().contains("pornhub") && !message.getTextChannel().isNSFW()) {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setDescription(
                                            "Nuu nya, pornhub/redtube can only be played Discord nsfw channels")
                                    .build())
                    .queue();
            return;
        }
        GuildMusicManager musicManager = AudioHandler.getMusicManager(message.getGuild());
        if (args.isEmpty()) {
            if (musicManager.player.isPaused()) {
                musicManager.player.setPaused(false);
                message
                        .getChannel()
                        .sendMessage("let me resume for you nya~")
                        .queue(
                                msg ->
                                        msg.addReaction(
                                                msg.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                                .queue());
            } else if (musicManager.player.getPlayingTrack() != null) {
                message
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setAuthor(
                                                Formats.error("Missing Args"),
                                                message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                                message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                        .addField(
                                                Formats.info("Info"),
                                                MessageFormat.format(
                                                        "{0}play <some url / search term>", Models.getPrefix(message)),
                                                false)
                                        .build())
                        .queue();
            } else if (musicManager.scheduler.queue.isEmpty()) {
                message
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setAuthor(
                                                Formats.error("Missing Args"),
                                                message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                                message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                        .addField(
                                                Formats.info("Info"),
                                                MessageFormat.format(
                                                        "{0}play <some url / search term>", Models.getPrefix(message)),
                                                false)
                                        .build())
                        .queue();
            }
        } else {
            AudioHandler.loadAndPlay(message, args, false);
            message
		            .addReaction(message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                    .queue();
        }
    }
}
