package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

import java.util.Random;

public class ChooseBiddingAndAds extends AppCompatActivity {

    TextView userTotalCoins, cpuTotalCoins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_choose_bidding_and_ads);
        userTotalCoins = (TextView)findViewById(R.id.myBid);
        cpuTotalCoins = (TextView)findViewById(R.id.CpuBid);

        Random r = new Random();
        int userAndCpuTotalCoins = r.nextInt(101)+100;
        userTotalCoins.setText(String.valueOf(userAndCpuTotalCoins));
        cpuTotalCoins.setText(String.valueOf(userAndCpuTotalCoins));

    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void customPlay(View view) {

        MyPreferences myPreferences = new MyPreferences();
        myPreferences.saveMyandCPUTotalBid(this,Integer.parseInt(userTotalCoins.getText().toString()),
                Integer.parseInt(cpuTotalCoins.getText().toString()) );

        Intent intent = new Intent(this, BoardPlayCPUBiddingActivity.class);
        startActivity(intent);

    }
}

