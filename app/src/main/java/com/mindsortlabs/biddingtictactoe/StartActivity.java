package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mindsortlabs.biddingtictactoe.ai.BiddingTicTacToeAi;
import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;

import java.util.Vector;

public class StartActivity extends AppCompatActivity {

    Button quickPlayBtn, settingsBtn, exitBtn;
    NormalTicTacAi obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        quickPlayBtn = (Button) findViewById(R.id.quick_play_btn);
        settingsBtn = (Button) findViewById(R.id.settings_btn);
        exitBtn = (Button) findViewById(R.id.exit_btn);
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
                android.util.Pair<Integer, Integer> p  = obj.getSolution(board);
                Toast.makeText(this, "row: " + p.first + " col: "+p.second, Toast.LENGTH_SHORT).show();
                break;
            case R.id.quick_play_btn:
                intent = new Intent(this, DecideGameActivity.class);
                break;
            case R.id.settings_btn:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.exit_btn:
                this.finishAffinity();
                break;
            default:
                break;
        }

        startActivity(intent);
    }
}
