package life.nekos.bot.commands.guild;

import com.github.rainestormee.jdacommand.Category;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.menu.ButtonMenu;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.concurrent.TimeUnit;

import static life.nekos.bot.commons.checks.UserChecks.isDonor;

@CommandDescription(
        name = "recolor",
        triggers = {
                "rc", "recolor",
        },
        attributes = {@CommandAttribute(key = "guild")},
        description =
                "Randomly sets the color to all hoisted roles on this server\nThis is a Patreon only command"
)
@Category("guild")
public class RecolorCommand implements Command {
    @Override
    public void execute(Message message, String args) {
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
        if (!BotChecks.canEditRoles(message)) {
            message
                    .getTextChannel()
                    .sendMessage(
                            Formats.error("Nu, nya i lack the `MANAGE_ROLES` permission needed to do this~"))
                    .queue();
            return;
        }
        if (!PermissionUtil.checkPermission(
                message.getTextChannel(), message.getMember(), Permission.MANAGE_ROLES)) {
            message
                    .getTextChannel()
                    .sendMessage(
                            Formats.error("Nu, nya you lack the `MANAGE_ROLES` permission needed to do this~"))
                    .queue();
            return;
        }
        new ButtonMenu.Builder()
                .setColor(Colors.getEffectiveMemberColor(message.getMember()))
                .setDescription(
                        Formats.info(
                                "This Command will randomly set the color to all hoisted roles on this server! Are you sure you want to continue?"))
                .setChoices(
                        message
                                .getJDA()
                                .asBot()
                                .getShardManager()
                                .getEmoteById(Formats.getEmoteID(Formats.YES_EMOTE)),
                        message
                                .getJDA()
                                .asBot()
                                .getShardManager()
                                .getEmoteById(Formats.getEmoteID(Formats.NO_EMOTE)))
                .setEventWaiter(NekoBot.waiter)
                .setTimeout(1, TimeUnit.MINUTES)
                .setFinalAction(
                        m -> {
                            m.delete().queue();
                            message.delete().queue();
                        })
                .setUsers(message.getAuthor())
                .setAction(
                        re -> {
                            if (re.getEmote()
                                    .equals(
                                            message
                                                    .getJDA()
                                                    .asBot()
                                                    .getShardManager()
                                                    .getEmoteById(Formats.getEmoteID(Formats.YES_EMOTE)))) {
                                message
                                        .getGuild()
                                        .getRoles()
                                        .parallelStream()
                                        .filter(Role::isHoisted)
                                        .forEach(
                                                role -> {
                                                    if (role.getGuild()
                                                            .getMember(message.getJDA().getSelfUser())
                                                            .canInteract(role)) {
                                                        role.getManager()
                                                                .setColor(Colors.getRndColor())
                                                                .reason(
                                                                        "Auto guild recolor command ran by "
                                                                                + Formats.getFullName(message))
                                                                .queue();
                                                    }
                                                });
                                long c =
                                        message
                                                .getGuild()
                                                .getRoles()
                                                .parallelStream()
                                                .filter(Role::isHoisted)
                                                .filter(role -> role.getGuild().getSelfMember().canInteract(role))
                                                .count();
                                message
                                        .getTextChannel()
                                        .sendMessage(Formats.U__EMOTE)
                                        .queue(
                                                m ->
                                                        m.editMessage(
                                                                "Done nya!, I have set the color of "
                                                                        + String.valueOf(c)
                                                                        + " roles \\o/")
                                                                .queueAfter(
                                                                        3,
                                                                        TimeUnit.SECONDS,
                                                                        sm -> sm.delete().queueAfter(5, TimeUnit.SECONDS)),
                                                null);
                            } else {
                                message
                                        .getTextChannel()
                                        .sendMessage("Alright i wont then")
                                        .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS), null);
                            }
                        })
                .build()
                .display(message.getChannel());
    }
}
