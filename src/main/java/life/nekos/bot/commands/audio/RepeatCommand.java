package life.nekos.bot.commands.audio;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.audio.AudioHandler;
import life.nekos.bot.audio.GuildMusicManager;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

import static life.nekos.bot.commons.checks.BotChecks.canReact;
import static life.nekos.bot.commons.checks.UserChecks.audioPrems;
import static life.nekos.bot.commons.checks.UserChecks.isDonor;

@CommandDescription(
		name = "Repeat",
		triggers = {"loop", "repeat"},
		attributes = {@CommandAttribute(key = "music"), @CommandAttribute(key = "dm")},
		description =
				"Set repeat for a track, or --all to loop the entire queue\nThis is a Patreon only command"
)
public class RepeatCommand implements Command {
    @Override
    public void execute(Message message, String args) {
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
	    GuildMusicManager musicManager = AudioHandler.getMusicManager(message.getGuild());
        String[] arg = args.trim().split(" ");
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
        if (musicManager.scheduler.queue.isEmpty()) {
	        message
                    .getChannel()
                    .sendMessage("oh? The playlist is empty! play something first nya~")
                    .queue(
		                    m -> {
			                    if (canReact(m)) {
				                    m
                                            .addReaction(
		                                            m.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.PLAY_EMOTE)))
                                            .queue();
                                }
                            });
            return;
        }
        AudioTrack Track = musicManager.player.getPlayingTrack();
	    if (!audioPrems(message, Track)) {
		    message
                    .getChannel()
                    .sendMessage(
                            Formats.error(
                                    "nu nya!~, You don't have permission to do this. " + Formats.NEKO_C_EMOTE))
                    .queue();
            return;
        }

        if (arg.length >= 1) {
            if (arg[0].equalsIgnoreCase("all")) {
                musicManager.scheduler.setLoop(false);
                musicManager.scheduler.setLoopall(
		                !AudioHandler.getMusicManager(message.getGuild()).scheduler.isLoopall());
	            message
                        .getChannel()
                        .sendMessage(
                                "Alright nya, I have "
                                        + (musicManager.scheduler.isLoopall()
                                        ? "set the current queue to repeat all tracks "
                                        + Formats.LOOO_ALL_EMOTE
                                        + " "
                                        : "disabled repeat all ")
                                        + Formats.getCat())
                        .queue();
                return;
            }
        }
        {
            musicManager.scheduler.setLoopall(false);
            musicManager.scheduler.setLoop(
		            !AudioHandler.getMusicManager(message.getGuild()).scheduler.isLoop());
	        message
                    .getChannel()
                    .sendMessage(
                            "Alright nya, I have "
                                    + (musicManager.scheduler.isLoop()
                                    ? "set the current track to repeat " + Formats.LOOP_EMOTE + " "
                                    : "disabled repeat ")
                                    + Formats.getCat())
                    .queue();
        }
    }
}
