package com.mindsortlabs.biddingtictactoe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {

    public static final String prefKey = "com.mindsortlabs.biddingtictactoe";
    public static final String soundPrefAccessKey = "com.mindsortlabs.biddingtictactoe.sound";
    public static final String animatePrefAccessKey = "com.mindsortlabs.biddingtictactoe.animate";
    public static final String messagesPrefAccessKey = "com.mindsortlabs.biddingtictactoe.messages";
    public static int animatedPlay = 0;
    public static int soundEffects = 0;
    public static int messageNotifications = 1;
    ToggleButton animatedPlayBtn, soundToggleBtn;
    TextView tvNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_settings);

        animatedPlayBtn = (ToggleButton) findViewById(R.id.animated_play_btn);
        soundToggleBtn = (ToggleButton) findViewById(R.id.sound_toggle_btn);
        tvNote = (TextView) findViewById(R.id.tv_note);

        if (animatedPlay == 0) {
            animatedPlayBtn.setChecked(false);
            tvNote.setVisibility(View.INVISIBLE);
        } else {
            animatedPlayBtn.setChecked(true);
            tvNote.setVisibility(View.VISIBLE);
        }

        ImageButton btnTutorial = findViewById(R.id.btn_tutorial);
        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,TutorialActivity.class);
                startActivity(intent);
            }
        });
        animatedPlayBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    animatedPlay = 1;
                    tvNote.setVisibility(View.VISIBLE);
                } else {
                    animatedPlay = 0;
                    tvNote.setVisibility(View.INVISIBLE);
                }

                saveData(b, 2);
            }
        });


        if (soundEffects == 0) {
            soundToggleBtn.setChecked(false);
        } else {
            soundToggleBtn.setChecked(true);
        }

        soundToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    soundEffects = 1;
                } else {
                    soundEffects = 0;
                }

                saveData(b, 1);
            }
        });

    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void saveData(boolean b, int variable) {
        SharedPreferences prefs = getSharedPreferences(prefKey, Context.MODE_PRIVATE);

        if (variable == 1) {
            prefs.edit().putBoolean(soundPrefAccessKey, b).apply();
        } else if (variable == 2) {
            prefs.edit().putBoolean(animatePrefAccessKey, b).apply();
        }
    }

    public void onClose(View view) {
        finish();
    }
}
