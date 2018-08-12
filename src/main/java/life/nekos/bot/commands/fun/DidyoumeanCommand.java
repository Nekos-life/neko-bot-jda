package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.AlexApis;
import net.dv8tion.jda.core.entities.Message;

import java.util.regex.Pattern;

@CommandDescription(
		name = "Did you mean?",
		triggers = {"dym", "did_you_mean"},
		attributes = {
				@CommandAttribute(key = "dm", value = "yes"),
				@CommandAttribute(key = "fun")
		},
		description = "Did you mean?  dym something | something else"
)
public class DidyoumeanCommand implements Command {
	@Override
	public void execute(Message message, String args) {
		if (args.isEmpty()) {
			message.getTextChannel().sendMessage(Formats.error("Missing args")).queue();
			return;
		}
		String[] arg = args.split(Pattern.quote("|"));
		try {

			message
					.getTextChannel()
					.sendFile(AlexApis.getGoolge(arg[0], arg[1]), "Did you Mean.png")
					.queue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}