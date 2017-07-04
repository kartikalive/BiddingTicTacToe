package com.mindsortlabs.biddingtictactoe.custom;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mindsortlabs.biddingtictactoe.BoardPlay2PlayerBiddingActivity;
import com.mindsortlabs.biddingtictactoe.R;

public class TwoPlayerCustom extends AppCompatActivity {

    LinearLayout layoutPlayer1 , layoutPlayer2 ;


    int playerTurn = 1;
    int board_size ;
    int objectives ;

    CountDownTimer moveTimer;
    CountDownTimer moveTimer2 ;

    int player1Image = 1 ;// 1--> Cross
    int player2Image = 2 ;// 2--> Circle

    TextView tvView ;
    TextView display ;
    GridView gridview ;

    int player1Score = 0 ;
    int player2Score = 0 ;


    int gameFinished = 1 ;//NOt finished


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_custom);

        Intent intent = getIntent();
        board_size = intent.getIntExtra("board_sizes",3);
        objectives  = intent.getIntExtra("objectives",3);



        gridview = (GridView) findViewById(R.id.gridView);

        tvView =(TextView)findViewById(R.id.tv_View);
        display = (TextView)findViewById(R.id.display);

        layoutPlayer1 = (LinearLayout) findViewById(R.id.Player1);
        layoutPlayer2 = (LinearLayout) findViewById(R.id.Player2);

        layoutPlayer1.getBackground().setAlpha(40);
        layoutPlayer2.getBackground().setAlpha(40);


        gridview.setNumColumns(board_size);

        gridview.setAdapter(new ImageAdapter(this,board_size));

        gridview.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }

        });


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
               // Toast.makeText(TwoPlayerCustom.this, "" + position,
                //        Toast.LENGTH_SHORT).show();

                moveTimer.cancel();
                Pair pos = getPositionPair(position);

                //Replace with new Image according to player
                ImageView img = (ImageView)v;
                setImage(img);

                if ( checkForSolution(playerTurn,pos) ){
                    // This player Wins
                    increamentWins(playerTurn);
                    UpdateLayout();
                    //restartGame();

                }else{

                    // Update for Second player Turn


                }


                if( !isgameFinished() ){
                    changePlayerTurn();
                    changeTextForPlayerTurn();
                }

            }
        });

    moveTimer = new CountDownTimer(3300, 1000) {
        @Override
        public void onTick(long l) {

            int time = (int) (l / 1000);
            Log.d("TAG123", (time) + "");
            tvView.setAlpha(1f);
            if (time > 1) {
                tvView.setText(String.valueOf(time));
            } else {
                tvView.setText("Play");
            }

            tvView.animate().alpha(0).setDuration(900);
        }

        @Override
        public void onFinish() {
            tvView.setVisibility(View.GONE);
            //hide/display Options
        }
    };
        tvView.setVisibility(View.VISIBLE);
        moveTimer.start();
}

    private void UpdateLayout() {


    }

    private void increamentWins(int playerTurn) {
        if(playerTurn==1)
            player1Score++;
        else
            player2Score++;
    }

    private boolean checkForSolution(int playerTurn, Pair pos) {
        int counter = objectives ;
        return checkForSolutionHelper(playerTurn,pos,counter);

    }

    int dx[] ={ 0, 0,-1,-1,-1,1, 1, 1};
    int dy[] ={ -1,1,-1, 0, 1,-1,0, 1};




    private boolean checkForSolutionHelper(int playerTurn, Pair pos, int counter) {

        if(counter==0)
            return true;

        int row_i = (int) pos.first, col_j = (int) pos.second;

        boolean flag =false ;

        for(int i=0; i<dx.length ; i++){
            int x= row_i+dx[i] , y =col_j+dy[i] ;
            if( inRange(x,y) ){

                ImageView img = (ImageView) gridview.getChildAt(getPosition(x,y));
                if ( img!=null&& (int)img.getTag() ==  getPLayerTurnId() ){
                    boolean temp = checkForSolutionHelper(playerTurn,Pair.create(x,y),counter-1);
                    if(temp==true) {
                        img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        flag = flag | temp;
                    }
                }
            }

        }

        return flag;
    }

    private boolean inRange(int x, int y) {
        if(x<board_size&&y<board_size)
            return true;
        return false;
    }

    private boolean isgameFinished() {
        return gameFinished==0 ;
    }

    private void changeTextForPlayerTurn() {
        display.setText("Player" + Integer.toString(playerTurn) + "  Turn");
    }

    private void changePlayerTurn() {
        if(playerTurn==1)
            playerTurn =2 ;
        else
            playerTurn =1 ;
    }

    public int getPlayer1Image() {
        if(player1Image == 1)
            return R.drawable.cross;
        else
            return R.drawable.circle;
    }

    public int getPlayer2Image() {
        if(player2Image == 1)
            return R.drawable.cross;
        else
            return R.drawable.circle;
    }


    private void setImage(ImageView img) {


        if( img.getTag().equals(R.drawable.back) ){

            if(playerTurn == 1 ){
                img.setImageResource(getPlayer1Image());
                img.setTag(getPlayer1Image());
            }else {
                img.setImageResource(getPlayer2Image());
                img.setTag(getPlayer2Image());
            }
        }
    }

    int getPLayerTurnId()
    {
        if(playerTurn == 1 ){
            return getPlayer1Image();
        }else {
            return getPlayer2Image();
        }
    }

    Pair<Integer,Integer> getPositionPair(int position){
        int row_i = position / board_size ;
        int col_j = position % board_size ;

        return Pair.create(row_i,col_j);
    }

    int getPosition(int x,int y){

        return x * board_size + y ;

    }




}
