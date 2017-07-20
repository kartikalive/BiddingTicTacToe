package com.mindsortlabs.biddingtictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);



    }

    public void onClose(View view) {

        finish();

    }
}
