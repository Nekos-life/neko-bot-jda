package life.nekos.bot.commands.nsfw;

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
        name = "pussy",
        triggers = {"pussy"},
		attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "nsfw"), @CommandAttribute(key = "PayWall")},
        description = "random pussy uwu"
)
public class PussyCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {
        Models.statsUp("pussy");
        try {
            trigger
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setDescription("uwu pussy")
                                    .setColor(Colors.getEffectiveColor(trigger))
                                    .setImage(Nekos.makeReqAndGetResAsString("pussy","url"))
                                    .build())
                    .queue();
        } catch (Exception e) {
            trigger.getChannel().sendMessage(Formats.error("oh? something broken nya!")).queue();
            NekoBot.log.error("lewd command broken? ", e);
        }
    }
}
