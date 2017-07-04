package com.mindsortlabs.biddingtictactoe.custom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.mindsortlabs.biddingtictactoe.DecideGameActivity;
import com.mindsortlabs.biddingtictactoe.R;

public class BoardChooser extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner objective ;
    Spinner spinner ;

    int board_sizes = 0;
    int objectives = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_chooser);

        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.board_size, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);



        objective = (Spinner) findViewById(R.id.objective);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.objective, android.R.layout.simple_spinner_item);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        objective.setAdapter(adapter2);


        spinner.setOnItemSelectedListener(this);
        objective.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int x = parent.getId() ;

        if(parent.getId()==R.id.spinner){
            board_sizes = position + 3 ;
        }else if(parent.getId()==R.id.objective){
            objectives =position + 3 ;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void customPlay(View view) {

        Intent intent = new Intent(this, TwoPlayerCustom.class);
        intent.putExtra("board_sizes",board_sizes);
        intent.putExtra("objectives",objectives);
        startActivity(intent);
    }
}
