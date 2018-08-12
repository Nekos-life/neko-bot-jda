package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.AlexApis;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

@CommandDescription(
        name = "coffee",
        triggers = "coffee",
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "fun")},
        description = "random coffee ^^"
)
@SuppressWarnings("unchecked")
public class CoffeeCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {
        Models.statsUp("coffee");
        try {
            trigger
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setDescription("coffee \\o/")
                                    .setColor(Colors.getEffectiveColor(trigger))
                                    .setImage(AlexApis.getCoffee())
                                    .setFooter("Api provided by AlexFlipnote", null)
                                    .build())
                    .queue();
        } catch (Exception e) {
            trigger.getChannel().sendMessage(Formats.error("oh nu, something went wrong :(")).queue();
            NekoBot.log.error("oh some neko error :/ ", e);
        }
    }
}
