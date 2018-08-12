package life.nekos.bot.commands.neko;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

@CommandDescription(
        name = "lewd",
        triggers = {"lewd", "owo", "wew", "nyaaaa"},
		attributes = {@CommandAttribute(key = "neko")},
        description = "random nekos owO"
)
public class LewdCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {
        Models.statsUp("lewd");
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
                                    .setDescription("Nekos owo")
                                    .setColor(Color.magenta)
                                    .setImage(Nekos.getLewd())
                                    .build())
                    .queue();
        } catch (Exception e) {
            trigger.getChannel().sendMessage(Formats.error("oh? something broken nya!")).queue();
            NekoBot.log.error("lewd command broken? ", e);
        }
    }
}
