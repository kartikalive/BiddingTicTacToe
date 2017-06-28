package com.mindsortlabs.biddingtictactoe;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DrawingPlayNormalActivity extends AppCompatActivity {

    DrawingViewNormal dv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_play_normal);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        dv = new DrawingViewNormal(this, this);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dv.setForegroundGravity(Gravity.CENTER_VERTICAL);
            dv.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        }

        // Rect rect = new Rect(1000,1000,1000,1000);
        //dv.setClipBounds(rect);
        addContentView(dv, layoutParams);
    }

    public void onClickBtn(View v){

    }


    //Required function ----------------------------------------------------------

    public void userTurn(int userRow, int userCol, int userTurn){
        // if turn = 0, symbol = O, if turn = 1, symbol = X
        Log.d("TAG4","User turn =  row = "+userRow + "  col = "+userCol + " turn: " + userTurn);
        Toast.makeText(this, "User turn = row = " + userRow + " col = "+userCol + " turn :  "+ userTurn, Toast.LENGTH_SHORT).show();



        //After deciding what CPU should move, return it to drawing view.
        //Example :  compRow = 1, compcol = 3 and compTurn = 1-turn
        int compRow = 0;             // to be calculated
        int compCol = 0;             // to be calculated
        int compTurn = 1-userTurn;   // not true in case of BTTT

        dv.compTurn(compRow,compCol,compTurn);   //for BTTT, use 4th parameter to tell UI class that whose turn is it - user or comp.
    }
}
