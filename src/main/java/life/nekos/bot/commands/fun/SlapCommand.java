/// **
// * Created by Tom on 9/18/2017.
// */
// package life.nekos.bot.commands;
//
// import com.jagrosh.jdautilities.commandclient.Command;
// import com.jagrosh.jdautilities.commandclient.CommandEvent;
// import com.mashape.unirest.http.HttpResponse;
// import com.mashape.unirest.http.JsonNode;
// import com.mashape.unirest.http.Unirest;
// import com.mashape.unirest.http.async.Callback;
// import com.mashape.unirest.http.exceptions.UnirestException;
// import life.nekos.bot.neko;
// import net.dv8tion.jda.core.EmbedBuilder;
// import net.dv8tion.jda.core.Permission;
// import net.dv8tion.jda.core.entities.Guild;
// import net.dv8tion.jda.core.entities.Member;
// import net.dv8tion.jda.core.entities.Message;
// import net.dv8tion.jda.core.entities.User;
//
// import java.awt.*;
// import java.util.List;
// import java.util.Map;
//
// public class PatCommand extends Command {
//
//    public PatCommand() {
//        this.name = "Pat";
//        this.help = "Pat someone \\o/";
//        this.arguments = "@USER_EMOTE";
//        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
//        this.guildOnly = true;
//    }
//
//    @Override
//    protected void execute(CommandEvent event) {
//        if (event.getMessage().getMentionedUsers().isEmpty()) {
//            event.reply(new EmbedBuilder().setColor(Color.magenta).setDescription("You must
// mention a USER_EMOTE").build());
//            return;
//        }
//        Map stats =
// neko.r.table("stats").get("f43c8828-fbdd-4fd4-87b7-d7719c537620").run(neko.conn);
//        long i = (long) stats.get("pat");
//        stats.put("pat", i + 1);
//        neko.r.table("stats").insert(stats).optArg("conflict", "update").run(neko.conn);
//        Guild guild = event.getGuild();
//        Message message = event.getMessage();
//        List<User> mentionedUsers = message.getMentionedUsers();
//        for (User USER_EMOTE : mentionedUsers) {
//            Member member = guild.getMember(USER_EMOTE);  //We get the member object for each
// mentioned USER_EMOTE to kick them!
//            String name = member.getEffectiveName();
//            User author = event.getAuthor();
//            String authorname = guild.getMember(author).getEffectiveName();
//            Unirest.get("https://nekos.life/api/pat").header("Key",
// "dnZ4fFJbjtch56pNbfrZeSRfgWqdPDgf").asJsonAsync(new Callback<JsonNode>() {
//                @Override
//                public void completed(HttpResponse<JsonNode> hr) {
//                    hr.getBody().getArray().getJSONObject(0)
//                    event.reply(new EmbedBuilder().setDescription(String.format("%s You got a pat
// from %s \\o/", name, authorname))
//                            .setColor(Color.magenta)
//                            .setImage(hr.getBody().getObject().getString("url"))
//                            .build());
//                }
//
//                @Override
//                public void failed(UnirestException ue) {
//                    event.reactError();
//                }
//
//                @Override
//                public void cancelled() {
//                    event.reactError();
//                }
//            });
//        }
//    }
// }
/**
 * Created by Tom on 9/18/2017.
 */
package life.nekos.bot.commands.fun;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.apis.Nekos;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

@CommandDescription(
        name = "slap",
        triggers = "slap",
        attributes = {@CommandAttribute(key = "fun")},
        description = "slap someone ^^"
)
@SuppressWarnings("unchecked")
public class SlapCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        Models.statsUp("slap");
        if (message.getMentionedUsers().isEmpty()) {
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setColor(Colors.getEffectiveColor(message))
                                    .setDescription("Who do you want to slap? Try mentioning a user, nya~")
                                    .build())
                    .queue();
            return;
        }

        List<User> mentionedUsers = message.getMentionedUsers();
        message.addReaction("\uD83E\uDD17").queue();
        for (User user : mentionedUsers) {
            if (user == message.getJDA().getSelfUser()) {
                message.getChannel().sendMessage("Nyaaaaaaaaaa, nu dun touch mee~").queue();
                break;
            }
            if (user == message.getAuthor()) {
                message
                        .getChannel()
                        .sendMessage("oh? why you want to cuddle yourself? Find a friend nya~")
                        .queue();
                break;
            }
            String name = message.getGuild().getMember(user).getEffectiveName();
            message
                    .getChannel()
                    .sendMessage("😦")
                    .queue(
                            msg -> {
                                try {
                                    msg.editMessage(
                                            new EmbedBuilder()
                                                    .setDescription(
                                                            String.format(
                                                                    "%s You got slapped by %s %s",
                                                                    name, message.getMember().getEffectiveName(), Formats.getCat()))
                                                    .setColor(
                                                            Colors.getEffectiveMemberColor(msg.getGuild().getMember(user)))
                                                    .setImage(Nekos.getCuddle())
                                                    .build())
                                            .queue();
                                } catch (Exception e) {
                                    NekoBot.log.error("broken slap ", e);
                                    if (BotChecks.canReact(msg)) {
                                        msg.addReaction("🚫").queue();
                                    }
                                }
                            });
        }
    }
}
