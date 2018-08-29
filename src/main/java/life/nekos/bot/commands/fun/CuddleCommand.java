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
        name = "cuddle",
        triggers = "cuddle",
        attributes = {@CommandAttribute(key = "dm", value = "true"), @CommandAttribute(key = "fun")},
        description = "cuddle someone \\o/ ~cuddle @user @user2"
)
@SuppressWarnings("unchecked")
public class CuddleCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("cuddle");
        if (message.getMentionedUsers().isEmpty()) {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setDescription("Who do you want to cuddle?? " + Formats.NEKO_C_EMOTE)
                                    .build())
                    .queue();
            return;
        }

        List<User> mentionedUsers = message.getMentionedUsers();
        message.addReaction("\uD83E\uDD17").queue();
        for (User user : mentionedUsers) {
            if (user == message.getJDA().getSelfUser()) {
                message.getChannel().sendMessage("Nyaaaaaaaaaa, nu dun touch mee~").queue();
                break;
            }
            if (user == message.getAuthor()) {
                message
                        .getChannel()
                        .sendMessage("oh? why you want to cuddle yourself? Find a friend nya~")
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
                                                                    "%s You got cuddles from %s %s",
                                                                    name, message.getMember().getEffectiveName(), Formats.getCat()))
                                                    .setColor(
                                                            Colors.getEffectiveMemberColor(msg.getGuild().getMember(user)))
                                                    .setImage(Nekos.getCuddle())
                                                    .build())
                                            .queue();
                                } catch (Exception e) {
                                    NekoBot.log.error("broken cuddle ", e);
                                    if (BotChecks.canReact(msg)) {
                                        msg.addReaction("🚫").queue();
                                    }
                                }
                            });
        }
    }
}
