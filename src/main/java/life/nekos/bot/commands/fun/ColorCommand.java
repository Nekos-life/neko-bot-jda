package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.AlexApis;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.json.JSONObject;

import java.awt.*;
import java.text.MessageFormat;

@CommandDescription(
        name = "ColorCommand",
        triggers = {"color", "colour"},
        attributes = {@CommandAttribute(key = "fun")},
        description = "ColorCommand"
)
public class ColorCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        if (!message.getMentionedMembers().isEmpty()) {
            System.out.println("k");
            Color c = Colors.getEffectiveMemberColor(message.getMentionedMembers().get(0));
            String hex = String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            try {
                JSONObject res = AlexApis.getColor(hex);
                message
                        .getTextChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setDescription(
                                                Formats.info(
                                                        MessageFormat.format(
                                                                "Color info for:\nUser: {3}\nName: {0}\n"
                                                                        + "Hex: {1}\n"
                                                                        + "RGB: {2}",
                                                                res.getString("name"),
                                                                res.getString("hex"),
                                                                res.getString("rgb"),
                                                                message.getMentionedMembers().get(0).getEffectiveName())))
                                        .setColor(c)
                                        .setImage(res.getString("image"))
                                        .setFooter("Api provided by AlexFlipnote", null)
                                        .build())
                        .queue();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            if (args.toLowerCase().contains("random")) {
                String hex = Colors.randomCodeGenerator(1).get(0);
                Color c = Color.decode("#" + hex);
                try {
                    JSONObject res = AlexApis.getColor(hex);
                    message
                            .getTextChannel()
                            .sendMessage(
                                    new EmbedBuilder()
                                            .setDescription(
                                                    Formats.info(
                                                            MessageFormat.format(
                                                                    "Color info:\nName: {0}\n" + "Hex: {1}\n" + "RGB: {2}",
                                                                    res.getString("name"),
                                                                    res.getString("hex"),
                                                                    res.getString("rgb"))))
                                            .setColor(c)
                                            .setImage(res.getString("image"))
                                            .setFooter("Api provided by AlexFlipnote", null)
                                            .build())
                            .queue();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            int color = Colors.parseColor(args);
            Color c = new Color(color);
            String hex = String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            try {
                JSONObject res = AlexApis.getColor(hex);
                message
                        .getTextChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setDescription(
                                                Formats.info(
                                                        MessageFormat.format(
                                                                "Color info:\nName: {0}\n" + "Hex: {1}\n" + "RGB: {2}",
                                                                res.getString("name"), res.getString("hex"), res.getString("rgb"))))
                                        .setColor(c)
                                        .setImage(res.getString("image"))
                                        .setFooter("Api provided by AlexFlipnote", null)
                                        .build())
                        .queue();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            message
                    .getTextChannel()
                    .sendMessage(
                            Formats.error(
                                    "Nu nya, that doesnt look like a color to me. try a hex `#0000ff` or name `Blue`"))
                    .queue();
        }
    }
}
