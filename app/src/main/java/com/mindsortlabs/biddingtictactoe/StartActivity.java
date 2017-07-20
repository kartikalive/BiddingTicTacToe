package com.mindsortlabs.biddingtictactoe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mindsortlabs.biddingtictactoe.ai.BiddingTicTacToeAi;
import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;
import com.mindsortlabs.biddingtictactoe.custom.BoardChooser;

import java.util.Vector;

public class StartActivity extends AppCompatActivity {

    Button primitiveGameBtn , customGameBtn, settingsBtn, exitBtn;
    ImageButton biddingGameBtn;
    NormalTicTacAi obj;
    int backPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        primitiveGameBtn = (Button) findViewById(R.id.btn_primitive_game);
        biddingGameBtn = (ImageButton) findViewById(R.id.btn_bidding_game);
        customGameBtn = (Button) findViewById(R.id.btn_custom_game);
        settingsBtn = (Button) findViewById(R.id.settings_btn);
        exitBtn = (Button) findViewById(R.id.exit_btn);


        SharedPreferences prefs = getSharedPreferences(SettingsActivity.prefKey, Context.MODE_PRIVATE);

        if(prefs.getBoolean(SettingsActivity.soundPrefAccessKey,false)){
            SettingsActivity.soundEffects = 1;
        }
        if(prefs.getBoolean(SettingsActivity.animatePrefAccessKey,false)){
            SettingsActivity.animatedPlay = 1;
        }
//        SettingsActivity.animatedPlay =
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

    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()){


            case R.id.btn_bidding:
                Vector<String> board = new Vector<String>(3);
                board.add("O__");
                board.add("___");
                board.add("___");
                obj = new NormalTicTacAi();
//                android.util.Pair<Integer, Integer> p  = obj.getSolution(board);
//                Toast.makeText(this, "row: " + p.first + " col: "+p.second, Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_bidding_game:
                Toast.makeText(this, "Opening Anything for now.", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, DecideGameActivity.class);
                break;
            case R.id.btn_primitive_game:
                intent = new Intent(this, DecideGameActivity.class);
                break;
            case R.id.btn_custom_game:
                intent = new Intent(this, BoardChooser.class);
                break;

            case R.id.settings_btn:
                intent = new Intent(this, SettingsActivity.class);
                break;

            case R.id.exit_btn:
                backPressed = 1;
                onBackPressed();
                break;
            default:
                break;
        }


        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {

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
                    }
                });
        builder.show();

    }
}
