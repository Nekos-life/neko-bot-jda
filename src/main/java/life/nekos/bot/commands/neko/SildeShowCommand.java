package life.nekos.bot.commands.neko;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Slideshow;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.TimeUnit;

import static life.nekos.bot.NekoBot.waiter;
import static life.nekos.bot.commons.checks.BotChecks.canDelete;

@CommandDescription(
        name = "Nekos",
        triggers = {"nekos", "slideshow", "ss", "mew", "nyaa"},
        attributes = {@CommandAttribute(key = "neko")},
        description = "neko slideshow"
)
public class SildeShowCommand implements Command {
    private Slideshow.Builder sbuilder =
            new Slideshow.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

    @Override
    public void execute(Message event, String args) {
        if (!canDelete(event)) {
            event
                    .getTextChannel()
                    .sendMessage(Formats.error("You lack the `manage_messgaes` permission"))
                    .queue();
            return;

        }
        StringBuilder urls = new StringBuilder();
        Models.statsUp("nekos");
        if (canDelete(event)) {
            event.delete().queue();
        }
        for (int i = 0; i < 20; ++i) {
            try {
                urls.append(Nekos.getNeko()).append(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Slideshow s =
                sbuilder
                        .setColor(Colors.getRndColor())
                        .setText(Formats.MAGIC_EMOTE + " Nekos \\o/ " + Formats.NEKO_C_EMOTE)
                        .setDescription(Formats.getCat())
                        .setUrls(urls.toString().split(","))
                        .setFinalAction(
                                message -> {
                                    message.clearReactions().queue();
                                    try {
                                        message
                                                .editMessage(
                                                        new EmbedBuilder()
                                                                .setImage(Nekos.getNeko())
                                                                .setColor(Colors.getRndColor())
                                                                .build())
                                                .queue();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                        .build();
        s.display(event.getChannel());
    }
}
