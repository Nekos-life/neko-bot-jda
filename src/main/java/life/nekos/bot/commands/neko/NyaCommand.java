package life.nekos.bot.commands.neko;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

@CommandDescription(
        name = "nya",
        triggers = "nya",
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "neko")},
        description = "mew!"
)
public class NyaCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("nya");
        message
                .getChannel()
                .sendMessage(
                        MessageFormat.format(
                                "{0}, Mew!!~ {1}", message.getAuthor().getAsMention(), Formats.getCat()))
                .queue();
    }
}
