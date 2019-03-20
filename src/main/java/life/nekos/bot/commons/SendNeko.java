package life.nekos.bot.commons;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.db.Models;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
// TODO check msg prems
/**
 * Created by Tom on 10/9/2017.
 */
public class SendNeko {

	private static boolean isMatch(String msg, String word) {
		int match = FuzzySearch.ratio(word, msg.toLowerCase());
		return (match > 95);
	}

	private static String canLewd(Message msg) {
		if (msg.getTextChannel().isNSFW()) {
			try {
				return Nekos.getLewd();
			} catch (Exception e) {
				NekoBot.log.error("shit ", e);
				return null;
			}
		} else {
			try {
				return Nekos.getNeko();
			} catch (Exception e) {
				NekoBot.log.error("shit ", e);
				return null;
			}
		}
	}

	public static void send(Message event, boolean sent) {
		if (!BotChecks.canSend(event)) {
			return;
		}
        String[] prefixes = {"!",".",">","<","n","/"};
        String[] sufixes = {"catch","gimme","mine","yus","owo","give","nya","mew","nyaaa"};
		String sufix = sufixes[new Random().nextInt(sufixes.length)];
        String prefix = prefixes[new Random().nextInt(prefixes.length)];
        String catchStr = prefix+sufix;
		try {
			event
					.getChannel()
					.sendMessage(
							new EmbedBuilder()
									.setFooter(
											Formats.getCat()
													+ " A wild neko has appeared\nUse "+ catchStr +" to catch it before it gets away \\o/",
											event.getJDA().getSelfUser().getEffectiveAvatarUrl())
									.setColor(Colors.getEffectiveColor(event))
									.setImage(canLewd(event))
									.build())
					.queue(
							nekomsg -> {
								EventWaiter waiter = NekoBot.waiter;
								waiter.waitForEvent(
										MessageReceivedEvent.class,
										e ->
												(!sent || (e.getAuthor() != event.getAuthor()) || !e.getAuthor().isBot())
														&& isMatch(e.getMessage().getContentRaw().toLowerCase(), catchStr)
														&& e.getTextChannel().equals(event.getTextChannel()),
										e -> {
											nekomsg.delete().queue();
											event
													.getChannel()
													.sendMessage(
															MessageFormat.format(
																	"{0} caught a Neko {1}",
																	e.getGuild().getMember(e.getAuthor()).getEffectiveName(),
																	Formats.getCat()))
													.queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
											Models.nekoCaught(e.getAuthor());
											if (BotChecks.canDelete(e.getMessage())) {
												event
														.getTextChannel()
														.getHistory()
														.retrievePast(35)
														.queueAfter(
																15,
																TimeUnit.SECONDS,
																ms -> {
																	List<Message> spam =
																			ms.stream()
																					.filter(m -> isMatch(m.getContentRaw().toLowerCase(), catchStr))
																					.collect(Collectors.toList());
																	if (spam.size() < 2 && spam.size() > 0) {
																		spam.get(0).delete().queue();
																		return;
																	}
																	if (spam.size() == 0) {
																		return;
																	}
																	event.getTextChannel().deleteMessages(spam).queue();
																});
											}
										},
										2,
										TimeUnit.MINUTES,
										() -> {
											nekomsg.delete().queue();
											event
													.getChannel()
													.sendMessage("Time up! The Neko escaped!")
													.queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
										});
							});
		} catch (Exception e) {
			NekoBot.log.error(e.getMessage(), e);
		}
	}
}
