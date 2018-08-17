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
                                    .setDescription("Who do you want to kiss?? " + Formats.NEKO_C_EMOTE)
                                    .build())
                    .queue();
            return;
        }
        List<User> mentionedUsers = message.getMentionedUsers();
        message.addReaction("\uD83D\uDC8B").queue();
        for (User user : mentionedUsers) {
            if (user == message.getJDA().getSelfUser()) {
                message.getChannel().sendMessage("Nyaaaaaaaaaa, nu dun kiss mee~").queue();
                break;
            }
            if (user == message.getAuthor()) {
                message
                        .getChannel()
                        .sendMessage("oh? why you want to kiss yourself? Find a friend nya~")
                        .queue();
                break;
            }
            String name = message.getGuild().getMember(user).getEffectiveName();
            message
                    .getChannel()
                    .sendMessage("💕")
                    .queue(
                        msg -> {
                            try {
                                msg.editMessage(
                                        new EmbedBuilder()
                                                .setDescription(
                                                        String.format(
                                                                "%s You got a kiss from %s owo",
                                                                name, message.getMember().getEffectiveName()))
                                                .setColor(
                                                        Colors.getEffectiveMemberColor(msg.getGuild().getMember(user)))
                                                .setImage(Nekos.getKiss())
                                                .build())
                                        .queue();
                            } catch (Exception e) {
                                NekoBot.log.error("broken kiss? ", e);
                                if (BotChecks.canReact(msg)) {
                                    msg.addReaction("🚫").queue();
                                }
                            }
                        });
        }
    }
}
