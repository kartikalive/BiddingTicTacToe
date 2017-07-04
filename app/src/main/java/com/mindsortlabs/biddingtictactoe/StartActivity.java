package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mindsortlabs.biddingtictactoe.ai.BiddingTicTacToeAi;
import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;
import com.mindsortlabs.biddingtictactoe.custom.BoardChooser;

import java.util.Vector;

public class StartActivity extends AppCompatActivity {

    Button quickPlayBtn, settingsBtn, exitBtn;
    NormalTicTacAi obj;
    int backPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        quickPlayBtn = (Button) findViewById(R.id.quick_play_btn);
        settingsBtn = (Button) findViewById(R.id.settings_btn);
        exitBtn = (Button) findViewById(R.id.exit_btn);
    }

    @Override
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
    }

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
            case R.id.quick_play_btn:
                intent = new Intent(this, DecideGameActivity.class);
                break;
            case R.id.btn_custom:
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
}
