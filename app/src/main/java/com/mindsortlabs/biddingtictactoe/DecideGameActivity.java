package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


//------------------------------------------Not used for now---------------------------------
public class DecideGameActivity extends AppCompatActivity {

    Button btnNormal, btnBidding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decide_game);

        btnNormal = (Button) findViewById(R.id.btn_normal);
        btnBidding = (Button) findViewById(R.id.btn_bidding);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {

        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_normal:
                intent = new Intent(this, DecidePlayOptionsNormalActivity.class);
                break;
            case R.id.btn_bidding:
                intent = new Intent(this, DecidePlayOptionsBiddingActivity.class);
                break;
            default:
                break;
        }

        startActivity(intent);
    }
}
