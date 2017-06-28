package com.mindsortlabs.biddingtictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;

public class BoardPlayCPUNormalActivity extends AppCompatActivity {

    GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_play_cpu_normal);

        gridLayout = (GridLayout)findViewById(R.id.gridLayout);
    }

    //write your code in this class.
}
