package com.mindsortlabs.biddingtictactoe;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mindsortlabs.biddingtictactoe.ads.LazyAds;
import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;
import com.mindsortlabs.biddingtictactoe.log.LogUtil;
import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class StartActivity extends AppCompatActivity {

    ImageButton biddingGameBtn, primitiveGameBtn, settingsBtn, instructionsBtn, exitBtn, multiplayerBtn;
    NormalTicTacAi obj;
    int backPressed = 0;
    LazyAds lazyAds;
    int tutorialStatus = 0;

    private int MAX_MOBILE_RAM_MB_AVAILABLE = 1700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideStatusBar();
        setContentView(R.layout.activity_start);

        primitiveGameBtn = findViewById(R.id.btn_primitive_game);
        biddingGameBtn = findViewById(R.id.btn_bidding_game);
        //customGameBtn = (Button) findViewById(R.id.btn_custom_game);
        settingsBtn = findViewById(R.id.btn_options);
        instructionsBtn = findViewById(R.id.btn_instructions);
        multiplayerBtn = findViewById(R.id.btn_multiplayer_game);
        lazyAds = LazyAds.getInstance(getApplicationContext());

//        hideStatusBar();
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.prefKey, Context.MODE_PRIVATE);

        if (prefs.getBoolean(SettingsActivity.soundPrefAccessKey, false)) {
            SettingsActivity.soundEffects = 1;
        }
        if (prefs.getBoolean(SettingsActivity.animatePrefAccessKey, false)) {
            SettingsActivity.animatedPlay = 1;
        }
//        SettingsActivity.animatedPlay =
        if(LogUtil.islogOn()){
            Log.d(StartActivity.class.getSimpleName(),"onCREATE");
        }
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
/*
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

    private long totalRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.totalMem / 1048576L;
        if(LogUtil.islogOn()){
            Log.d(StartActivity.class.getSimpleName()," "+availableMegs );
        }
        return availableMegs;
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.btn_bidding_game:

                MyPreferences myPreferences = new MyPreferences();
                tutorialStatus = myPreferences.getTutorialStatus(this);
                Log.d("TAGTutorial", tutorialStatus+"");

                if(tutorialStatus==0) {
                    if(totalRamMemorySize()>MAX_MOBILE_RAM_MB_AVAILABLE) {
                        intent = new Intent(this, TutorialActivity.class);
                    }else{
                        //DIFFERENT TUTORIAL NEEDED
                        //FOR NOW DecidePlayOptionsBiddingActivity
                        intent = new Intent(this, DecidePlayOptionsBiddingActivity.class);
                    }
                }
                else{
                    intent = new Intent(this, DecidePlayOptionsBiddingActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.btn_multiplayer_game:
                if(!isNetworkAvailable()){
                    showNetworkError();
                }
                else {

                    MyPreferences preferences = new MyPreferences();
                    tutorialStatus = preferences.getTutorialStatus(this);
                    Log.d("TAGTutorial", tutorialStatus+"");

                    if(tutorialStatus==0) {
                        intent = new Intent(this, TutorialActivity.class);
                    }
                    else{
                        intent = new Intent(this, BoardPlayMultiplayerActivity.class);
                    }
                    startActivity(intent);
                }
                break;
            case R.id.btn_primitive_game:
                intent = new Intent(this, DecidePlayOptionsNormalActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_profile:
                showProfileDialog();
                break;

            case R.id.btn_options:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_instructions:
                intent = new Intent(this, InstructionActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }



    }

    private void showProfileDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View theView = inflater.inflate(R.layout.dialog_profile, null);

        final EditText etNickname = theView.findViewById(R.id.et_nickname);
        TextView tvNickname = theView.findViewById(R.id.tv_nickname);
        TextView tvRank = theView.findViewById(R.id.tv_rank);
        TextView tvWin = theView.findViewById(R.id.tv_win);
        TextView tvDraw = theView.findViewById(R.id.tv_draw);
        TextView tvLoss = theView.findViewById(R.id.tv_loss);

//        etNickname.setClickable(true);

        etNickname.setVisibility(View.VISIBLE);
        tvNickname.setVisibility(View.GONE);

        MyPreferences myPreferences = new MyPreferences();
        etNickname.setText(myPreferences.getNickname(this));
        tvRank.setText(myPreferences.getLeaderboardScore(this));
        tvWin.setText(myPreferences.getUserWin(this));
        tvDraw.setText(myPreferences.getUserDraw(this));
        tvLoss.setText(myPreferences.getUserLoss(this));

        builder.setView(theView);
        builder.setView(theView)
                .setPositiveButton("Close",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = String.valueOf(etNickname.getText());
                        MyPreferences myPreferences = new MyPreferences();
                        myPreferences.saveNicknamePreference(StartActivity.this, nickname);
                    }
                });

        android.support.v7.app.AlertDialog profileDialog;
        profileDialog = builder.create();
        profileDialog.show();
    }


    @Override
    protected void onResume() {
        lazyAds.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        lazyAds.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //lazyAds.onDestroy();
        lazyAds = null;
        if(LogUtil.islogOn()){
            Log.d(StartActivity.class.getSimpleName(),"onDestroy()");
        }
        super.onDestroy();

    }

    void showNetworkError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.no_network_error))
                .setNeutralButton(android.R.string.ok, null).create().show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        }
        catch (Exception e) {
            return true;
        }
    }

    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("TAGNetwork", "Error checking internet connection", e);
            }
        } else {
            Log.d("TAGNetwork", "No network available!");
        }
        return false;
    }
}
