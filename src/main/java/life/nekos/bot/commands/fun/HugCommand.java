package life.nekos.bot.commands.fun;
/**
 * Created by Tom on 9/18/2017.
 */

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

@CommandDescription(
        name = "hug",
        triggers = "hug",
        attributes = {@CommandAttribute(key = "dm", value = "true"), @CommandAttribute(key = "fun")},
        description = "Hug someone \\o/ ~hug @user @user2"
)
@SuppressWarnings("unchecked")
public class HugCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("hug");
        if (message.getMentionedUsers().isEmpty()) {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setDescription("Who do you want to hug?? " + Formats.NEKO_C_EMOTE)
                                    .build())
                    .queue();
            return;
        }

        List<User> mentionedUsers = message.getMentionedUsers();
        for (User user : mentionedUsers) {
            if (user == message.getJDA().getSelfUser()) {
                message.getChannel().sendMessage("Nyaaaaaaaaaa, nu dun hug mee~").queue();
                break;
            }
            if (user == message.getAuthor()) {
                message
                        .getChannel()
                        .sendMessage("oh? why you want to hug yourself? Find a friend nya~")
                        .queue();
                break;
            }
            String name = message.getGuild().getMember(user).getEffectiveName();
            message
                    .getChannel()
                    .sendMessage("\\o/")
                    .queue(
                            msg -> {
                                try {
                                    msg.editMessage(
                                            new EmbedBuilder()
                                                    .setDescription(
                                                            String.format(
                                                                    "%s You got a hug from %s %s",
                                                                    name, msg.getMember().getEffectiveName(), Nekos.getCat()))
                                                    .setColor(
                                                            Colors.getEffectiveMemberColor(
                                                                    message.getGuild().getMember(user)))
                                                    .setImage(Nekos.getHug())
                                                    .setTitle(Formats.getCat())
                                                    .build())
                                            .queue();

                                } catch (Exception e) {
                                    NekoBot.log.error("broken hug ", e);
                                    if (BotChecks.canReact(msg)) {
                                        msg.addReaction("🚫").queue();
                                    }
                                }
                            });
        }
    }
}
