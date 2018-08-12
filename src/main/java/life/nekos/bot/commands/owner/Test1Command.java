package life.nekos.bot.commands.owner;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Misc;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@CommandDescription(
        name = "testt",
        triggers = {"testt", "o"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly", value = "true"),
                @CommandAttribute(key = "dm", value = "yes"),
        },
        description = "test"
)
public class Test1Command implements Command {
    @Override
    public void execute(Message context, String args) {
        System.out.println("wot");
        //        JFrame frame = new JFrame();
        //        frame.setSize(300, 300);
        //        JLabel label = new JLabel(new ImageIcon(image));
        //        frame.add(label);
        //        frame.setVisible(true);
        TextChannel channel = context.getTextChannel();
        channel.sendTyping().queue();
        User user;
        if (context.getMentionedUsers().isEmpty()) {
            user = context.getAuthor();
        } else {
            user = context.getMentionedUsers().get(0);
        }
        Map uinfo = Models.getUser(user.getId());
        BufferedImage target = Misc.getAvatar(user);
        try {
            // BufferedImage template = ImageIO.read(new File("res/profile.png"));
            BufferedImage ow = ImageIO.read(new File("res/ow.png"));
      /*BufferedImage bg = new BufferedImage(template.getWidth(), template.getHeight(), template.getType());
      Graphics2D image = bg.createGraphics();
      image.drawImage(target, 35, 25, 112, 112, null);
      image.drawImage(template, 0, 0, null);
      Font font = new Font("Whitney", Font.BOLD, 45);
      Font font2 = new Font("Whitney", Font.PLAIN, 30);
      image.setColor(Color.white);
      image.setFont(font);
      image.drawString(user.getName(), 175, 80);
      image.setColor(Color.black);
      image.setFont(font2);
      image.drawString("Level: \n" + String.format("%s", uinfo.get("level")), 44, 210);
      image.drawString("Total Experience: \n" + String.format("%s", uinfo.get("exp")), 44, 310);
      image.drawString("Total Nekos Caught: \n" + String.format("%s", uinfo.get("nekosall")), 44, 410);
      image.drawString("Current Nekos: \n" + String.format("%s", uinfo.get("nekos")), 44, 510);
      image.drawString("Date Registered: \n" + String.format("%s", uinfo.get("regdate")), 44, 610);
      image.dispose();*/
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.setUseCache(false);
            ImageIO.write(ow, "png", stream);
            channel.sendFile(stream.toByteArray(), "k.png", null).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
