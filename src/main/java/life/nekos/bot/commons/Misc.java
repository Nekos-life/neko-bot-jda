package life.nekos.bot.commons;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.apis.Nekos;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildManager;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static life.nekos.bot.handlers.EventHandler.getHOME;
import static life.nekos.bot.handlers.EventHandler.getJDA;

/**
 * Created by Tom on 10/4/2017.
 */
public class Misc {

    public static final String[] UA = {
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"
    };
    private static final HashMap<String, String> emap;

    static {
        emap = new HashMap<>();
        emap.put("online", Formats.ON_EMOTE);
        emap.put("idle", Formats.IDLE_EMOTE);
        emap.put("dnd", Formats.DND_EMOTE);
        emap.put("invisible", Formats.OFF_EMOTE);
        emap.put("offline", Formats.OFF_EMOTE);
    }

    public static String getStatusEmote(Member member) {
        return emap.get(member.getOnlineStatus().getKey());
    }

    public static Runnable autoAvatar() {
        return () -> {
            try {
                URL url = new URL(Nekos.getAvatar());
                URLConnection connection = url.openConnection();
                connection.connect();
                Icon icon = Icon.from(connection.getInputStream());
                GuildManager gm = new GuildManager(getHOME());
                gm.setIcon(icon).queue();
                getJDA().getSelfUser().getManager().setAvatar(icon).queue();
                NekoBot.log.info(
                        "Setting New Guild icon/Avatar\n\rGot new icon url \n\r"
                                + url.toString()
                                + "\n\rAvatar set to \n\r"
                                + getJDA().getSelfUser().getEffectiveAvatarUrl()
                                + "\n\rGuild icon set to \n\r"
                                + getHOME().getIconUrl());
            } catch (Exception e) {
                NekoBot.log.error("oh? ", e);
            }
        };
    }

    public static String now() {
        DateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy, h:mm:ss a");
        return dateFormat.format(new Date());
    }

    public static String wump(String data) {
        StringBuilder url = new StringBuilder();
        url.append("https://feed-the-wump.us/");
        try {
            url.append(
                    Unirest.post("https://feed-the-wump.us/documents")
                            .body(data)
                            .asJson()
                            .getBody()
                            .getObject()
                            .getString("key"));
            System.out.println(url);
        } catch (UnirestException e) {
            url.append("broke");
        }
        return url.toString();
    }

    public static BufferedImage getAvatar(User user) {
        BufferedImage ava = null;
        try {
            URL userAva = new URL(user.getEffectiveAvatarUrl());
            URLConnection connection = userAva.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();
            ava = ImageIO.read(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ava;
    }

    public static WebhookClient webhookClient(String url) {
        return new WebhookClientBuilder(url).build();
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
