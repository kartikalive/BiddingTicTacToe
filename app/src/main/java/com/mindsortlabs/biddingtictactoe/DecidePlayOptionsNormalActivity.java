package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.mindsortlabs.biddingtictactoe.custom.BoardChooser;

public class DecidePlayOptionsNormalActivity extends AppCompatActivity {

    ImageButton btnCPU, btnTwoPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_decide_play_options_normal);

        btnCPU = (ImageButton) findViewById(R.id.btn_practice);
        btnTwoPlayers = (ImageButton) findViewById(R.id.btn_two_players);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    private void hideStatusBar() {
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClick(View v){

        Intent intent = null;

        switch (v.getId()){
            case R.id.btn_practice:
                if(SettingsActivity.animatedPlay==0){
                    intent = new Intent(this, BoardPlayCPUNormalActivity.class);
                }
                else{
                    intent = new Intent(this, BoardPlayCPUNormalActivity.class);
                }
                break;
            case R.id.btn_two_players:
                if(SettingsActivity.animatedPlay==0) {
                    intent = new Intent(this, BoardChooser.class);
                }
                else{
                    intent = new Intent(this, DrawingPlayCPUNormalActivity.class);
                }
                break;
            default:
                break;

        }

        startActivity(intent);
        finish();
    }

}
