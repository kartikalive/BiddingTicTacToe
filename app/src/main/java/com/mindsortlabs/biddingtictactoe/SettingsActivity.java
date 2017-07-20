package com.mindsortlabs.biddingtictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {

    public static int animatedPlay = 0;
    public static int soundEffects = 0;
    public static final String prefKey = "com.mindsortlabs.biddingtictactoe";
    public static final String soundPrefAccessKey = "com.mindsortlabs.biddingtictactoe.sound";
    public static final String animatePrefAccessKey = "com.mindsortlabs.biddingtictactoe.animate";
    ToggleButton animatedPlayBtn, soundToggleBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        animatedPlayBtn = (ToggleButton) findViewById(R.id.animated_play_btn);
        soundToggleBtn = (ToggleButton) findViewById(R.id.sound_toggle_btn);

        if(animatedPlay==0){
            animatedPlayBtn.setChecked(false);
        }
        else{
            animatedPlayBtn.setChecked(true);
        }

        animatedPlayBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    animatedPlay = 1;
                }

                else{
                    animatedPlay = 0;
                }

                saveData(b,2);
            }
        });


        if(soundEffects==0){
            soundToggleBtn.setChecked(false);
        }
        else{
            soundToggleBtn.setChecked(true);
        }

        soundToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    soundEffects = 1;
                }

                else{
                    soundEffects = 0;
                }

                saveData(b,1);
            }
        });

    }

    private void saveData(boolean b, int variable) {
        SharedPreferences prefs = getSharedPreferences(prefKey, Context.MODE_PRIVATE );

        if(variable==1) {
            prefs.edit().putBoolean(soundPrefAccessKey, b).apply();
        }

        else if(variable==2){
            prefs.edit().putBoolean(animatePrefAccessKey, b).apply();
        }
    }

    public void onClose(View view) {
        finish();
    }
}
