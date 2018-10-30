package life.nekos.bot.commons.db;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.OptArgs;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.SendNeko;
import life.nekos.bot.commons.checks.BotChecks;
import life.nekos.bot.commons.checks.MiscChecks;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import static life.nekos.bot.commons.Constants.IS_DEBUG;
import static life.nekos.bot.commons.Formats.getFullName;
import static life.nekos.bot.commons.Misc.now;
import static life.nekos.bot.commons.checks.UserChecks.isDonor_plus;

/**
 * Created by Tom on 10/4/2017.
 */
public class Models {

    public static final RethinkDB r = RethinkDB.r;
    public static Connection conn =
            r.connection().hostname("localhost").port(28015).db("neko").connect();

    public static Cursor getTopExp() {
        return r.table("users")
                .orderBy()
                .optArg("index", r.desc("exp"))
                .limit(50)
                .run(conn, OptArgs.of("array_limit", 1000000));
    }

    public static Cursor getTopNekos() {
        return r.table("users")
                .orderBy()
                .optArg("index", r.desc("nekos"))
                .limit(50)
                .run(conn, OptArgs.of("array_limit", 1000000));
    }

    public static Map<String, Long> getTopStats() {
        Map<String, Long> map = getStats();
        map.remove("id");
        ValueComparator vc = new ValueComparator(map);
        TreeMap<String, Long> sorted = new TreeMap<>(vc);
        sorted.putAll(map);
        return sorted;
    }

    public static Map<String, Long> getStats() {
        return r.table("stats").get("f43c8828-fbdd-4fd4-87b7-d7719c537620").run(conn);
    }

    private static void setStats(Map<String, Long> stats) {
        r.table("stats").insert(stats).optArg("conflict", "update").run(conn);
    }

    public static Map<String, Object> getUser(String id) {
        return r.table("users").get(id).run(conn);
    }

    public static void setUser(Map user) {
        r.table("users").insert(user).optArg("conflict", "update").run(conn);
    }

    public static Long getBal(String id) {
        Map<String, Object> user = r.table("users").get(id).run(conn);
        return (long) user.get("nekos");
    }

    public static void setBal(String id, Long bal) {
        r.table("users").get(id).update(r.hashMap("nekos", bal)).run(conn);
    }

    public static Map<String, Object> getGuild(String id) {
        return r.table("guilds").get(id).run(conn);
    }

    public static void setGuild(Map guild) {
        r.table("guilds").insert(guild).optArg("conflict", "update").run(conn);
    }

    public static void spwNeko(MessageReceivedEvent event) {
        Map g = Models.getGuild(event.getGuild().getId());
        if (g.get("nekochannel") != null
                && g.get("nekochannel").toString().equalsIgnoreCase(event.getChannel().getId())) {
            long msgcnt = (long) g.get("msgcnt");
            long randomNum = ThreadLocalRandom.current().nextInt(40, 150 + 1);
            r.table("guilds")
                    .get(event.getGuild().getId())
                    .update(r.hashMap("msgcnt", msgcnt + 1))
                    .run(conn);
            if (msgcnt > randomNum) {
                r.table("guilds").get(event.getGuild().getId()).update(r.hashMap("msgcnt", 0)).run(conn);
                SendNeko.send(event.getMessage(), false);
                NekoBot.log.info(
                        "Neko sent on "
                                + event.getGuild().toString()
                                + " ("
                                + event.getTextChannel().toString()
                                + ")");
            }
        }
    }

    public static void statsUp(String stat) {
        Map<String, Long> stats = getStats();
        stats.computeIfAbsent(stat, k -> (long) 0);
        long i = stats.get(stat);
        stats.put(stat, i + 1);
        setStats(stats);
    }

    public static void nekoCaught(User user) {
        Map<String, Object> userData = getUser(user.getId());
        long nekos = (long) userData.get("nekos");
        long nekosall = (long) userData.get("nekosall");
        if (MiscChecks.isMagicDay(Calendar.getInstance())) {
            NekoBot.log.info("its a magic day!! owo");
            if (isDonor_plus(user)) {
                NekoBot.log.info(user.getName() + " Is donor added 2x nekos");
                userData.put("nekos", nekos + 8);
                userData.put("nekosall", nekosall + 8);
            } else {
                userData.put("nekos", nekos + 4);
                userData.put("nekosall", nekosall + 4);
            }
        } else {
            if (isDonor_plus(user)) {
                NekoBot.log.info(user.getName() + " Is donor added 2x nekos");
                userData.put("nekos", nekos + 4);
                userData.put("nekosall", nekosall + 4);
            } else {
                userData.put("nekos", nekos + 2);
                userData.put("nekosall", nekosall + 2);
            }
        }

        r.table("users")
                .get(user.getId())
                .update(r.hashMap("nekos", userData.get("nekos")).with("nekosall", userData.get("nekosall")))
                .run(conn);
    }

    public static void newUser(Message msg) {
        r.table("users")
                .insert(
                        r.array(
                                r.hashMap("id", msg.getAuthor().getId())
                                        .with("nekos", 0)
                                        .with("nekosall", 0)
                                        .with("exp", 0)
                                        .with("level", 0)
                                        .with("premium", 0)
                                        .with("name", getFullName(msg))
                                        .with("regdate", now())))
                .optArg("conflict", "update")
                .run(conn);
        NekoBot.log.info("New User: " + getFullName(msg));
    }

    public static void newGuild(Guild guild) {
        r.table("guilds")
                .insert(
                        r.array(
                                r.hashMap("id", guild.getId())
                                        .with("prefix", "~")
                                        .with("nekochannel", null)
                                        .with("msgcnt", 0)))
                .optArg("conflict", "update")
                .run(conn);
        NekoBot.log.info("New Guild: " + guild.getName());
    }

    public static void delGuild(Guild guild) {
        r.table("guilds").get(guild.getId()).delete().run(conn);
        NekoBot.log.info("Deleted Guild: " + guild.getName());
    }

    public static void setOptIn(Boolean optin, Boolean is18, String uid) {
        if (optin) {
            if (is18) {
                r.table("users").get(uid).update(r.hashMap("opted_in", 1).with("is_18", 1)).run(conn);
            } else {
                r.table("users").get(uid).update(r.hashMap("opted_in", 1)).run(conn);
            }
        } else {
            r.table("users").get(uid).update(r.hashMap("opted_in", 0).with("is_18", 0)).run(conn);
        }
    }

    public static void updateUser(Message message) {
        Map user = r.table("users").get(message.getAuthor().getId()).run(conn);
        long exp = (Long) user.get("exp");
        long level = (Long) user.get("level");
        Double curLevel = Math.floor(0.1 * Math.sqrt(exp));
        if (curLevel.longValue() > level) {
            NekoBot.log.info("LVL UP");
        }

        if (user.get("opted_in") == null) {
            r.table("users")
                    .get(message.getAuthor().getId())
                    .update(r.hashMap("opted_in", 0).with("is_18", 0))
                    .run(conn);
            NekoBot.log.info("Added new send fields for " + getFullName(message));
            NekoBot.log.info(getUser(message.getAuthor().getId()).toString());
        }

        if (user.get("premium") == null) {
            r.table("users").get(message.getAuthor().getId()).update(r.hashMap("premium", 0)).run(conn);
            NekoBot.log.info("Added premium field for " + getFullName(message));
        }
        if (isDonor_plus(message.getAuthor())) {
            NekoBot.log.info(getFullName(message) + " Is donor added 2x exp");
            r.table("users")
                    .get(message.getAuthor().getId())
                    .update(
                            r.hashMap("exp", exp + 2)
                                    .with("level", curLevel.longValue())
                                    .with("name", getFullName(message))
                                    .with("premium", 1))
                    .run(conn);
        } else {

            r.table("users")
                    .get(message.getAuthor().getId())
                    .update(
                            r.hashMap("exp", exp + 1)
                                    .with("level", curLevel.longValue())
                                    .with("name", getFullName(message))
                                    .with("premium", 0))
                    .run(conn);
        }
    }

    public static String getPrefix(Message msg) {
        if (IS_DEBUG) {
            return "~~~";
        }
        if (!BotChecks.isDm(msg)) {
            Map g = getGuild(msg.getGuild().getId());
            return g.get("prefix").toString();
        }
        return "~";
    }

    public static void setPrefix(String prefix, String id) {
        r.table("guilds").get(id).update(r.hashMap("prefix", prefix)).run(conn);
    }

    public static boolean hasPrefix(Message message) {
        if (message.getContentRaw().startsWith(getPrefix(message))) {
            return true;
        }
        return message.getContentRaw().startsWith(message.getJDA().getSelfUser().getAsMention());
    }

    public static boolean hasUser(Message msg) {
        return getUser(msg.getAuthor().getId()) != null;
    }

    public static boolean hasGuild(Guild guild) {
        return getGuild(guild.getId()) != null;
    }

    private static class ValueComparator implements Comparator<String> {
        Map<String, Long> base;

        ValueComparator(Map<String, Long> base) {
            this.base = base;
        }

        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
