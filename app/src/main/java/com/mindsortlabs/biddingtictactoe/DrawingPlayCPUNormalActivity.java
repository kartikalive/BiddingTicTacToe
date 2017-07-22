package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DrawingPlayCPUNormalActivity extends AppCompatActivity {

    DrawingViewNormal dv ;
//    Bundle previousState;

    SoundPool turnSound, winSound , drawSound;
    boolean turnSoundLoaded = false, winSoundLoaded = false, drawSoundLoaded;
    int turnSoundId, winSoundId, drawSoundId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
//        previousState = savedInstanceState;
        setContentView(R.layout.activity_drawing_play_normal);

        loadSound();

        turnSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                turnSoundLoaded = true;
//                playSound(1);
            }
        });

        winSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                winSoundLoaded = true;
            }
        });

        drawSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                drawSoundLoaded = true;
//                playSound(1);
            }
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = (int) (1.09*width);

                dv = new DrawingViewNormal(this, this);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dv.setForegroundGravity(Gravity.CENTER_VERTICAL);
            dv.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        }

        // Rect rect = new Rect(1000,1000,1000,1000);
        //dv.setClipBounds(rect);
        addContentView(dv, layoutParams);
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        releaseSound();
        Intent intent = new Intent(this, DecidePlayOptionsNormalActivity.class);
        startActivity(intent);
        finish();
    }

    private void releaseSound() {
        if(turnSound!=null){
            turnSound.release();
        }
        if(winSound!=null){
            winSound.release();
        }
        if(drawSound!=null){
            drawSound.release();
        }

    }

    public void onClick(View v){

        releaseSound();
        ((ViewManager)dv.getParent()).removeView(dv);
//        RelativeLayout parentLyt = (RelativeLayout) findViewById(R.id.activity_drawing_play_normal);
//        parentLyt.removeView(dv);
        dv = new DrawingViewNormal(this, this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int height = (int) (1.09*width);

        dv = new DrawingViewNormal(this, this);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dv.setForegroundGravity(Gravity.CENTER_VERTICAL);
            dv.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        }

        // Rect rect = new Rect(1000,1000,1000,1000);
        //dv.setClipBounds(rect);
        addContentView(dv, layoutParams);
//        Intent intent = new Intent(this, DrawingPlayCPUNormalActivity.class);
//        startActivity(intent);

    }


    //Required function ----------------------------------------------------------

    public void userTurn(int userRow, int userCol, int userTurn){

        SoundActivity.playSound(this,turnSound,turnSoundLoaded,turnSoundId);
        // if turn = 0, symbol = O, if turn = 1, symbol = X
        Log.d("TAG4","User turn =  row = "+userRow + "  col = "+userCol + " turn: " + userTurn);
//        Toast.makeText(this, "User turn = row = " + userRow + " col = "+userCol + " turn :  "+ userTurn, Toast.LENGTH_SHORT).show();



        //After deciding what CPU should move, return it to drawing view.
        //Example :  compRow = 1, compcol = 3 and compTurn = 1-turn
        int compRow = 0;             // to be calculated
        int compCol = 0;             // to be calculated
        int compTurn = 1-userTurn;   // not true in case of BTTT

        dv.compTurn(compRow,compCol,compTurn);   //for BTTT, use 4th parameter to tell UI class that whose turn is it - user or comp.
    }

    private void loadSound() {
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            turnSound = new SoundPool.Builder().build();
            winSound = new SoundPool.Builder().build();
            drawSound = new SoundPool.Builder().build();
        }

        else {
            turnSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            winSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            drawSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        turnSoundId = turnSound.load(this, R.raw.sound1, 1);
        winSoundId = winSound.load(this, R.raw.sound2, 2);
        drawSoundId = drawSound.load(this, R.raw.sound2, 2);
    }
}
