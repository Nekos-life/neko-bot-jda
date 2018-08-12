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
import net.dv8tion.jda.core.entities.User;

import java.util.List;

@CommandDescription(
        name = "kiss",
        triggers = "kiss",
        attributes = {@CommandAttribute(key = "fun")},
        description = "Kiss someone \\o/  ~kiss @user @user"
)
public class KissCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("kiss");
        if (message.getMentionedUsers().isEmpty()) {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setDescription("You must mention a user")
                                    .build())
                    .queue();
            return;
        }
        List<User> mentionedUsers = message.getMentionedUsers();
        message.addReaction("\uD83D\uDC8B").queue();
        for (User user : mentionedUsers) {
            String name = message.getGuild().getMember(user).getEffectiveName();
            try {
                message
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setDescription(
                                                String.format(
                                                        "%s You got a kiss from %s owo",
                                                        name, message.getMember().getEffectiveName()))
                                        .setColor(Colors.getEffectiveMemberColor(message.getGuild().getMember(user)))
                                        .setImage(Nekos.getKiss())
                                        .build())
                        .queue();
            } catch (Exception e) {
                NekoBot.log.error("kiss broken? ", e);
            }
        }
    }
}
