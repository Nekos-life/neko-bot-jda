package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import static life.nekos.bot.commons.Misc.UA;

@CommandDescription(
		name = "avatar",
		triggers = {"avatar", "ava", "pfp", "avi"},
		attributes = {@CommandAttribute(key = "user")},
		description =
				"\nShows your or a @users avatar\nAdd --dm to have the image sent to you in a dm\nAdd --new for a new avatar for you and --new --nsfw for a new nsfw avatar for you"
)
public class AvatarCommand implements Command {

	@Override
	public void execute(Message message, String args) {
		Models.statsUp("avatar");
		if (args.toLowerCase().contains("--new")) {
			if (args.toLowerCase().contains("--nsfw")) {
				if (message.getTextChannel().isNSFW()) {
					try {
						message
								.getTextChannel()
								.sendMessage(
										new EmbedBuilder()
												.setDescription(
														Formats.info(
																MessageFormat.format(
																		"Hey {0}! Here is a new nsfw Avatar, Nya~ {1}",
																		message.getAuthor().getName(), Formats.getCat())))
												.setColor(Colors.getEffectiveColor(message))
												.setImage(Nekos.getNsfwAvatar())
												.build())
								.queue();
					} catch (Exception e) {
						NekoBot.log.error("shit ", e);
					}
					return;
				}
				if (args.toLowerCase().contains("--dm")) {
					message
							.getAuthor()
							.openPrivateChannel()
							.queue(
									pm -> {
										try {
											pm.sendMessage(
													new EmbedBuilder()
															.setDescription(
																	Formats.info(
																			MessageFormat.format(
																					"Hey {0}! Here is a new nsfw Avatar, Nya~ {1}",
																					message.getAuthor().getName(), Formats.getCat())))
															.setColor(Colors.getEffectiveColor(message))
															.setImage(Nekos.getNsfwAvatar())
															.build())
													.queue();
										} catch (Exception e) {
											NekoBot.log.error("broken? ", e);
										}
									});
					message.addReaction("📬").queue();
					return;
				}
				message
						.getTextChannel()
						.sendMessage(Formats.error("Nu, nya use this in a nsfw channel or add `--dm` to end"))
						.queue();
				return;
			}
			if (args.toLowerCase().contains("--dm")) {
				message
						.getAuthor()
						.openPrivateChannel()
						.queue(
								pm -> {
									try {
										pm.sendMessage(
												new EmbedBuilder()
														.setDescription(
																Formats.info(
																		MessageFormat.format(
																				"Hey {0}! Here is a new Avatar, Nya~ {1}",
																				message.getAuthor().getName(), Formats.getCat())))
														.setColor(Colors.getEffectiveColor(message))
														.setImage(Nekos.getAvatar())
														.build())
												.queue();
									} catch (Exception e) {
										NekoBot.log.error("broken? ", e);
									}
								});
				message.addReaction("📬").queue();
				return;
			}
			try {

				message
						.getTextChannel()
						.sendMessage(
								new EmbedBuilder()
										.setDescription(
												Formats.info(
														MessageFormat.format(
																"Hey {0}! Here is a new Avatar, Nya~ {1}",
																message.getAuthor().getName(), Formats.getCat())))
										.setColor(Colors.getEffectiveColor(message))
										.setImage(Nekos.getAvatar())
										.build())
						.queue();
			} catch (Exception e) {
				NekoBot.log.error("shit ", e);
			}
			return;
			// message.getTextChannel().sendMessage().queue();
		}
		User user;
		if (message.getMentionedUsers().isEmpty()) {
			user = message.getAuthor();
		} else {
			user = message.getMentionedUsers().get(0);
		}
		String name = MessageFormat.format("{0}.png", user.getName());
		try {
			URL url = new URL(user.getEffectiveAvatarUrl() + "?size=1024");
			URLConnection connection = url.openConnection();
			connection.setRequestProperty(UA[0], UA[1]);
			if (connection.getContentType().equals("image/gif"))
				name = MessageFormat.format("{0}.gif", user.getName());
			Message msg =
					new MessageBuilder()
							.append(MessageFormat.format("Avatar for {0} nya~", user.getName()))
							.build();
			if (args.length() != 0) {
				if (args.toLowerCase().endsWith("--dm")) {
					try {
						final String n = name;
						message
								.getAuthor()
								.openPrivateChannel()
								.queue(
										pm -> {
											try {

												pm.sendFile(connection.getInputStream(), n, msg).queue();
											} catch (Exception e) {
												e.printStackTrace();
											}
										});
						message.addReaction("📬").queue();
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			message.getTextChannel().sendFile(connection.getInputStream(), name, msg).queue();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}