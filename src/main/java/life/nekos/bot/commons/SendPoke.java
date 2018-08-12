package life.nekos.bot.commons;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.db.Models;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
// TODO check msg prems

/**
 * Created by Tom on 10/9/2017.
 */
public class SendPoke {

	private static boolean isMatch(String msg) {
		if (!msg.toLowerCase().startsWith(">")) {
			return false;
		}
		int match = FuzzySearch.ratio(">catch", msg.toLowerCase());
		return (match > 85);
	}

	public static void send(Message event, boolean sent) {
		if (!BotChecks.canSend(event)) {
			return;
		}
		int randomNum = ThreadLocalRandom.current().nextInt(0, 350 + 1);
		Pokemon poke = NekoBot.pokeApi.getPokemon(randomNum);
		try {
			event
					.getChannel()
					.sendMessage(
							new EmbedBuilder()
									.setFooter(
											Formats.getCat()
													+ " A wild " + poke.getName() + " has appeared\nUse >catch to catch it before it gets away \\o/",
											event.getJDA().getSelfUser().getEffectiveAvatarUrl())
									.setColor(Colors.getEffectiveColor(event))
									.setImage("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + poke.getId() + ".png")
									.build())
					.queue(
							nekomsg -> {
								EventWaiter waiter = NekoBot.waiter;
								waiter.waitForEvent(
										MessageReceivedEvent.class,
										e ->
												(!sent || e.getAuthor() != event.getAuthor())
														&& isMatch(e.getMessage().getContentRaw().toLowerCase())
														&& e.getTextChannel().equals(event.getTextChannel()),
										e -> {
											nekomsg.delete().queue();
											event
													.getChannel()
													.sendMessage(
															MessageFormat.format(
																	"{0} caught a {2} {1}",
																	e.getGuild().getMember(e.getAuthor()).getEffectiveName(),
																	Formats.getCat(), poke.getName()))
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
																					.filter(m -> isMatch(m.getContentRaw().toLowerCase()))
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
													.sendMessage("Time up! The Pokemon escaped!")
													.queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
										});
							});
		} catch (Exception e) {
			NekoBot.log.error(e.getMessage(), e);
		}
	}
}
