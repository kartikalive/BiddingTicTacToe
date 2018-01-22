package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.mindsortlabs.biddingtictactoe.ads.LazyAds;
import com.mindsortlabs.biddingtictactoe.log.LogUtil;
import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

import java.util.Random;

public class ChooseBiddingAndAds extends AppCompatActivity implements LazyAds.Implementable,AdapterView.OnItemSelectedListener {


    private final int REWARDED_COINS = 2;
    private final int MAX_REWARD_COINS = 30;
    private final int EASY_LEVEL_COINS = 100;
    private final int EXTRA_POSSIBLE_COINS = 101;

    Spinner spinner;
    int level = 0, totalRewardedCoins = 0;
    int userAndCpuTotalCoins;

    TextView userTotalCoins, cpuTotalCoins;
    private ImageButton mShowVideoButton;

    LazyAds lazyAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_choose_bidding_and_ads);
        userTotalCoins = findViewById(R.id.myBid);
        cpuTotalCoins = findViewById(R.id.CpuBid);

        totalRewardedCoins=0;
        userAndCpuTotalCoins=EASY_LEVEL_COINS;

        //set Up Spinner
        spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.levels, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        // Initialize the Mobile Ads SDK.

        userTotalCoins.setText(String.valueOf(userAndCpuTotalCoins));
        cpuTotalCoins.setText(String.valueOf(userAndCpuTotalCoins));

        // Create the "show" button, which shows a rewarded video if one is loaded.
        mShowVideoButton = findViewById(R.id.video);

        lazyAds = LazyAds.getInstance(getApplicationContext());
        lazyAds.initializeInterface(this);

        mShowVideoButton.setEnabled(lazyAds.isButtonEnabled());

        mShowVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lazyAds.showRewardedVideo();
            }
        });
        if (LogUtil.islogOn()) {
            Log.d("CREATE ", "OK " + userAndCpuTotalCoins);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner) {
            level = position;
            updateTotalCoins(position);
        }

    }

    private void updateTotalCoins(int position) {
        if (position > 2) return;

        Random r = new Random();
        int userAndCpuTotalCoins = 0;

        switch (position) {
            case 0:
                userAndCpuTotalCoins = EASY_LEVEL_COINS;
                break;
            case 1:
                userAndCpuTotalCoins = r.nextInt(EXTRA_POSSIBLE_COINS) + EASY_LEVEL_COINS;
                break;
            case 2:
                userAndCpuTotalCoins = r.nextInt(EXTRA_POSSIBLE_COINS * 2) + EASY_LEVEL_COINS;
                break;
        }

        userTotalCoins.setText(String.valueOf(userAndCpuTotalCoins + totalRewardedCoins));
        cpuTotalCoins.setText(String.valueOf(userAndCpuTotalCoins));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void startBiddingPlay(View view) {


        //mRewardedVideoAd.pause(this);
        MyPreferences myPreferences = new MyPreferences();
        myPreferences.saveMyandCPUTotalBid(this, Integer.parseInt(userTotalCoins.getText().toString()),
                Integer.parseInt(cpuTotalCoins.getText().toString()));
        myPreferences.saveLevel(this, level);
        Intent intent = new Intent(this, BoardPlayCPUBiddingActivity.class);
        startActivity(intent);
        //finish();

    }

    @Override
    public void addCoins() {
        totalRewardedCoins += REWARDED_COINS;
        totalRewardedCoins = Math.min(totalRewardedCoins, MAX_REWARD_COINS);
        userTotalCoins.setText(String.valueOf(
                userAndCpuTotalCoins + totalRewardedCoins));
    }

    @Override
    public void onPause() {
        super.onPause();
        lazyAds.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        lazyAds.onResume();
    }

    private void customToast(String message, int duration) {

        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_custom_toasty, null);

        TextView tvMsg = customView.findViewById(R.id.tv_msg);
        tvMsg.setText("  " + message + "");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(customView);

        toast.show();

    }


    @Override
    public void enableButton() {
        mShowVideoButton.setEnabled(true);
    }

    @Override
    public void disableButton() {
        mShowVideoButton.setEnabled(false);
    }

    @Override
    public void onRewardedAndVideoAdClosed() {
        customToast(String.format(" Rewarded! : %d  %s", REWARDED_COINS,"COINS"), Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(lazyAds!=null) {
            lazyAds.removeInterface();
            lazyAds = null;
        }
    }
}