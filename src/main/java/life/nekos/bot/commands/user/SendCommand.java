package life.nekos.bot.commands.user;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@CommandDescription(
        name = "SendCommand",
        triggers = {"send"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly", value = "no"),
                @CommandAttribute(key = "dm", value = "no"),
        },
        description = "SendCommand"
)
public class SendCommand implements Command {
    @Override
    public void execute(Message event, String args) {
        Models.statsUp("send");
        User author = event.getAuthor();
        TextChannel ch = event.getTextChannel();
        List<User> u = event.getMentionedUsers();
        List<User> users = new ArrayList<>(u);
        String[] arg = args.trim().split(" ");
        if (users.isEmpty()) {
            users.add(author);
        }
        if (args.length() == 0) {
            // no args send com help
        }
        switch (arg[0]) {
            case "neko":
                for (User m : users) {
                    m.openPrivateChannel()
                            .queue(
                                    (PrivateChannel pm) -> {
                                        try {
                                            String neko = Nekos.getNeko();
                                            pm.sendMessage(
                                                    new EmbedBuilder()
		                                                    .setDescription(Formats.NEKO_C_EMOTE)
                                                            .setTitle(
                                                                    MessageFormat.format(
                                                                            "hey {0}, {1} has sent you a {2}",
                                                                            m.getName(), event.getAuthor().getName(), arg[0]),
                                                                    neko)
                                                            .setColor(Colors.getDominantColor(m))
                                                            .setImage(neko)
                                                            .build())
                                                    .queue(
                                                            null,
                                                            b ->
                                                                    ch.sendMessage(
                                                                            MessageFormat.format(
                                                                                    "hey, This {0} {1} has me blocked or there filter turned on \uD83D\uDD95",
                                                                                    "whore", m.getName()))
                                                                            .queue());

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    e -> {
                                        e.printStackTrace();
                                    });
                }

                ch.sendMessage(MessageFormat.format("Good job {0}", event.getAuthor().getAsMention()))
                        .queue();
                break;
	        case "lewd":
		        for (User m : users) {
			        m.openPrivateChannel()
					        .queue(
							        (PrivateChannel pm) -> {
								        try {
									        String neko = Nekos.getNeko();
									        pm.sendMessage(
											        new EmbedBuilder()
													        .setDescription(Formats.NEKO_C_EMOTE)
													        .setTitle(
															        MessageFormat.format(
																	        "hey {0}, {1} has sent you a {2}",
																	        m.getName(), event.getAuthor().getName(), arg[0]),
															        neko)
													        .setColor(Colors.getDominantColor(m))
													        .setImage(neko)
													        .build())
											        .queue(
													        null,
													        b ->
															        ch.sendMessage(
																	        MessageFormat.format(
																			        "hey, This {0} has me blocked or there filter turned on \uD83D\uDD95",
																			        m.getName()))
																	        .queue());

								        } catch (Exception e) {
									        e.printStackTrace();
								        }
							        },
							        Throwable::printStackTrace);
		        }

		        ch.sendMessage(MessageFormat.format("Good job {0}", event.getAuthor().getAsMention()))
				        .queue();
		        break;
            default:
                // send com help

        }
    }
}
