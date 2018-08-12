package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import static life.nekos.bot.commons.Misc.UA;

@CommandDescription(
        name = "ship",
        triggers = {"ship", "love"},
        attributes = {@CommandAttribute(key = "fun")},
        description = "Ships user/users"
)
public class ShipCommand implements Command {

    private static BufferedImage getAvatar(User user) {
        BufferedImage ava = null;
        try {
            URL userAva = new URL(user.getEffectiveAvatarUrl() + "?size=160");
            URLConnection connection = userAva.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();
            ava = ImageIO.read(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ava;
    }

    private String mixString(String a, String b) {
        int max = Math.max(a.length(), b.length());
        StringBuilder mixxed = new StringBuilder();
        for (int i = 0; i < max; i++) {
            if (i <= a.length() - 1) mixxed.append(a, i, i + 1);
            if (i <= b.length() - 1) mixxed.append(b, i, i + 1);
        }
        return mixxed.toString();
    }

    @Override
    public void execute(Message context, String args) {
        TextChannel channel = context.getTextChannel();
        channel.sendTyping().queue();
        User user;
        User user1;
        if (context.getMentionedUsers().isEmpty()) {
            channel.sendMessage(Formats.error("Nu nya, you must mention a user~")).queue();
        } else {
            if (context.getMentionedUsers().size() > 1) {
                user1 = context.getMentionedUsers().get(1);
            } else {
                user1 = context.getAuthor();
            }
            user = context.getMentionedUsers().get(0);
            if (user == context.getJDA().getSelfUser()) {
                channel.sendMessage(Formats.error("Nu nya, Your tails not big enough for me~ >.<")).queue();
                return;
            }
            if (user == context.getAuthor()) {
                channel.sendMessage(Formats.error("Nu nya, You cant ship yourself~")).queue();
                return;
            }
            BufferedImage target1 = getAvatar(user);
            BufferedImage target = getAvatar(user1);
            try {
                Random r = new Random();
                int Low = 10;
                int High = 99;
                int Result = r.nextInt(High - Low) + Low;
                long Result1 = user.getIdLong() / 100 / user1.getIdLong() / 100 * 100;
                BufferedImage template = ImageIO.read(new File("res/catLove.png"));
                BufferedImage bg =
                        new BufferedImage(template.getWidth(), template.getHeight(), template.getType());
                Graphics2D image = bg.createGraphics();
                Font font = new Font("Whitney", Font.BOLD, 36);
                image.setColor(new Color(51, 232, 211));
                image.setFont(font);
                image.drawImage(target, 0, 0, 160, 160, null);
                image.drawImage(target1, 320, 0, 160, 160, null);
                image.drawImage(template, 0, 0, null);
                image.drawString(String.format("%s", Result), 222, 157);
                image.dispose();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.setUseCache(false);
                ImageIO.write(bg, "png", stream);
                Message msg =
                        new MessageBuilder()
                                .append(mixString(user.getName(), user1.getName()))
                                .append(" ")
                                .append(Formats.NEKO_C_EMOTE)
                                .build();
                channel.sendFile(stream.toByteArray(), "shipped.png", msg).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
