package com.mindsortlabs.biddingtictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DecideGameActivity extends AppCompatActivity {

    Button btnNormal, btnBidding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decide_game);

        btnNormal = (Button) findViewById(R.id.btn_normal);
        btnBidding = (Button) findViewById(R.id.btn_bidding);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_normal:

        }
    }
}
