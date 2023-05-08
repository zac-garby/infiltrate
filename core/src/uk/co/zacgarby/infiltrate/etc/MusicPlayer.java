package uk.co.zacgarby.infiltrate.etc;

import com.badlogic.gdx.audio.Music;

public class MusicPlayer {
    private Music playing, queued;
    private float secondsPerBar = 0.5f, queuedSecondsPerBar;
    private float time;

    public void queue(Music music, float secondsPerBar) {
        queued = music;
        queuedSecondsPerBar = secondsPerBar;
    }

    public void update(float dt) {
        time += dt;

        if (time > secondsPerBar) {
            time = 0f;

            if (queued != null && queued != playing) {
                if (playing != null && playing.isPlaying()) {
                    playing.stop();
                }

                queued.play();
                secondsPerBar = queuedSecondsPerBar;

                playing = queued;
                queued = null;
            }
        }
    }
}
