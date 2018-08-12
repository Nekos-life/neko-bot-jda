package life.nekos.bot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.User;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final Queue<AudioTrack> queue;
    public AudioTrack lastTrack;
    private boolean loop = false;
    private boolean loopall = false;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {

        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is
        // currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already
        // playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was
        // empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;
        User user = (User) track.getUserData();
        if (endReason.mayStartNext) {
            AudioTrack ltrack = track.makeClone();
            ltrack.setUserData(user);
            if (loop) {
                player.startTrack(ltrack, false);
                return;
            }
            if (loopall) {
                queue.add(ltrack);
                nextTrack();
            } else {
                nextTrack();
            }
        }
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        nextTrack();
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean repeating) {
        this.loop = repeating;
    }

    public boolean isLoopall() {
        return loopall;
    }

    public void setLoopall(boolean loopall) {
        this.loopall = loopall;
    }
}
