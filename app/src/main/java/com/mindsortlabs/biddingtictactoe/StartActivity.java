package com.mindsortlabs.biddingtictactoe;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mindsortlabs.biddingtictactoe.ads.LazyAds;
import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class StartActivity extends AppCompatActivity {

    ImageButton biddingGameBtn, primitiveGameBtn, settingsBtn, instructionsBtn, exitBtn, multiplayerBtn;
    NormalTicTacAi obj;
    int backPressed = 0;
    LazyAds lazyAds;
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
        lazyAds = LazyAds.getInstance(this);

//        hideStatusBar();
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.prefKey, Context.MODE_PRIVATE);

        if (prefs.getBoolean(SettingsActivity.soundPrefAccessKey, false)) {
            SettingsActivity.soundEffects = 1;
        }
        if (prefs.getBoolean(SettingsActivity.animatePrefAccessKey, false)) {
            SettingsActivity.animatedPlay = 1;
        }
//        SettingsActivity.animatedPlay =
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

   /* @Override
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
    }*/

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.btn_bidding_game:
//                Toast.makeText(this, "Opening Anything for now.", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, DecidePlayOptionsBiddingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_multiplayer_game:
                if(!isNetworkAvailable()){
                    showNetworkError();
                }
                else {
                    intent = new Intent(this, BoardPlayMultiplayerActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_primitive_game:
                intent = new Intent(this, DecidePlayOptionsNormalActivity.class);
                startActivity(intent);
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

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Want To Quit ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
//                        hideStatusBar();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
//                hideStatusBar();
            }
        });
        builder.show();
//        hideStatusBar();
    }


    @Override
    protected void onResume() {
        lazyAds.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        lazyAds.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        lazyAds.onDestroy(this);
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
