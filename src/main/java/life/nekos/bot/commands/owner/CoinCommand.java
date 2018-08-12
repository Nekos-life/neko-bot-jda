package life.nekos.bot.commands.owner;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.SendPoke;
import net.dv8tion.jda.core.entities.Message;


@CommandDescription(
        name = "coin",
        triggers = {"coin"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly"),
        },
        description = "force send a neko"
)
public class CoinCommand implements Command {

    @Override
    public void execute(Message trigger, String args) {
        SendPoke.send(trigger, false);
        trigger.delete().queue();
    }
}
