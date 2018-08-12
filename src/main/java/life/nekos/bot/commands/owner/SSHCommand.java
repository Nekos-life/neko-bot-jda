package life.nekos.bot.commands.owner;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Misc;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

@CommandDescription(
        name = "ssh",
        triggers = {"ssh", "exec"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly"),
                @CommandAttribute(key = "dm"),
        },
        description = "run a shell command"
)
public class SSHCommand implements Command {

    @Override
    public void execute(Message trigger, String args) {
        String s = null;
        if (args.length() == 0) {
            trigger
                    .getChannel()
                    .sendMessage(":x: **LoL you need a command <:blobwaitwhat:357234053310578690> **")
                    .queue();
            return;
        }
        try {

            Process p = Runtime.getRuntime().exec(args);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder wew = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                wew.append(s);
            }
            String k = MessageFormat.format("```{0}```", wew.toString());
            if (k.length() > 5 && k.length() < 1000) {

                String wump = Misc.wump(k);
                trigger
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setAuthor(
                                                trigger.getJDA().getSelfUser().getName(),
                                                trigger.getJDA().asBot().getInviteUrl(),
                                                trigger.getJDA().getSelfUser().getAvatarUrl())
                                        .setColor(Colors.getEffectiveColor(trigger))
                                        .addField("exec", k, false)
                                        .addField("wump", wump, false)
                                        .build())
                        .queue();
            }

            if (k.length() > 1000) {

                String wump = Misc.wump(k);
                trigger
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setAuthor(
                                                trigger.getJDA().getSelfUser().getName(),
                                                trigger.getJDA().asBot().getInviteUrl(),
                                                trigger.getJDA().getSelfUser().getAvatarUrl())
                                        .setColor(Colors.getEffectiveColor(trigger))
                                        .addField("wump", wump, false)
                                        .build())
                        .queue();
            }

            StringBuilder err = new StringBuilder();
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
                err.append(s);
            }

            String e = MessageFormat.format("```{0}```", err.toString());
            if (e.length() > 5) {
                String wump = Misc.wump(k);
                trigger
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setAuthor(
                                                trigger.getJDA().getSelfUser().getName(),
                                                trigger.getJDA().asBot().getInviteUrl(),
                                                trigger.getJDA().getSelfUser().getAvatarUrl())
                                        .setColor(Colors.getEffectiveColor(trigger))
                                        .addField("err", wump, false)
                                        .build())
                        .queue();
            }
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            String wump = Misc.wump(e.getLocalizedMessage());
            trigger
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setAuthor(
                                            trigger.getJDA().getSelfUser().getName(),
                                            trigger.getJDA().asBot().getInviteUrl(),
                                            trigger.getJDA().getSelfUser().getAvatarUrl())
                                    .setColor(Colors.getEffectiveColor(trigger))
                                    .addField(
                                            "exception happened <:blobwaitwhat:357234053310578690>",
                                            "here's what I know:\n" + wump,
                                            false)
                                    .build())
                    .queue();
        }
    }
}
