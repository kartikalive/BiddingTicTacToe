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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mindsortlabs.biddingtictactoe.log.LogUtil;
import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

import java.util.Random;

public class ChooseBiddingAndAds extends AppCompatActivity implements RewardedVideoAdListener, AdapterView.OnItemSelectedListener {

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";

    private static final int REWARDED_COINS = 2;
    private static final int MAX_REWARD_COINS = 30;
    private static final int EASY_LEVEL_COINS = 100;
    private static final int EXTRA_POSSIBLE_COINS = 101;

    Spinner spinner;
    int level = 0, totalRewardedCoins = 0;
    boolean isRewarded;
    int userAndCpuTotalCoins;

    TextView userTotalCoins, cpuTotalCoins;
    private RewardedVideoAd mRewardedVideoAd;
    private Button mShowVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_choose_bidding_and_ads);
        Log.d("OnCreate is called"," Are u shitting me");
        userTotalCoins = (TextView) findViewById(R.id.myBid);
        cpuTotalCoins = (TextView) findViewById(R.id.CpuBid);
        isRewarded =false ;
        totalRewardedCoins=0;
        userAndCpuTotalCoins=EASY_LEVEL_COINS;

        //set Up Spinner
        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.levels, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, APP_ID);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        Random r = new Random();
        userTotalCoins.setText(String.valueOf(userAndCpuTotalCoins));
        cpuTotalCoins.setText(String.valueOf(userAndCpuTotalCoins));


        // Create the "show" button, which shows a rewarded video if one is loaded.
        mShowVideoButton = findViewById(R.id.video);
        mShowVideoButton.setEnabled(false);
        mShowVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRewardedVideo();
            }
        });

        Log.d("CREATE ","OK " + userAndCpuTotalCoins);
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


        mRewardedVideoAd.pause(this);
        MyPreferences myPreferences = new MyPreferences();
        myPreferences.saveMyandCPUTotalBid(this, Integer.parseInt(userTotalCoins.getText().toString()),
                Integer.parseInt(cpuTotalCoins.getText().toString()));
        myPreferences.saveLevel(this, level);
        Intent intent = new Intent(this, BoardPlayCPUBiddingActivity.class);
        startActivity(intent);
        finish();

    }

    void addCoins() {
        totalRewardedCoins += REWARDED_COINS;
        totalRewardedCoins = Math.min(totalRewardedCoins, MAX_REWARD_COINS);
        userTotalCoins.setText(String.valueOf(
                userAndCpuTotalCoins + totalRewardedCoins));
    }

    @Override
    public void onPause() {
        super.onPause();
        mRewardedVideoAd.pause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRewardedVideoAd.resume(this);
    }

    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }


    private void showRewardedVideo() {
        mShowVideoButton.setEnabled(false);
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        if (LogUtil.islogOn()) {
            Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewardedVideoAdClosed() {
        if (LogUtil.islogOn()) {
            Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        }// Preload the next video ad.
        if(isRewarded){
            customToast(String.format(" Rewarded! : %s amount: %d","COINS", REWARDED_COINS), Toast.LENGTH_SHORT);
            isRewarded=false;
        }
        Log.d("REWARDED ","OK " + userAndCpuTotalCoins);
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        if (LogUtil.islogOn()) {
            Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {


        mShowVideoButton.setEnabled(true);
        if (LogUtil.islogOn()) {
            Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        if (LogUtil.islogOn()) {
            Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewarded(RewardItem reward) {
        isRewarded=true;
        addCoins();
    }

    @Override
    public void onRewardedVideoStarted() {
        if (LogUtil.islogOn()) {
            Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
        }
    }

    private void customToast(String message, int duration) {

        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_custom_toasty, null);

        TextView tvMsg = (TextView) customView.findViewById(R.id.tv_msg);
        tvMsg.setText("  " + message + "");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(customView);

        toast.show();

    }


}