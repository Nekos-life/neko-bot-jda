package life.nekos.bot.handlers;
/**
 * Created by Tom on 9/19/2017.
 */

import com.google.common.base.Joiner;
import life.nekos.bot.NekoBot;
import life.nekos.bot.audio.VoiceHandler;
import life.nekos.bot.commons.Constants;
import life.nekos.bot.commons.Formats;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static life.nekos.bot.audio.VoiceHandler.isEmptyVc;
import static life.nekos.bot.commons.Misc.now;
import static life.nekos.bot.commons.Misc.webhookClient;
import static life.nekos.bot.server.Server.StartServer;

public class EventHandler extends ListenerAdapter {
    private static JDA JDA;
    private static Guild HOME;
    private static ShardManager Shards;
    private static Boolean READY = Boolean.FALSE;
    private int c = 0;

    public static Boolean getREADY() {
        return READY;
    }

    public static void setREADY(Boolean READY) {
        EventHandler.READY = READY;
    }

    public static JDA getJDA() {
        return JDA;
    }

    private void setJDA(JDA JDA) {
        EventHandler.JDA = JDA;
    }

    public static ShardManager getShards() {
        return Shards;
    }

    private static void setShards(ShardManager shards) {
        Shards = shards;
    }

    public static Guild getHOME() {
        return HOME;
    }

    private static void setHOME(Guild HOME) {
        EventHandler.HOME = HOME;
    }

    public void onReady(ReadyEvent event) {
        WebhookClient readyClient = webhookClient(Constants.RDY_WEBHOOK);
        c += 1;
        JDA jda = event.getJDA();
        setShards(jda.asBot().getShardManager());
        setJDA(jda);
        List<String> pinglist = new ArrayList<>();
        Map<JDA, net.dv8tion.jda.core.JDA.Status> s = getJDA().asBot().getShardManager().getStatuses();
        for (Map.Entry<JDA, net.dv8tion.jda.core.JDA.Status> e : s.entrySet()) {
            if (jda.getShardInfo().getShardId() == e.getKey().getShardInfo().getShardId()) {
                pinglist.add(
                        MessageFormat.format(
                                "Shard: {0}, Ping: {1}ms, Status: {2} (This Guild)\n",
                                e.getKey().getShardInfo().getShardId(), e.getKey().getPing(), e.getValue()));
            } else {
                pinglist.add(
                        MessageFormat.format(
                                "Shard: {0}, Ping: {1}ms, Status: {2}\n",
                                e.getKey().getShardInfo().getShardId(), e.getKey().getPing(), e.getValue()));
            }
        }
        System.out.println(Joiner.on("").join(pinglist));
	    readyClient.send(new WebhookMessageBuilder().setUsername("boot log").setAvatarUrl(jda.getSelfUser().getEffectiveAvatarUrl()).setContent("```\n" + Joiner.on("").join(pinglist) + "\n```\n\n").build());
        NekoBot.log.info(jda.getShardInfo().getShardString());
        // jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.watching("Nekos owo"));
        if (c == jda.asBot().getShardManager().getShardsTotal()) {
            setREADY(Boolean.TRUE);
            setHOME(getJDA().asBot().getShardManager().getGuildById(Constants.HOME_GUILD));
            System.out.println(getHOME().getSelfMember().getOnlineStatus().toString());
            event.getJDA().asBot().getShardManager().setGame(Game.playing("~help | Guilds: " + jda.asBot().getShardManager().getGuilds().toArray().length));
            readyClient.send(
                    new WebhookMessageBuilder()
                            .setContent("owo")
                            .addEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Color.magenta)
                                            .setAuthor(
                                                    jda.getSelfUser().getName(),
                                                    jda.getSelfUser().getEffectiveAvatarUrl(),
                                                    jda.getSelfUser().getEffectiveAvatarUrl())
                                            .setTitle(
                                                    MessageFormat.format(
                                                            "{0} Ready today at {1}", jda.getSelfUser().getName(), now()),
                                                    jda.asBot().getInviteUrl(Permission.ADMINISTRATOR))
                                            .setThumbnail(jda.getSelfUser().getEffectiveAvatarUrl())
                                            .setDescription("Ready info")
                                            .addField("neko", "```" + Formats.getReadyFormat(jda, HOME) + "```", false)
                                            .setFooter("\\o/", null)
                                            .build())
                            .setUsername(jda.getSelfUser().getName())
                            .setAvatarUrl(jda.getSelfUser().getEffectiveAvatarUrl())
                            .build());
            readyClient.close();
            StartServer();
            NekoBot.log.info(Formats.getReadyFormat(getJDA(), getHOME()));
        }
        readyClient.close();
    }

    public void onGuildJoin(GuildJoinEvent event) {
        JDA jda = event.getJDA();
        Guild guild = event.getGuild();
        event.getJDA().asBot().getShardManager().setGame(Game.playing("~help | Guilds: " + jda.asBot().getShardManager().getGuilds().toArray().length));
        NekoBot.log.info(
                MessageFormat.format("New Guild Joined {0}({1})", guild.getName(), guild.getId()));
        if (!Models.hasGuild(guild)) {
            Models.newGuild(guild);
        }
        MessageEmbed em =
                new EmbedBuilder()
                        .setColor(Color.green)
                        .setAuthor(guild.getName(), guild.getIconUrl(), guild.getIconUrl())
                        .setTitle(MessageFormat.format("Joined {0} today at {1}", guild.getName(), now()), null)
                        .setThumbnail(guild.getIconUrl())
                        .setDescription("Guild info")
                        .addField(
                                Formats.info("info"),
                                MessageFormat.format(
                                        "**{0}**\nGuilds: **{1}**\nOwner: **{3}**\nGuild Users: **{2}**\n",
                                        guild.getJDA().getShardInfo(),
                                        jda.asBot().getShardManager().getGuilds().toArray().length,
                                        guild.getMembers().toArray().length,
                                        guild.getOwner().getEffectiveName()),
                                false)
                        .setFooter("\\o/", null)
                        .build();
        WebhookClient guildJoinClient = webhookClient(Constants.GJLOG_WEBHOOK);
        guildJoinClient.send(
                new WebhookMessageBuilder()
                        .addEmbeds(em)
                        .setUsername(guild.getName())
                        .setAvatarUrl(guild.getIconUrl())
                        .build());
        guildJoinClient.close();
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        JDA jda = event.getJDA();
        Guild guild = event.getGuild();
        event.getJDA().asBot().getShardManager().setGame(Game.playing("~help | Guilds: " + jda.asBot().getShardManager().getGuilds().toArray().length));
        NekoBot.log.info(MessageFormat.format("Guild left {0}({1})", guild.getName(), guild.getId()));
        if (Models.getGuild(event.getGuild().getId()) != null) {
            Models.delGuild(guild);
        }
        WebhookClient guildLeaveClient = webhookClient(Constants.GLLOG_WEBHOOK);
        guildLeaveClient.send(
                new WebhookMessageBuilder()
                        .addEmbeds(
                                new EmbedBuilder()
                                        .setColor(Color.red)
                                        .setAuthor(guild.getName(), guild.getIconUrl(), guild.getIconUrl())
                                        .setTitle(MessageFormat.format("Left {0} today at {1}", guild.getName(), now()))
                                        .setThumbnail(guild.getIconUrl())
                                        .setDescription("Guild info")
                                        .addField(
                                                "info",
                                                MessageFormat.format(
                                                        "**{0}**\nGuilds: **{1}**\nOwner: **{3}**\nGuild Users: **{2}**\n",
                                                        guild.getJDA().getShardInfo(),
                                                        jda.asBot().getShardManager().getGuilds().size(),
                                                        guild.getMembers().toArray().length,
                                                        guild.getOwner().getEffectiveName()),
                                                false)
                                        .setFooter("\\o/", null)
                                        .build())
                        .setUsername(guild.getName())
                        .setAvatarUrl(guild.getIconUrl())
                        .build());
        guildLeaveClient.close();
    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getGuild().getAudioManager().getConnectedChannel() == event.getChannelLeft()) {
            if (isEmptyVc(event.getChannelLeft())) {
                Runnable task = VoiceHandler.check(event.getChannelLeft());
                NekoBot.Scheduler.schedule(task, 5, TimeUnit.MINUTES);
                NekoBot.log.debug("scheduled vc disconnect for " + event.getChannelLeft().getName());
            }
        }
    }

    public void onResume(ResumedEvent event) {
        JDA jda = event.getJDA();
        NekoBot.log.info(
                MessageFormat.format("Resumed on shard: {0}", event.getJDA().getShardInfo().getShardId()));
        WebhookClient resumeClient = webhookClient(Constants.RDY_WEBHOOK);
        resumeClient.send(
                new WebhookMessageBuilder()
                        .addEmbeds(
                                new EmbedBuilder()
                                        .setColor(Color.magenta)
                                        .setAuthor(
                                                jda.getSelfUser().getName(),
                                                jda.getSelfUser().getEffectiveAvatarUrl(),
                                                jda.getSelfUser().getEffectiveAvatarUrl())
                                        .setTitle(
                                                MessageFormat.format(
                                                        "{0} Resumed today at {1}", jda.getSelfUser().getName(), now()),
                                                jda.asBot().getInviteUrl())
                                        .setThumbnail(jda.getSelfUser().getEffectiveAvatarUrl())
                                        .setDescription("Ready info")
                                        .addField(
                                                "Stats",
                                                MessageFormat.format(
                                                        "**{0}**\nGuilds: **{1}**\nUsers: **{2}**\n",
                                                        jda.getShardInfo(),
                                                        jda.getGuilds().toArray().length,
                                                        jda.getUsers().toArray().length),
                                                false)
                                        .setFooter("\\o/", null)
                                        .build())
                        .setUsername(jda.getSelfUser().getName())
                        .setAvatarUrl(jda.getSelfUser().getEffectiveAvatarUrl())
                        .build());
        resumeClient.close();
    }

    public void onReconnect(ReconnectedEvent event) {
        JDA jda = event.getJDA();
        NekoBot.log.warn(
                MessageFormat.format(
                        "reconnected on shard: {0}", event.getJDA().getShardInfo().getShardId()));
        WebhookClient reconnectClient = webhookClient(Constants.RDY_WEBHOOK);
        reconnectClient.send(
                new WebhookMessageBuilder()
                        .addEmbeds(
                                new EmbedBuilder()
                                        .setColor(Color.magenta)
                                        .setAuthor(
                                                jda.getSelfUser().getName(),
                                                jda.getSelfUser().getEffectiveAvatarUrl(),
                                                jda.getSelfUser().getEffectiveAvatarUrl())
                                        .setTitle(
                                                MessageFormat.format(
                                                        "{0} Reconnected today at {1}", jda.getSelfUser().getName(), now()),
                                                jda.asBot().getInviteUrl(Permission.ADMINISTRATOR))
                                        .setThumbnail(jda.getSelfUser().getEffectiveAvatarUrl())
                                        .setDescription("Ready info")
                                        .addField(
                                                "Stats",
                                                MessageFormat.format(
                                                        "**{0}**\nGuilds: **{1}**\nUsers: **{2}**\n",
                                                        jda.getShardInfo(),
                                                        jda.getGuilds().toArray().length,
                                                        jda.getUsers().toArray().length),
                                                false)
                                        .setFooter("\\o/", null)
                                        .build())
                        .setUsername(jda.getSelfUser().getName())
                        .setAvatarUrl(jda.getSelfUser().getEffectiveAvatarUrl())
                        .build());
        reconnectClient.close();
    }

    public void onDisconnect(DisconnectEvent event) {

        WebhookClient disconnectClient = webhookClient(Constants.RDY_WEBHOOK);
        NekoBot.log.warn(
                MessageFormat.format(
                        "Disconnected on shard: {0}", event.getJDA().getShardInfo().getShardId()));
        try {
            disconnectClient.send(
                    new WebhookMessageBuilder().setUsername(event.getJDA().getSelfUser().getName())
                            .setAvatarUrl(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                            .addEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Color.red)
                                            .addField("rip", MessageFormat.format("Disconnected on shard: {0}", event.getJDA().getShardInfo().getShardId()), false)
                                            .addField("Time", event.getDisconnectTime().toString(), false)
                                            .build())
                            .build());
            disconnectClient.close();
        } catch (Exception e) {
            disconnectClient.close();
            NekoBot.log.error("broke? ", e);
        }
    }
}
