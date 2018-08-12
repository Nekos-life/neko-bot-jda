package life.nekos.bot.commands.bot;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.Misc;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

@CommandDescription(
        name = "invite",
        triggers = {"invite", "join", "oauth", "link", "links", "support"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "bot")},
        description = "bot and support guild links -.o"
)
public class InviteCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {
        Models.statsUp("invite");
        trigger
                .getTextChannel()
                .sendMessage(
                        new EmbedBuilder()
                                .setColor(Colors.getRndColor())
                                .setAuthor(
                                        trigger.getJDA().getSelfUser().getName(),
                                        trigger.getJDA().getSelfUser().getEffectiveAvatarUrl(),
                                        trigger.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                .setDescription(Formats.LING_MSG)
                                .setFooter(
                                        MessageFormat.format(
                                                "Requested by {0} | {1}",
                                                trigger.getMember().getEffectiveName(), Misc.now()),
                                        trigger.getAuthor().getEffectiveAvatarUrl())
                                .build())
                .queue();
    }
}
