package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

@CommandDescription(
        name = "why",
        triggers = {"why", "huh", "how come", "Hmmmmm"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "fun")},
        description = "random why?"
)
public class WhyCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("why");
        try {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setAuthor(
                                            "Why??",
                                            message.getJDA().asBot().getInviteUrl(),
                                            message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .setDescription(Nekos.getWhy())
                                    .build())
                    .queue();
        } catch (Exception e) {
            NekoBot.log.error("why broken? ", e);
        }
    }
}
