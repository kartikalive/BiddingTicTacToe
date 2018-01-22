package com.mindsortlabs.biddingtictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_instruction);
        MyPreferences preferences = new MyPreferences();
        int status = preferences.getTutorialStatus(this);
        if(status==0) {
            preferences.saveTutorialStatus(this, 1);
        }
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClose(View view) {
        finish();
    }
}