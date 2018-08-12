package life.nekos.bot.audio;

import life.nekos.bot.NekoBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

import static life.nekos.bot.audio.AudioHandler.getMusicManager;

public class VoiceHandler {

    public static boolean isEmptyVc(VoiceChannel channel) {
        return channel
                .getMembers()
                .stream()
                .filter(member -> !member.getUser().isBot())
                .toArray()
                .length
                == 0;
    }

    public static boolean inVoice(Message message) {
        return message.getMember().getVoiceState().inVoiceChannel();
    }

    public static boolean sameVoice(Message message) {
        return inVoice(message)
                && message.getMember().getVoiceState().getChannel()
                == message.getGuild().getAudioManager().getConnectedChannel();
    }

    public static void disconnectFromVoice(VoiceChannel channel) {
        getMusicManager(channel.getGuild()).scheduler.queue.clear();
        getMusicManager(channel.getGuild()).player.stopTrack();
        getMusicManager(channel.getGuild()).player.setPaused(false);
        getMusicManager(channel.getGuild()).player.destroy();
        channel.getGuild().getAudioManager().setSendingHandler(null);
        channel.getGuild().getAudioManager().closeAudioConnection();
        NekoBot.musicManagers.remove(channel.getGuild().getId());
    }

    static void connectToVoiceChannel(Message message) {
        if (!message.getGuild().getAudioManager().isConnected()
                && !message.getGuild().getAudioManager().isAttemptingToConnect()) {
            message
                    .getGuild()
                    .getAudioManager()
                    .setSendingHandler(getMusicManager(message.getGuild()).sendHandler);
            message
                    .getGuild()
                    .getAudioManager()
                    .openAudioConnection(message.getMember().getVoiceState().getChannel());
        }
    }

    public static Runnable check(final VoiceChannel channel) {
        return () -> {
            if (isEmptyVc(channel)) {
                disconnectFromVoice(channel);
            }
        };
    }
}
