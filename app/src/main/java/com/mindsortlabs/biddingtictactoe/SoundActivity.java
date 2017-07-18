package com.mindsortlabs.biddingtictactoe;

import android.media.MediaPlayer;

/**
 * Created by kartik1 on 09-07-2017.
 */

public class SoundActivity extends MediaPlayer{

    MediaPlayer mediaPlayer;
    public static int soundNumber = 0;

    public SoundActivity(int number) {
        soundNumber = number;

    }
}
