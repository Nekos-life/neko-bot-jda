package life.nekos.bot.commands.guild;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.Misc;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static life.nekos.bot.NekoBot.waiter;

@CommandDescription(
		name = "roles",
		triggers = {"r", "roles"},
		attributes = {@CommandAttribute(key = "guild"), @CommandAttribute(key = "dm")},
		description = "Shows server role info"
)
public class RolesCommand implements Command {
	Paginator.Builder pbuilder =
			new Paginator.Builder()
					.setColumns(1)
					.setItemsPerPage(2)
					.showPageNumbers(true)
					.waitOnSinglePage(true)
					.useNumberedItems(false)
					.setEventWaiter(waiter)
					.setTimeout(1, TimeUnit.MINUTES);

	public void execute(Message event, String args) {
		Models.statsUp("roles");
		Guild guild = event.getGuild();
		StringBuilder sb = new StringBuilder();
		sb.append("Guild Roles\n").append("\n");
		pbuilder.clearItems();
		for (Role role : guild.getRoles()) {
			Color c = role.getColor();
			String color = (c != null) ? String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()) : "00000";
			String roleStr = MessageFormat.format(
							"**Role**: {0}\n**Members**: {1}\n**Color**: {2}\n**Hoisted**: {4}\n**Managed**: {5}\n**Mentionable**: {6}\n**Permissions**: \n{3}\n",
							role.getAsMention(),
							guild.getMembers().parallelStream().filter(member -> member.getRoles().parallelStream().filter(role1 -> role1 == role).toArray().length > 0).toArray().length,
							"#" + color, role.getPermissions(), role.isHoisted(), role.isManaged(), role.isMentionable()

			);
			sb.append(roleStr).append("\n\n");
			pbuilder.addItems(roleStr);
		}
		MessageEmbed msg =
				new EmbedBuilder()
						.setAuthor(
								event.getJDA().getSelfUser().getName(),
								event.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
								event.getJDA().getSelfUser().getEffectiveAvatarUrl())
						.addField(
								Formats.info("Guild roles"),
								MessageFormat.format("{0}.", "Role list"),
								false)
						.build();
		Paginator p =
				pbuilder
						.setColor(Colors.getEffectiveColor(event))
						.setUsers(event.getAuthor())
						.setText(Misc.wump(sb.toString()))
						.setFinalAction(
								m -> {
									try {
										m.clearReactions().queue();
										m.delete().queue();
									} catch (PermissionException ex) {
										m.delete().queue();
									}
								})
						.build();
		p.paginate(event.getChannel(), 1);
	}
}


