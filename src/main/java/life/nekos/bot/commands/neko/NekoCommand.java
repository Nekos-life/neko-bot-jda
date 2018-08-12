package life.nekos.bot.commands.neko;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

@CommandDescription(
        name = "neko",
        triggers = "neko",
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "neko")},
        description = "random nekos owO"
)
@SuppressWarnings("unchecked")
public class NekoCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {
        Models.statsUp("neko");
        try {
            trigger
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setDescription("Nekos \\o/")
                                    .setColor(Colors.getEffectiveColor(trigger))
                                    .setImage(Nekos.getNeko())
                                    .build())
                    .queue();
        } catch (Exception e) {
            trigger.getChannel().sendMessage(Formats.error("oh nu, something went wrong :(")).queue();
            NekoBot.log.error("oh some neko error :/ ", e);
        }
    }
}
