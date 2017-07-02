package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DecidePlayOptionsBiddingActivity extends AppCompatActivity {

    Button btnCPU, btnTwoPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decide_play_options_bidding);

        btnCPU = (Button) findViewById(R.id.btn_cpu);
        btnTwoPlayers = (Button) findViewById(R.id.btn_two_players);
    }

    public void onClick(View v){

        Intent intent = null;

        switch (v.getId()){
            case R.id.btn_cpu:
                if(SettingsActivity.animatedPlay==0){
                    intent = new Intent(this, BoardPlayCPUBiddingActivity.class);
                }
                else{
                    intent = new Intent(this, DrawingPlayCPUBiddingActivity.class);
                }
                break;
            case R.id.btn_two_players:
                if(SettingsActivity.animatedPlay==0){
                    intent = new Intent(this, BoardPlay2PlayerBiddingActivity.class);
                }
                else{
                    intent = new Intent(this, DrawingPlay2PlayerBiddingActivity.class);
                }
                break;
            default:
                break;

        }

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DecideGameActivity.class);
        startActivity(intent);
    }
}
