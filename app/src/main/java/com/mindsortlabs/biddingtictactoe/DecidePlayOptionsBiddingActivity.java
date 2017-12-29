package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class DecidePlayOptionsBiddingActivity extends AppCompatActivity {

    ImageButton btnCPU, btnTwoPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_decide_play_options_bidding);

        btnCPU = (ImageButton) findViewById(R.id.btn_practice);
        btnTwoPlayers = (ImageButton) findViewById(R.id.btn_two_players);
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {
            case R.id.btn_practice:
                if (SettingsActivity.animatedPlay == 0) {
                    intent = new Intent(this, ChooseBiddingAndAds.class);
                } else {
                    intent = new Intent(this, ChooseBiddingAndAds.class);
                }
                break;
            case R.id.btn_two_players:
                if (SettingsActivity.animatedPlay == 0) {
                    intent = new Intent(this, BoardPlay2PlayerBiddingActivity.class);
                } else {
                    intent = new Intent(this, BoardPlay2PlayerBiddingActivity.class);
                }
                break;
            default:
                break;

        }

        startActivity(intent);
        //finish();
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }*/
}
