package com.mindsortlabs.biddingtictactoe;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.mindsortlabs.biddingtictactoe.log.LogUtil;

import static android.content.Context.AUDIO_SERVICE;

public class SoundActivity {

    public static int soundNumber = 0;
    MediaPlayer mediaPlayer;

    public static void playSound(Context context, SoundPool soundPool, boolean soundLoaded, int soundId) {

        if (SettingsActivity.soundEffects == 1) {
            AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
            float actualVolume = (float) audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            // Is the sound loaded already?
            if (LogUtil.islogOn()) {
                Log.d("Soundcheck", soundLoaded + "");
            }
            if (soundLoaded) {
                soundPool.play(soundId, volume, volume, 1, 0, 1f);
            }
        }
    }
}
