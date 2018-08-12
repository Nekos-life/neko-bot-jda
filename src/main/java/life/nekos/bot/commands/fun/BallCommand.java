package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.json.JSONObject;

import static life.nekos.bot.commons.apis.Nekos.getBall;

@CommandDescription(
        name = "8ball",
        triggers = {"8", "8ball", "8b", "ball"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "fun")},
        description = "random why?"
)
public class BallCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {

	    if (!args.endsWith("?")) {
            trigger
                    .getTextChannel()
                    .sendMessage(
                            Formats.error(
                                    "Nuu, nya! That doesn't look like a question? didn't anyone teach you punctuation??"))
                    .queue();
            return;
        }
        try {
            String ball = getBall();
            JSONObject jsonObj = new JSONObject(ball);
            System.out.println(jsonObj.getString("url"));
            trigger
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.getEffectiveColor(trigger))
                                    .setAuthor(
                                            "Magic \uD83C\uDFB1",
                                            trigger.getJDA().asBot().getInviteUrl(),
                                            trigger.getAuthor().getEffectiveAvatarUrl())
                                    .setDescription("❓: " + args + "\nℹ: " + jsonObj.getString("response"))
                                    .setImage(jsonObj.getString("url"))
                                    .build())
                    .queue();
        } catch (Exception e) {
            NekoBot.log.error("why broken? ", e);
        }
    }
}
