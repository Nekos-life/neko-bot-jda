package life.nekos.bot.commands.guild;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.Misc;
import life.nekos.bot.commons.checks.UserChecks;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.Map;

@CommandDescription(
        name = "config",
        triggers = {"set", "config", "settings", "setup"},
        attributes = {@CommandAttribute(key = "guild")},
        description =
                "\nconfig prefix set: Sets the guild prefix\n"
                        + "config nekos on: Turn on catch a neko in the current channel for your guild\n"
                        + "config nekos off: Turn off catch a neko for your guild\n"
                        + "config status: Shows the guild config info"
)
@SuppressWarnings("unchecked")
public class SettingsCommand implements Command {
    @Override
    public void execute(Message event, String args) {
        TextChannel ch = event.getTextChannel();
        Map g = Models.getGuild(event.getGuild().getId());
        String gp = Models.getPrefix(event);
        String[] arg = args.trim().split(" ");
        if (args.length() == 0) {
            ch.sendMessage(
                    new EmbedBuilder()
                            .setAuthor(
                                    event.getJDA().getSelfUser().getName(),
                                    event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                    event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                            .setColor(Colors.getEffectiveColor(event))
                            .addField(
                                    Formats.error("Missing args"),
                                    MessageFormat.format(
                                            "**{0}config prefix set**: Sets the guild prefix owo\n"
                                                    + "**{0}config nekos on**: Turn on catch a neko in the current channel for your guild \\o \n"
                                                    + "**{0}config nekos off**: Turn off catch a neko for your guild o/ \n"
                                                    + "**{0}config status**: Shows the guild config info\n",
                                            gp),
                                    false)
                            .setFooter(
                                    MessageFormat.format(
                                            "Requested by {0} | {1}",
                                            event.getMember().getEffectiveName(), Misc.now()),
                                    event.getAuthor().getEffectiveAvatarUrl())
                            .build())
                    .queue();
            return;
        }
        switch (arg[0]) {
            case "prefix":
                if (arg.length > 1) {
                    if (!UserChecks.isAdmin(event)) {
                        ch.sendMessage(
                                Formats.error(
                                        "You lack the `MANAGE_SERVER` Permissions needed to set the prefix for this guild"))
                                .queue();
                    }
                    if (!arg[1].equalsIgnoreCase("set")) {
                        ch.sendMessage(Formats.error(MessageFormat.format("Use {0}config prefix set", gp)))
                                .queue();
                        break;
                    }
                    if (arg[1].equalsIgnoreCase("set") && arg.length == 2) {
                        ch.sendMessage(Formats.error("Missing args")).queue();
                        break;
                    }
                    if (arg[1].equalsIgnoreCase("set") && arg.length > 2) {
                        Models.setPrefix(arg[2], event.getGuild().getId());
                        ch.sendMessage("set prefix to " + arg[2]).queue();
                        break;
                    }
                } else {
                    ch.sendMessage(
                            new EmbedBuilder()
                                    .setAuthor(
                                            event.getJDA().getSelfUser().getName(),
                                            event.getJDA().asBot().getInviteUrl(),
                                            event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .setColor(Colors.getEffectiveColor(event))
                                    .addField(
                                            Formats.info("Info"),
                                            MessageFormat.format(
                                                    "My prefix on this guild is `{0}` {1}", gp, Formats.MAGIC_EMOTE),
                                            false)
                                    .setFooter(
                                            MessageFormat.format(
                                                    "Requested by {0} | {1}",
                                                    event.getMember().getEffectiveName(), Misc.now()),
                                            event.getAuthor().getEffectiveAvatarUrl())
                                    .build())
                            .queue();
                    break;
                }
                break;
            case "status":
                Object c = g.get("nekochannel");
                String nc;
                if (c != null) {
                    nc = event.getJDA().getTextChannelById(g.get("nekochannel").toString()).getAsMention();
                } else {
                    nc = "No neko channel found";
                }
                ch.sendMessage(
                        new EmbedBuilder()
                                .setAuthor(
                                        event.getJDA().getSelfUser().getName(),
                                        event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                        event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                .setColor(Colors.getRndColor())
                                .addField(
                                        Formats.info("Info"),
                                        MessageFormat.format(
                                                "Neko Channel:\n{0}\n" + "My prefix on this guild is `{1}`\n", nc, gp),
                                        false)
                                .setFooter(
                                        MessageFormat.format(
                                                "Requested by {0} | {1}",
                                                event.getMember().getEffectiveName(), Misc.now()),
                                        event.getAuthor().getEffectiveAvatarUrl())
                                .build())
                        .queue();
                break;
            case "nekos":
                System.out.println(args);
                if (arg.length > 1) {
                    if (arg[1].equalsIgnoreCase("on")) {
                        if (!UserChecks.isAdmin(event)) {
                            ch.sendMessage(
                                    Formats.error(
                                            "You lack the `MANAGE_SERVER` Permissions needed to set the neko channel for this guild"))
                                    .queue();
                        }
                        g.put("nekochannel", event.getTextChannel().getId());
                        Models.setGuild(g);
                        ch.sendMessage(
                                new EmbedBuilder()
                                        .setAuthor(
                                                event.getJDA().getSelfUser().getName(),
                                                event.getJDA().asBot().getInviteUrl(),
                                                event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                        .setColor(Colors.getEffectiveColor(event))
                                        .addField(
                                                Formats.info("Info"),
                                                MessageFormat.format(
                                                        "Nekos on for {0} {1}\n"
                                                                + "Nekos will now spawn in {2}\n"
                                                                + "I hear they are attracted to active channels /o\\",
                                                        event.getGuild().getName(), Formats.MAGIC_EMOTE, ch.getAsMention()),
                                                false)
                                        .setFooter(
                                                MessageFormat.format(
                                                        "Requested by {0} | {1}",
                                                        event.getMember().getEffectiveName(), Misc.now()),
                                                event.getAuthor().getEffectiveAvatarUrl())
                                        .build())
                                .queue();
                        break;
                    }
                    if (arg[1].equalsIgnoreCase("off")) {
                        if (!UserChecks.isAdmin(event)) {
                            ch.sendMessage(
                                    Formats.error(
                                            "You lack the `MANAGE_SERVER` Permissions needed to set the neko channel for this guild"))
                                    .queue();
                        }
                        g.put("nekochannel", null);
                        Models.setGuild(g);
                        ch.sendMessage(
                                new EmbedBuilder()
                                        .setAuthor(
                                                event.getJDA().getSelfUser().getName(),
                                                event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                                event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                        .setColor(Colors.getEffectiveColor(event))
                                        .addField(
                                                Formats.info("Info"),
                                                MessageFormat.format(
                                                        "Nekos Off for {0} {1}\n",
                                                        event.getGuild().getName(), Formats.MAGIC_EMOTE),
                                                false)
                                        .setFooter(
                                                MessageFormat.format(
                                                        "Requested by {0} | {1}",
                                                        event.getMember().getEffectiveName(), Misc.now()),
                                                event.getAuthor().getEffectiveAvatarUrl())
                                        .build())
                                .queue();
                        break;
                    } else {
                        ch.sendMessage(
                                Formats.error(
                                        MessageFormat.format(
                                                "Use `{0}config nekos on` or `{0}config nekos off`", gp)))
                                .queue();
                        break;
                    }

                } else {
                    ch.sendMessage(
                            Formats.error(
                                    MessageFormat.format(
                                            "Use `{0}config nekos on` or `{0}config nekos off`", gp)))
                            .queue();
                    break;
                }
            default:
                ch.sendMessage(
                        new EmbedBuilder()
                                .setAuthor(
                                        event.getJDA().getSelfUser().getName(),
                                        event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                        event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                .setColor(Colors.getEffectiveColor(event))
                                .addField(
                                        Formats.error("Missing args"),
                                        MessageFormat.format(
                                                "**{0}config prefix set**: Sets the guild prefix owo\n"
                                                        + "**{0}config nekos on**: Turn on catch a neko in the current channel for your guild \\o \n"
                                                        + "**{0}config nekos off**: Turn off catch a neko for your guild o/ \n"
                                                        + "**{0}config status**: Shows the guild config info\n",
                                                gp),
                                        false)
                                .setFooter(
                                        MessageFormat.format(
                                                "Requested by {0} | {1}",
                                                event.getMember().getEffectiveName(), Misc.now()),
                                        event.getAuthor().getEffectiveAvatarUrl())
                                .build())
                        .queue();
                break;
        }
    }
}
