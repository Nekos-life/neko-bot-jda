package life.nekos.bot.handlers;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandHandler;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.checks.MiscChecks;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static life.nekos.bot.commons.checks.UserChecks.isDonor;
import static life.nekos.bot.commons.db.Models.hasPrefix;

/**
 * Created by Repulser https://github.com/Repulser
 */
public class MessageHandler extends ListenerAdapter {
	private static final ThreadGroup threadGroup = new ThreadGroup("Command Executor");
	private static final Executor commandsExecutor =
			Executors.newCachedThreadPool(r -> new Thread(threadGroup, r, "Command Pool"));

	static {
		threadGroup.setMaxPriority(Thread.MAX_PRIORITY);
	}

	private final CommandHandler handler;
	public String str;

	public MessageHandler(CommandHandler handler) {
		this.handler = handler;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onMessageReceived(MessageReceivedEvent event) {
    /*if (Objects.equals(event.getTextChannel().getId(), "440667148315262978") && !BotChecks.noBot(event.getMessage())){
    	 StringBuilder info = new StringBuilder();
    	 info.append(MessageFormat.format("################### Users in {0} ###################\n",event.getGuild().getName()));
        EventHandler.getHOME()
            .getMembers()
            .forEach(
                member -> {
                  info.append("##############################################################\n");
                  info.append("Name: ")
                      .append(member.getUser().getName()).append("#").append(member.getUser().getDiscriminator())
                      .append("\n")
                    .append("Status: \n").append(member.getOnlineStatus().getKey()).append("\n");
                    if (member.getGame() != null){
                    	info.append("Game: \n").append(member.getGame().getName()).append("\n");
                    }
                      info.append("Permissions: \n");
                  member
                      .getPermissions()
                      .forEach(
                          permission -> {
                            info.append(permission.getName()).append("\n");
                          });
                  info.append("\nRoles: \n");
                  member
                      .getRoles()
                      .forEach(
                          role -> {
                            info.append("Role : *").append(role.getName()).append("*").append("\n").append("Role Permissions:").append("\n");
                            role.getPermissions().forEach(r -> info.append(r.getName()).append("\n"));
                          });
                  info.append(
                      "\n##############################################################\n");
                });
    	Misc.wump(info.toString());
    }*/

		if (!EventHandler.getREADY()) {
			return;
		}
		commandsExecutor.execute(
				() -> {
					JDA jda = event.getJDA();
					if (BotChecks.noBot(event.getMessage())) return;
					if (!Models.hasUser(event.getMessage())) {
						Models.newUser(event.getMessage());
					}
					if (!BotChecks.isDm(event.getMessage())) {
						if (!Models.hasGuild(event.getGuild())) {
							Models.newGuild(event.getGuild());
						}
						Models.updateUser(event.getMessage());
						Models.spwNeko(event);
					}
					String prefix = Models.getPrefix(event.getMessage());
					String message = event.getMessage().getContentRaw();
					if (message.startsWith(jda.getSelfUser().getAsMention())
							& message.length() == jda.getSelfUser().getAsMention().length()) {
						try {
							event
									.getChannel()
									.sendMessage(
											event.getAuthor().getAsMention()
													+ ", My prefix is `"
													+ prefix
													+ "`\n`"
													+ prefix
													+ "help` to see my commands. Mew!!")
									.queue();
						} catch (Exception e) {
							NekoBot.log.error("shit ", e);
						}
						return;
					}

					if (!hasPrefix(event.getMessage())) return;

					String[] splitMessage = message.split("\\s+", 2);
					String commandString;
					try {
						if (message.startsWith(prefix)) {
							commandString = splitMessage[0].substring(prefix.length());
						} else {
							commandString = splitMessage[0].substring(jda.getSelfUser().getAsMention().length());
						}
					} catch (Exception e) {
						return;
					}
					Command command = handler.findCommand(commandString.toLowerCase());
					if (command == null) {
						if (message.startsWith(jda.getSelfUser().getAsMention())) {
							try {
								event
										.getChannel()
										.sendMessage(
												event.getAuthor().getName()
														+ ", "
														+ Nekos.getChat(false, message.replace("@", "")).replace("@", "\\@\\"))
										.queue();
							} catch (Exception e) {
								NekoBot.log.error("shit ", e);
							}
							return;
						} else return;
					}

					if (command.hasAttribute("OwnerOnly") && !MiscChecks.isOwner(event.getMessage())) {
						return;
					}

					if (command.hasAttribute("PayWall") && !isDonor(event.getAuthor())) {
						event
								.getChannel()
								.sendMessage(
										new EmbedBuilder()
												.setAuthor(
														event.getJDA().getSelfUser().getName(),
														event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
														event.getJDA().getSelfUser().getEffectiveAvatarUrl())
												.setColor(Colors.getEffectiveColor(event.getMessage()))
												.setDescription(
														Formats.error(
																" Sorry this command is only available to our Patrons.\n"
																		+ event
																		.getJDA()
																		.asBot()
																		.getShardManager()
																		.getEmoteById(475801484282429450L)
																		.getAsMention()
																		+ "[Join today](https://www.patreon.com/bePatron?c=1830314&rid=2826101)"))
												.build())
								.queue();
						return;
					}

					if (BotChecks.isDm(event.getMessage()) && !command.hasAttribute("dm")) {
						event
								.getChannel()
								.sendMessage(Formats.error("Nu! nya, you can only use this in a guild"))
								.queue();
						return;
					}

					if (!BotChecks.isDm(event.getMessage())) {
						if (!BotChecks.canSend(event.getMessage())) {
							return;
						}
						if (!BotChecks.canEmbed(event.getMessage())) {
							event
									.getChannel()
									.sendMessage(
											Formats.error(
													"Nu! nya, I can't do this i do not have permission to use embeds"))
									.queue();
							return;
						}
					}

					if(!BotChecks.isDm(event.getMessage())) {
						if (!event.getTextChannel().isNSFW() && command.hasAttribute("nsfw")) {
							event
									.getChannel()
									.sendMessage(
											new EmbedBuilder()
													.setColor(Colors.getEffectiveColor(event.getMessage()))
													.setDescription(
															"Lewd nekos are shy nya, They can only be found in Discord nsfw channels")
													.build())
									.queue();
							return;
						}
					}

					try {
						Formats.logCommand(event.getMessage());
						handler.execute(
								command, event.getMessage(), splitMessage.length > 1 ? splitMessage[1] : "");
					} catch (Exception e) {
						NekoBot.log.error("Error on command", e);
						event.getMessage().addReaction("\uD83D\uDEAB").queue();
					}
				});
	}

	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		if (!EventHandler.getREADY()) {
			return;
		}

		commandsExecutor.execute(
				() -> {
					if (BotChecks.noBot(event.getMessage()) || BotChecks.isDm(event.getMessage())) return;
					String prefix = Models.getPrefix(event.getMessage());
					String message = event.getMessage().getContentRaw();
					if (!message.startsWith(prefix)) return;
					String[] splitMessage = message.split("\\s+", 2);
					String commandString;
					try {
						commandString = splitMessage[0].substring(prefix.length());
					} catch (Exception e) {
						return;
					}
					Command command = handler.findCommand(commandString.toLowerCase());
					if (command == null) return;
					if (command.hasAttribute("OwnerOnly") && !MiscChecks.isOwner(event.getMessage())) {
						return;
					}
					if (BotChecks.isDm(event.getMessage()) && !command.hasAttribute("dm")) {
						event
								.getChannel()
								.sendMessage(Formats.error("Nu! nya, you can only use this in a guild"))
								.queue();
						return;
					}
					if (command.hasAttribute("PayWall") && !isDonor(event.getAuthor())) {
						event
								.getChannel()
								.sendMessage(
										new EmbedBuilder()
												.setAuthor(
														event.getJDA().getSelfUser().getName(),
														event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
														event.getJDA().getSelfUser().getEffectiveAvatarUrl())
												.setColor(Colors.getEffectiveColor(event.getMessage()))
												.setDescription(
														Formats.error(
																" Sorry this command is only available to our Patrons.\n"
																		+ event
																		.getJDA()
																		.asBot()
																		.getShardManager()
																		.getEmoteById(475801484282429450L)
																		.getAsMention()
																		+ "[Join today](https://www.patreon.com/bePatron?c=1830314&rid=2826101)"))
												.build())
								.queue();
						return;
					}
					if(!BotChecks.isDm(event.getMessage())) {
						if (!event.getTextChannel().isNSFW() && command.hasAttribute("nsfw")) {
							event
									.getChannel()
									.sendMessage(
											new EmbedBuilder()
													.setColor(Colors.getEffectiveColor(event.getMessage()))
													.setDescription(
															"Lewd nekos are shy nya, They can only be found in Discord nsfw channels")
													.build())
									.queue();
							return;
						}
					}
					try {
						Formats.logCommand(event.getMessage());
						handler.execute(
								command, event.getMessage(), splitMessage.length > 1 ? splitMessage[1] : "");
					} catch (Exception e) {
						NekoBot.log.error("Error on command", e);
					}
				});
  }
}
