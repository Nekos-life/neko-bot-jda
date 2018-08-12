package life.nekos.bot.commands.owner;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Message;

import java.net.URL;
import java.net.URLConnection;

import static life.nekos.bot.commons.Misc.UA;

@CommandDescription(
        name = "set avatar",
        triggers = {"setavatar"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly"),
        },
        description = "Sets the bot avatar, --random for a random new one"
)
public class SetAvatarCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        if (args.toLowerCase().contains("--random")) {
            try {
                URL url = new URL(Nekos.getAvatar());
                URLConnection connection = url.openConnection();
                // connection.setRequestProperty(UA[0], UA[1]);
                connection.connect();
                Icon icon = Icon.from(connection.getInputStream());
                message.getJDA().getSelfUser().getManager().setAvatar(icon).queue();
                Message msg =
                        new MessageBuilder()
                                .append("done nya! set avatar\\o/ ")
                                .append(Formats.NEKO_C_EMOTE)
                                .build();
                URL newUrl = new URL(message.getJDA().getSelfUser().getEffectiveAvatarUrl());
                URLConnection conn = newUrl.openConnection();
                conn.setRequestProperty(UA[0], UA[1]);
                conn.connect();
                message
                        .getTextChannel()
                        .sendFile(conn.getInputStream(), "NewAvatar.png", msg)
                        .queue(
                                message1 ->
                                        message
                                                .addReaction(
                                                        message.getJDA().getEmoteById(Formats.getEmoteID(Formats.LEWD_EMOTE)))
                                                .queue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            URL url = new URL(args);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();
            Icon icon = Icon.from(connection.getInputStream());
            message.getJDA().getSelfUser().getManager().setAvatar(icon).queue();
            Message msg =
                    new MessageBuilder()
                            .append("done nya! set avatar\\o/ ")
                            .append(Formats.NEKO_C_EMOTE)
                            .build();
            URL newUrl = new URL(message.getJDA().getSelfUser().getEffectiveAvatarUrl());
            URLConnection conn = newUrl.openConnection();
            conn.setRequestProperty(UA[0], UA[1]);
            conn.connect();
            message
                    .getTextChannel()
                    .sendFile(conn.getInputStream(), "NewAvatar.png", msg)
                    .queue(
                            message1 ->
                                    message
                                            .addReaction(
                                                    message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.NEKO_C_EMOTE)))
                                            .queue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
