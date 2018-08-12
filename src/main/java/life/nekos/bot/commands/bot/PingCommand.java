package life.nekos.bot.commands.bot;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static life.nekos.bot.NekoBot.waiter;
import static life.nekos.bot.handlers.EventHandler.getShards;

@CommandDescription(
        name = "ping",
        triggers = "ping",
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "bot")},
        description = "Pong!"
)
public class PingCommand implements Command {
    private Paginator.Builder pbuilder =
            new Paginator.Builder()
                    .setColumns(1)
                    .setItemsPerPage(10)
                    .showPageNumbers(true)
                    .waitOnSinglePage(false)
                    .useNumberedItems(true)
                    .setFinalAction(
                            m -> {
                                try {
                                    m.clearReactions().queue();
                                } catch (PermissionException ex) {
                                    m.delete().queue();
                                }
                            })
                    .setEventWaiter(waiter)
                    .setTimeout(1, TimeUnit.MINUTES);

    @Override
    public void execute(Message trigger, String args) {
        Models.statsUp("ping");
        if (args.toLowerCase().contains("--all")) {
            List<String> pinglist = new ArrayList<>();
        Map<JDA, JDA.Status> s = getShards().getStatuses();
            pbuilder.clearItems();
            for (Map.Entry<JDA, net.dv8tion.jda.core.JDA.Status> e : s.entrySet()) {
                if (trigger.getJDA().getShardInfo().getShardId()
                        == e.getKey().getShardInfo().getShardId()) {
                    pbuilder.addItems(
                            MessageFormat.format(
                                    "Shard: {0}, Ping: {1}ms, Status: {2} (This Guild)\n",
                                    e.getKey().getShardInfo().getShardId(), e.getKey().getPing(), e.getValue()));
                } else {
                    pbuilder.addItems(
                            MessageFormat.format(
                                    "Shard: {0}, Ping: {1}ms, Status: {2}\n",
                                    e.getKey().getShardInfo().getShardId(), e.getKey().getPing(), e.getValue()));
                }
            }
            Paginator p =
                    pbuilder
                            .setColor(Colors.getEffectiveColor(trigger))
                            .setText(Formats.MAGIC_EMOTE + " **Global Pings** " + Formats.NEKO_C_EMOTE)
                            .setUsers(trigger.getAuthor())
                            .build();
            p.paginate(trigger.getChannel(), 1);
            return;
        }
	    trigger
			    .getChannel()
			    .sendMessage("Ping: \uD83C\uDFD3")
			    .queue(
					    m ->
							    m.editMessage(
									    "⏳ Ping: "
											    + trigger
											    .getCreationTime()
											    .until(m.getCreationTime(), ChronoUnit.MILLIS)
											    + "ms | \uD83D\uDC93 Web-socket: "
											    + trigger.getJDA().getPing()
											    + "ms")
									    .queue());
    }
}
