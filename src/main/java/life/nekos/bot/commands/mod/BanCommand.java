package life.nekos.bot.commands.mod;

import com.github.rainestormee.jdacommand.Category;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import static life.nekos.bot.commons.checks.BotChecks.canBan;
import static life.nekos.bot.commons.checks.UserChecks.isAdmin;

@Category("guild")
@CommandDescription(
        name = "BanCommand",
        triggers = {"ban", "gtfo"},
        attributes = {
                @CommandAttribute(key = "dm", value = "no"),
                @CommandAttribute(key = "mod")
        },
        description = "Bans a asshat"
)
public class BanCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        if (message.getMentionedMembers().isEmpty()) {
            message.getTextChannel().sendMessage(Formats.error("You must tag a user")).queue();
            return;
        }
        if (!isAdmin(message)) {
            message.getTextChannel().sendMessage(Formats.error("You lack the `ban_members` permission")).queue();
            return;
        }
        if (!canBan(message)) {
            message.getTextChannel().sendMessage(Formats.error("I lack the `ban_members` permission")).queue();
            return;
        }
        Member m = message.getMentionedMembers().get(0);
        m.getGuild().getController().ban(m, 7).queue(mm -> {
            message.getTextChannel().sendMessage(Formats.info("Banned " + m.getEffectiveName() + ", Nya~")).queue();
        });
    }
}
