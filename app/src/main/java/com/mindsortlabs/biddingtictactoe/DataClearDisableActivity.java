package com.mindsortlabs.biddingtictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DataClearDisableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_clear_disable);

        finish();
    }
}
