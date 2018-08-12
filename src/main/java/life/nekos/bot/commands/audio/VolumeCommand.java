package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.audio.GuildMusicManager;
import life.nekos.bot.audio.VoiceHandler;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPrems;
import static life.nekos.bot.commons.checks.UserChecks.isDonor;

@CommandDescription(
        name = "Volume",
        triggers = {"Volume", "v", "vol"},
        attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "dm")},
        description = "sets the volume.\nThis is a Patreon only command"
)
public class VolumeCommand implements Command {
    public void execute(Message message, String args) {
        Models.statsUp("volume");
        if (!isDonor(message.getAuthor())) {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setAuthor(
                                            message.getJDA().getSelfUser().getName(),
                                            message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                            message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setDescription(
                                            Formats.error(
                                                    " Sorry this command is only available to our Patrons.\n"
                                                            + message
                                                            .getJDA()
                                                            .asBot()
                                                            .getShardManager()
                                                            .getEmoteById(475801484282429450L).getAsMention()

                                                            + "[Join Today](https://www.patreon.com/bePatron?c=1830314&rid=2826101)"))
                                    .build())
                    .queue();
            return;
        }
        if (!message.getMember().getVoiceState().inVoiceChannel()) {
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

            String[] arg = args.trim().split(" ");
            int vol = musicManager.player.getVolume();
            if (args.length() > 0) {
                try {
                    int nvol = Integer.parseInt(arg[0]);
                    if (nvol > 100) nvol = 100;
                    int nvolf = nvol;
                    musicManager.player.setVolume(nvol);
                    System.out.println("???");
                    message
                            .getChannel()
                            .sendMessage(
                                    MessageFormat.format(
                                            "I set the volume to **{2}** {0}, nya~ {1}",
                                            Formats.getVolEmote(nvol), Formats.getCat(), nvol))
                            .queue(
                                    message1 -> {
                                        if (canReact(message1)) {
                                            message1
                                                    .addReaction(
                                                            message1
                                                                    .getJDA().asBot().getShardManager()
                                                                    .getEmoteById(Formats.getEmoteID(Formats.getVolEmote(nvolf))))
                                                    .queue();
                                        }
                                    });

                } catch (NumberFormatException e) {
                    message
                            .getChannel()
                            .sendMessage(
                                    Formats.error(
                                            MessageFormat.format(
                                                    "oh? {0} dun look like a valid number to me! {1}",
                                                    arg[0], Formats.getCat())))
                            .queue();
                    return;
                }
            }
            if (args.isEmpty()) {
                message
                        .getChannel()
                        .sendMessage(
                                MessageFormat.format(
                                        "My volume is currently {1}**{0}** nya~ {2}",
                                        vol, Formats.getVolEmote(vol), Formats.getCat()))
                        .queue(
                                message2 -> {
                                    if (canReact(message2)) {
                                        message2
                                                .addReaction(
                                                        message2
                                                                .getJDA().asBot().getShardManager()
                                                                .getEmoteById(Formats.getEmoteID(Formats.getVolEmote(vol))))
                                                .queue();
                                    }
                                });
            }

        } else {
            message
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
                            message3 -> {
                                if (canReact(message3)) {
                                    message3
                                            .addReaction(
                                                    message3.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                                }
                            });
        }
    }
}
