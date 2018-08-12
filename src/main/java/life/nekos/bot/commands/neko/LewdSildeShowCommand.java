package life.nekos.bot.commands.neko;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Slideshow;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.TimeUnit;

import static life.nekos.bot.NekoBot.waiter;
import static life.nekos.bot.commons.checks.UserChecks.isDonor;

@CommandDescription(
        name = "Lewds",
        triggers = {"lewds", "lewdslideshow", "lss", "Mewww", "o.o"},
        attributes = {@CommandAttribute(key = "neko")},
        description = "Neko slideshow \\o//\nThis is a Patreon only command"
)
public class LewdSildeShowCommand implements Command {
  private Slideshow.Builder sbuilder =
          new Slideshow.Builder().setEventWaiter(waiter).setTimeout(1, TimeUnit.MINUTES);

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
    Models.statsUp("lewds");
    if (!message.getTextChannel().isNSFW()) {
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
                                      "Lewd nekos are shy nya~, They can only be found in Discord nsfw channels")
                              .build())
              .queue();
      return;
    }
    StringBuilder urls = new StringBuilder();
    message.delete().queue();
    for (int i = 0; i < 20; ++i) {
      try {
        urls.append(Nekos.getLewd());
        urls.append(",");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    Slideshow s =
            sbuilder
                    .setColor(Colors.getEffectiveColor(message))
                    .setText(Formats.MAGIC_EMOTE + " Lewd Nekos \\o/ " + Formats.NEKO_V_EMOTE)
                    .setDescription(Formats.getCat())
                    .setUrls(urls.toString().split(","))
                    .setFinalAction(
                            msg -> {
                              msg.clearReactions().queue();
                              try {
                                msg.editMessage(
                                        new EmbedBuilder()
                                                .setImage(Nekos.getLewd())
                                                .setColor(Colors.getEffectiveColor(msg))
                                                .build())
                                        .queue();
                              } catch (Exception e) {
                                e.printStackTrace();
                              }
                            })
                    .build();
    s.display(message.getChannel());
  }
}
