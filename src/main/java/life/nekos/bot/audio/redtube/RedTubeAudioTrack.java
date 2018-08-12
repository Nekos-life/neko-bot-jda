package life.nekos.bot.audio.redtube;

import com.sedmelluq.discord.lavaplayer.container.mpeg.MpegAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

import java.net.URI;

public class RedTubeAudioTrack extends DelegatedAudioTrack {

    private final RedTubeAudioSourceManager sourceManager;

    public RedTubeAudioTrack(AudioTrackInfo trackInfo, RedTubeAudioSourceManager sourceManager) {
        super(trackInfo);

        this.sourceManager = sourceManager;
    }

    @Override
    public AudioTrack makeClone() {
        return new RedTubeAudioTrack(trackInfo, sourceManager);
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return sourceManager;
    }

    @Override
    public void process(LocalAudioTrackExecutor localExecutor) throws Exception {
        try (HttpInterface httpInterface = sourceManager.getHttpInterface()) {
            processStatic(localExecutor, httpInterface);
        }
    }

    private void processStatic(LocalAudioTrackExecutor localExecutor, HttpInterface httpInterface)
            throws Exception {
        try (RedTubePersistentHttpStream stream =
                     new RedTubePersistentHttpStream(httpInterface, new URI(trackInfo.identifier), null)) {
            // processDelegate(new MatroskaAudioTrack(trackInfo, stream), localExecutor);
            processDelegate(new MpegAudioTrack(trackInfo, stream), localExecutor);
        }
    }
}
