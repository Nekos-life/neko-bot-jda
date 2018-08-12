package life.nekos.bot.commands.user;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;
import java.util.Map;

import static life.nekos.bot.commons.Misc.now;
import static life.nekos.bot.commons.checks.UserChecks.isDonor;
import static life.nekos.bot.commons.checks.UserChecks.isDonor_plus;

@CommandDescription(
        name = "profile",
        triggers = {"profile", "rank", "exp"},
        attributes = {@CommandAttribute(key = "user")},
        description = "Shows your profile or a users profile @user o.-"
)
public class ProfileCommand implements Command {
  @Override
  public void execute(Message message, String args) {
    Models.statsUp("profile");
    message
            .addReaction(
                    message
                            .getJDA()
                            .asBot()
                            .getShardManager()
                            .getEmoteById(Formats.getEmoteID(Formats.USER_EMOTE)))
            .queue();
    if (message.getMentionedMembers().isEmpty()) {
      Map uinfo = Models.getUser(message.getAuthor().getId());
      EmbedBuilder em = new EmbedBuilder()
              .addField(
                      Formats.LEVEL_EMOTE + " Level",
                      Formats.bold(String.format("%s", uinfo.get("level"))),
                      false)
              .addField(
                      Formats.MAGIC_EMOTE + " Total Experience",
                      Formats.bold(String.format("%s", uinfo.get("exp"))),
                      false)
              .addField(
                      Formats.NEKO_V_EMOTE + " Total Nekos Caught",
                      Formats.bold(String.format("%s", uinfo.get("nekosall"))),
                      false)
              .addField(
                      Formats.NEKO_C_EMOTE + " Current Nekos",
                      Formats.bold(String.format("%s", uinfo.get("nekos"))),
                      false)
              .addField(
                      Formats.DATE_EMOTE + " Date Registered",
                      Formats.bold(String.format("%s", uinfo.get("regdate"))),
                      false)
              .setThumbnail(message.getAuthor().getEffectiveAvatarUrl())
              .setFooter(
                      MessageFormat.format(
                              "Profile for {0} | Today at {1}", message.getAuthor().getName(), now()),
                      "https://media.discordapp.net/attachments/333742928218554368/374966699524620289/profile.png")
              .setColor(Colors.getEffectiveColor(message))
              .setAuthor(
                      String.format("Profile For %s", message.getAuthor().getName()),
                      message.getAuthor().getEffectiveAvatarUrl(),
                      message.getAuthor().getEffectiveAvatarUrl());

      if (isDonor(message.getAuthor()) && !isDonor_plus(message.getAuthor())) {
        em.addField(
                Formats.PATRON_EMOTE + "Donor",
                Formats.bold("Commands unlocked"),
                false);
      }
      if (isDonor(message.getAuthor()) && isDonor_plus(message.getAuthor())) {
        em.addField(
                Formats.PATRON_EMOTE + "Donor+",
                Formats.bold("Commands & 2x exp,nekos unlocked"),
                false);
      }
      message
              .getChannel()
              .sendMessage(
                      em.build())
              .queue();
      return;
    }
    for (Member member : message.getMentionedMembers()) {
      if (member.getUser().isBot()) {
        message
                .getChannel()
                .sendMessage(new EmbedBuilder().setDescription("Bots dont have profiles ;p").build())
                .queue();
        return;
      }
      Map uinfo = Models.getUser(member.getUser().getId());
      EmbedBuilder em =
              new EmbedBuilder()
                      .addField(
                              Formats.LEVEL_EMOTE + " Level",
                              Formats.bold(String.format("%s", uinfo.get("level"))),
                              false)
                      .addField(
                              Formats.MAGIC_EMOTE + " Total Experience",
                              Formats.bold(String.format("%s", uinfo.get("exp"))),
                              false)
                      .addField(
                              Formats.NEKO_V_EMOTE + " Total Nekos Caught",
                              Formats.bold(String.format("%s", uinfo.get("nekosall"))),
                              false)
                      .addField(
                              Formats.NEKO_C_EMOTE + " Current Nekos",
                              Formats.bold(String.format("%s", uinfo.get("nekos"))),
                              false)
                      .addField(
                              Formats.DATE_EMOTE + " Date Registered",
                              Formats.bold(String.format("%s", uinfo.get("regdate"))),
                              false)
                      .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                      .setFooter(
                              MessageFormat.format(
                                      "Profile for {0} | Today at {1}", member.getEffectiveName(), now()),
                              message
                                      .getJDA()
                                      .asBot()
                                      .getShardManager()
                                      .getEmoteById(Formats.getEmoteID(Formats.USER_EMOTE))
                                      .getImageUrl())
                      .setColor(Colors.getEffectiveMemberColor(member))
                      .setAuthor(
                              String.format("Profile For %s", member.getEffectiveName()),
                              member.getUser().getEffectiveAvatarUrl(),
                              member.getUser().getEffectiveAvatarUrl());
      if (isDonor(member.getUser()) && !isDonor_plus(member.getUser())) {
        em.addField(
                Formats.PATRON_EMOTE + "Donor",
                Formats.bold("Commands unlocked"),
                false);
      }
      if (isDonor(member.getUser()) && isDonor_plus(member.getUser())) {
        em.addField(
                Formats.PATRON_EMOTE + "Donor+",
                Formats.bold("Commands & 2x exp,nekos unlocked"),
                false);
      }

      message.getChannel().sendMessage(em.build()).queue();
    }
  }
}
