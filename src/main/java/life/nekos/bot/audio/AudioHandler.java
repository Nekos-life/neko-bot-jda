package life.nekos.bot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import life.nekos.bot.commons.Colors;
import life.nekos.bot.commons.Formats;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static life.nekos.bot.NekoBot.musicManagers;
import static life.nekos.bot.NekoBot.playerManager;
import static life.nekos.bot.audio.VoiceHandler.connectToVoiceChannel;

/**
 * Created by Tom on 10/5/2017.
 */
public class AudioHandler {

    public static void loadAndPlay(Message message, String input, final boolean addPlaylist) {
        GuildMusicManager musicManager = getMusicManager(message.getGuild());
        if (input.startsWith("<") && input.endsWith(">")) {
            input = input.replace("<", "").replace(">", "");
        }
        Pattern pattern = Pattern.compile(Formats.httpRx);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            input = "ytsearch:" + input;
        }
        String url = input;
        playerManager.loadItemOrdered(
                musicManager,
                url,
                new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        MessageEmbed em =
                                new EmbedBuilder()
                                        .setAuthor(
                                                message.getJDA().getSelfUser().getName(),
                                                message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                                message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                        .addField(
                                                Formats.info("Queued " + Formats.PLAY_EMOTE),
                                                MessageFormat.format(
                                                        "Track: {0}\nLength: {1}",
                                                        track.getInfo().title, getTimestamp(track.getInfo().length)),
                                                false)
                                        .setColor(Colors.getEffectiveColor(message))
                                        .build();
                        track.setUserData(message.getAuthor());
                        play(message, musicManager, track);
                        message.getChannel().sendMessage(em).queue();
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        AudioTrack firstTrack = playlist.getSelectedTrack();
                        List<AudioTrack> tracks = playlist.getTracks();

                        if (firstTrack == null) {
                            firstTrack = playlist.getTracks().get(0);
                        }

                        if (addPlaylist) {
                            MessageEmbed em =
                                    new EmbedBuilder()
                                            .setAuthor(
                                                    message.getJDA().getSelfUser().getName(),
                                                    message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                                    message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                            .addField(
                                                    Formats.info("info"),
                                                    MessageFormat.format(
                                                            "Queued: **{0}** tracks {2} \nFrom: {1}",
                                                            playlist.getTracks().size(),
                                                            playlist.getName(),
                                                            Formats.PLAYLIST_EMOTE),
                                                    false)
                                            .setColor(Colors.getEffectiveColor(message))
                                            .build();
                            message.getChannel().sendMessage(em).queue();
                            connectToVoiceChannel(message);
                            tracks.forEach(
                                    audioTrack -> {
                                        audioTrack.setUserData(message.getAuthor());
                                        musicManager.scheduler.queue(audioTrack);
                                    });
                        } else {
                            MessageEmbed em =
                                    new EmbedBuilder()
                                            .setAuthor(
                                                    message.getJDA().getSelfUser().getName(),
                                                    message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                                    message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                            .addField(
                                                    Formats.info("Queued " + Formats.PLAY_EMOTE),
                                                    MessageFormat.format(
                                                            "Track: {0}\nLength: {1}\n{2}",
                                                            firstTrack.getInfo().title,
                                                            getTimestamp(firstTrack.getInfo().length),
                                                            playlist.getName()),
                                                    false)
                                            .setColor(Colors.getEffectiveColor(message))
                                            .build();
                            message.getChannel().sendMessage(em).queue();
                            firstTrack.setUserData(message.getAuthor());
                            play(message, musicManager, firstTrack);
                        }
                    }

                    @Override
                    public void noMatches() {
                        message
                                .getChannel()
                                .sendMessage(Formats.error("I couldn't find anything for " + url + ", nya~"))
                                .queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        message
                                .getChannel()
                                .sendMessage(
                                        Formats.error("nuu i cant nya~ something exploded: " + exception.getMessage()))
                                .queue();
                    }
                });
    }

    private static void play(Message message, GuildMusicManager musicManager, AudioTrack track) {
        connectToVoiceChannel(message);
        musicManager.scheduler.queue(track);
    }

    public static GuildMusicManager getMusicManager(Guild guild) {
        String guildId = guild.getId();
        GuildMusicManager musicManager = musicManagers.get(guildId);
        if (musicManager == null) {
            musicManager = musicManagers.get(guildId);
            if (musicManager == null) {
                musicManager = new GuildMusicManager(playerManager);
                musicManager.player.setVolume(33);
                musicManagers.put(guildId, musicManager);
            }
        }
        return musicManager;
    }

    public static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0) return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else return String.format("%02d:%02d", minutes, seconds);
    }
}
