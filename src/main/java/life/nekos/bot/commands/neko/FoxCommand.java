package life.nekos.bot.commands.neko;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

@CommandDescription(
        name = "fox",
        triggers = {"fox", "fox_girl", "fg"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "neko")},
        description = "random fox girl"
)
@SuppressWarnings("unchecked")
public class FoxCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("fox");
        try {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setDescription("Fox girls \\o/")
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setImage(Nekos.getFox())
                                    .build())
                    .queue();
            message
                    .addReaction(message.getJDA().asBot().getShardManager().getEmoteById(Formats.getEmoteID(Formats.LEWD_EMOTE)))
                    .queue();
        } catch (Exception e) {
            message.getChannel().sendMessage(Formats.error("oh nu, something went wrong :(")).queue();
            NekoBot.log.error("oh some neko error :/ ", e);
        }
    }
}
