/*
 Created by Tom on 9/18/2017.
*/
package life.nekos.bot;

import ch.qos.logback.classic.Logger;
import com.github.rainestormee.jdacommand.CommandHandler;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import life.nekos.bot.audio.GuildMusicManager;
import life.nekos.bot.audio.pornhub.PornHubAudioSourceManager;
import life.nekos.bot.audio.redtube.RedTubeAudioSourceManager;
import life.nekos.bot.commons.Misc;
import life.nekos.bot.handlers.EventHandler;
import life.nekos.bot.handlers.MessageHandler;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ch.qos.logback.classic.Level.DEBUG;
import static ch.qos.logback.classic.Level.INFO;
import static life.nekos.bot.commons.Constants.*;

public class NekoBot {
    public static final CommandHandler commandHandler = new CommandHandler();
    public static AudioPlayerManager playerManager;
    public static Map<String, GuildMusicManager> musicManagers;
    public static EventWaiter waiter = new EventWaiter();
    public static Logger log = (Logger) LoggerFactory.getLogger(NekoBot.class);
    public static ScheduledExecutorService Scheduler = Executors.newSingleThreadScheduledExecutor();
    public static PokeApi pokeApi = new PokeApiClient();

    public static void main( String[] args) throws Exception {
        NekoBot.Scheduler.scheduleAtFixedRate(Misc.autoAvatar(), 1, 2, TimeUnit.HOURS);
        log.info(JDAInfo.VERSION);
        log.setLevel(INFO);

        if (args.length > 0 && args[0].contains("debug") || IS_DEBUG) {
            IS_DEBUG = true;
            TOKEN = DEBUG_TOKEN;
            log.setLevel(DEBUG);
            log.warn("Running in debug");
        }

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new PornHubAudioSourceManager());
        playerManager.registerSourceManager(new RedTubeAudioSourceManager());
        playerManager.registerSourceManager(new NicoAudioSourceManager(NICO_EMAIL, NICO_PASS));
        playerManager.getConfiguration().setOpusEncodingQuality(10);
        playerManager
                .getConfiguration()
                .setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        musicManagers = new ConcurrentHashMap<>();
        commandHandler.registerCommands(new CommandRegistry().getCommands());

        new DefaultShardManagerBuilder()
                .setGame(Game.playing("https://nekos.life"))
                .setAudioSendFactory(new NativeAudioSendFactory())
                .addEventListeners(new MessageHandler(commandHandler), new EventHandler(), waiter)
                .setToken(TOKEN)
                .setShardsTotal(-1)
                .build();

    }
}