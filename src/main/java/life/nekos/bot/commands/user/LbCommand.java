package life.nekos.bot.commands.user;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import com.rethinkdb.net.Cursor;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static life.nekos.bot.NekoBot.waiter;

@CommandDescription(
        name = "leaderboard",
        triggers = {"lb", "leaderboard", "top", "ranks"},
        attributes = {@CommandAttribute(key = "user")},
        description = "Global leaderboard, lb nekos or lb levels"
)
public class LbCommand implements Command {
    private Paginator.Builder pbuilder =
            new Paginator.Builder()
                    .setColumns(1)
                    .setItemsPerPage(4)
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
    public void execute(Message message, String args) {
        Models.statsUp("leaderboard");
        if (!BotChecks.canReact(message)) {
            message
                    .getChannel()
                    .sendMessage(
                            Formats.error("I can't, nya~ I don't have reaction permission " + Formats.getCat()))
                    .queue();
            return;
        }
        message.addReaction("\uD83E\uDD47").queue();
        if (args.length() == 0) {
            message.getChannel().sendMessage(":x: **Use `lb nekos` or `lb levels`**").queue();
            return;
        }
        String[] arg = args.trim().split(" ");
        switch (arg[0]) {
            case "nekos":
                int page = 1;
                if (arg.length > 1 && !arg[1].isEmpty()) {
                    try {
                        page = Integer.parseInt(arg[1]);
                    } catch (NumberFormatException e) {
                        message.getChannel().sendMessage("`" + arg[1] + "` is not a valid Number!").queue();
                        return;
                    }
                }
                pbuilder.clearItems();
                Cursor TopNekos = Models.getTopNekos();
                for (Object doc : TopNekos) {
                    pbuilder.addItems(
                            MessageFormat.format(
                                    "\n{3} **__Name__**: **{0}**\n**{2} __Nekos__**: **{1}**\n",
                                    ((HashMap) doc)
                                            .get("name")
                                            .toString()
                                            .replace("`", "")
                                            .replace("*", "")
                                            .replace("_", ""),
                                    ((HashMap) doc).get("nekos"),
                                    Formats.NEKO_V_EMOTE,
                                    Formats.USER_EMOTE));
                }
                TopNekos.close();
                Paginator p =
                        pbuilder
                                .setColor(Colors.getEffectiveColor(message))
                                .setText(
                                        Formats.MAGIC_EMOTE
                                                + " **Global leaderboard for Nekos** "
                                                + Formats.NEKO_C_EMOTE)
                                .setUsers(message.getAuthor())
                                .build();
                p.paginate(message.getChannel(), page);
                break;

            case "levels":
                int page2 = 1;
                if (arg.length > 1 && !arg[1].isEmpty()) {
                    try {
                        page2 = Integer.parseInt(arg[1]);
                    } catch (NumberFormatException e) {
                        message.getChannel().sendMessage("`" + arg[1] + "` Is not a valid Number!").queue();
                        return;
                    }
                }
                pbuilder.clearItems();
                Cursor TopExp = Models.getTopExp();
                for (Object doc : TopExp) {
                    pbuilder.addItems(
                            MessageFormat.format(
                                    "\n{5} **__Name__**: **{0}**\n{3} **__Level__**: **{1}** \n{4} **__Experience__**: **{2}**\n",
                                    ((HashMap) doc)
                                            .get("name")
                                            .toString()
                                            .replace("`", "")
                                            .replace("*", "")
                                            .replace("_", ""),
                                    ((HashMap) doc).get("level"),
                                    ((HashMap) doc).get("exp"),
                                    Formats.MAGIC_EMOTE,
                                    Formats.LEVEL_EMOTE,
                                    Formats.USER_EMOTE));
                }
                TopExp.close();
                Paginator p2 =
                        pbuilder
                                .setColor(Colors.getEffectiveColor(message))
                                .setText(
                                        Formats.MAGIC_EMOTE
                                                + " **Global leaderboard for Levels** "
                                                + Formats.LEVEL_EMOTE)
                                .setUsers(message.getAuthor())
                                .build();
                p2.paginate(message.getChannel(), page2);
                break;
            default:
                message
                        .getChannel()
                        .sendMessage(Formats.error("**Use `lb nekos` or `lb levels`**"))
                        .queue();
                break;
        }
    }
}
