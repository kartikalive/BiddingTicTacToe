package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button quickPlayBtn, settingsBtn, exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        quickPlayBtn = (Button) findViewById(R.id.quick_play_btn);
        settingsBtn = (Button) findViewById(R.id.settings_btn);
        exitBtn = (Button) findViewById(R.id.exit_btn);
    }

    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.quick_play_btn:
                if(SettingsActivity.animatedPlay==0){
                    intent = new Intent(this, BoardPlayNormalActivity.class);
                }
                else {
                    intent = new Intent(this, DrawingPlayNormalActivity.class);
                }
                break;
            case R.id.settings_btn:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.exit_btn:
                this.finishAffinity();
                break;
            default:
                break;
        }

        startActivity(intent);
    }
}
