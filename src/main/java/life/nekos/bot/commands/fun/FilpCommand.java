package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.entities.Message;

import java.util.Random;

@CommandDescription(
        name = "FilpCommand",
        triggers = {"test"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly", value = "no"),
                @CommandAttribute(key = "dm", value = "no"),
        },
        description = "FilpCommand"
)
public class FilpCommand implements Command {

    Random randomNum = new Random();
    Coin coinFlip;
    private int result;
    private int heads = 0;
    private int tails = 1;

    private static Number parse(String str) {
        Number number;
        try {
            number = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            try {
                number = Long.parseLong(str);
            } catch (NumberFormatException ee) {
                throw e;
            }
        }
        return number;
    }

    @Override
    public void execute(Message message, String args) {
        String[] arg = args.trim().split(" ");
        if (args.length() == 0) {
            return;
        }
        Number raw = parse(arg[0]);
        if (raw instanceof Integer) {
            int bet = raw.intValue();
        }
        Long bal = Models.getBal(message.getAuthor().getId());
        if (bal < 1) {
            return;
        }

        result = randomNum.nextInt(2);
        if (result == 0) {
            coinFlip = Coin.Heads;
            System.out.println("You flipped Heads!");
        } else {
            coinFlip = Coin.Tails;
            System.out.println("You flipped Tails!");
        }
    }

    private enum Coin {
        Heads,
        Tails
    }
}
