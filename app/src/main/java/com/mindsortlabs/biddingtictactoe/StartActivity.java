package com.mindsortlabs.biddingtictactoe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;

public class StartActivity extends AppCompatActivity {

    ImageButton biddingGameBtn, primitiveGameBtn, settingsBtn, instructionsBtn, exitBtn, multiplayerBtn;
    NormalTicTacAi obj;
    int backPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideStatusBar();
        setContentView(R.layout.activity_start);

        primitiveGameBtn = (ImageButton) findViewById(R.id.btn_primitive_game);
        biddingGameBtn = (ImageButton) findViewById(R.id.btn_bidding_game);
        //customGameBtn = (Button) findViewById(R.id.btn_custom_game);
        settingsBtn = (ImageButton) findViewById(R.id.btn_options);
        instructionsBtn = (ImageButton) findViewById(R.id.btn_instructions);
        multiplayerBtn = (ImageButton) findViewById(R.id.btn_multiplayer_game);


//        hideStatusBar();
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.prefKey, Context.MODE_PRIVATE);

        if (prefs.getBoolean(SettingsActivity.soundPrefAccessKey, false)) {
            SettingsActivity.soundEffects = 1;
        }
        if (prefs.getBoolean(SettingsActivity.animatePrefAccessKey, false)) {
            SettingsActivity.animatedPlay = 1;
        }
//        SettingsActivity.animatedPlay =
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

   /* @Override
    public void onBackPressed() {
        if(backPressed==0) {
            backPressed = 1;
            backPressTimer();
            Toast.makeText(this, "Press back again to Exit", Toast.LENGTH_SHORT).show();
        }

        else if(backPressed==1) {
            finishAffinity();
        }
    }

    private void backPressTimer() {
        CountDownTimer timer = new CountDownTimer(1000, 10) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                backPressed = 0;
            }
        };

        timer.start();
    }*/

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.btn_bidding_game:
//                Toast.makeText(this, "Opening Anything for now.", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, DecidePlayOptionsBiddingActivity.class);
                break;
            case R.id.btn_multiplayer_game:
                intent = new Intent(this, BoardPlayMultiplayerActivity.class);
                break;
            case R.id.btn_primitive_game:
                intent = new Intent(this, DecidePlayOptionsNormalActivity.class);
                break;
            case R.id.btn_options:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.btn_instructions:
                intent = new Intent(this, InstructionActivity.class);
                break;

            default:
                break;
        }


        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Want To Quit ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
//                        hideStatusBar();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
//                hideStatusBar();
            }
        });
        builder.show();
//        hideStatusBar();
    }
}
