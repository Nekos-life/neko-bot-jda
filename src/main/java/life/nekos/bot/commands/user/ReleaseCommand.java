package life.nekos.bot.commands.user;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.SendNeko;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.entities.Message;

@CommandDescription(
        name = "release",
        triggers = {"release", "free", "catch"},
        attributes = {@CommandAttribute(key = "user")},
        description =
                "Releases one of your nekos for others to catch  >.< (you cant not catch a neko you relesaed)"
)
public class ReleaseCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("release");
        Models.setBal(message.getAuthor().getId(), Models.getBal(message.getAuthor().getId()) - 1);
        SendNeko.send(message, true);
        if (BotChecks.canDelete(message)) {
            message.delete().queue();
        }
    }
}
