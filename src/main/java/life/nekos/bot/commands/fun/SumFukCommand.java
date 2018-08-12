package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static life.nekos.bot.commons.checks.UserChecks.isDonor;

@CommandDescription(
        name = "sumfuk",
        triggers = {"sumfuk", "lf", "sf"},
        attributes = {
                @CommandAttribute(key = "fun"),
        },
        description = "You want sum fuk?"
)

public class SumFukCommand implements Command {
    @Override
    public void execute(Message context, String args) {
        Message message = context;
        if (!isDonor(message.getAuthor())) {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setAuthor(
                                            message.getJDA().getSelfUser().getName(),
                                            message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                            message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setDescription(
                                            Formats.error(
                                                    " Sorry this command is only available to our Patrons.\n"
                                                            + message
                                                            .getJDA()
                                                            .asBot()
                                                            .getShardManager()
                                                            .getEmoteById(475801484282429450L).getAsMention()

                                                            + "[Join Today](https://www.patreon.com/bePatron?c=1830314&rid=2826101)"))
                                    .build())
                    .queue();
            return;
        }
        TextChannel channel = context.getTextChannel();
        channel.sendTyping().queue();
        User user = null;
        if (context.getMentionedUsers().isEmpty()) {
            channel.sendMessage(Formats.error("Nu nya, you must mention a user~")).queue();
        } else {
            user = context.getMentionedUsers().get(0);
            if (user == context.getJDA().getSelfUser()) {
                channel.sendMessage(Formats.error("Nu nya, Your tails not big enough for me~ >.<")).queue();
                return;
            }
            if (user == context.getAuthor()) {
                channel
                        .sendMessage(Formats.error("Nu nya, I dont think you need to ask to fuk yourself~"))
                        .queue();
                return;
            }

            try {
                BufferedImage template = ImageIO.read(new File("res/sf.jpg"));
                BufferedImage bg =
                        new BufferedImage(template.getWidth(), template.getHeight(), template.getType());
                Graphics2D image = bg.createGraphics();
                image.drawImage(template, 0, 0, null);
                Font font = new Font("Whitney", Font.BOLD, 30);
                image.setColor(Color.black);
                image.setFont(font);
                image.drawString(String.format("%s", user.getName()), 228, 116);
                image.drawString(String.format("%s", context.getAuthor().getName()), 248, 165);
                image.dispose();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.setUseCache(false);
                ImageIO.write(bg, "png", stream);
                channel.sendFile(stream.toByteArray(), "sumfuk?.png", null).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @CommandDescription(
            name = "gecg",
            triggers = {"gecg", "meme"},
            attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "fun")},
            description = "random gecg owO"
    )
    public static class MemeCommand implements Command {
        @Override
        public void execute(Message trigger, String args) {
            Models.statsUp("gecg");
            if (!trigger.getTextChannel().isNSFW()) {
                trigger
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setColor(Color.magenta)
                                        .setDescription(
                                                "Lewd nekos are shy nya, They can only be found in Discord nsfw channels")
                                        .build())
                        .queue();
                return;
            }
            try {
                trigger
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setDescription("owo")
                                        .setColor(Colors.getEffectiveColor(trigger))
                                        .setImage(Nekos.getMeme())
                                        .build())
                        .queue();
            } catch (Exception e) {
                trigger.getChannel().sendMessage(Formats.error("oh? something broken nya!")).queue();
                NekoBot.log.error("lewd command broken? ", e);
            }
        }
    }
}
