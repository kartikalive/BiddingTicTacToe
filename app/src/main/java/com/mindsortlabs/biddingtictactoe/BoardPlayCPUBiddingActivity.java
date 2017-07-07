package com.mindsortlabs.biddingtictactoe;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mindsortlabs.biddingtictactoe.ai.BiddingTicTacToeAi;

import java.util.Vector;

public class BoardPlayCPUBiddingActivity extends AppCompatActivity {

    TextView tvBid1, tvBid2, tvBidTime, tvTotal1, tvTotal2, tvPlayerTitle;
    Button btnSetBid;
    ImageView counter ,winLine;
    GridLayout gridLayout;

    RadioGroup radioGroupSymbol;
    RadioButton radioBtnCross, radioBtnCircle;
    LinearLayout layoutPlayer1,layoutPlayer2;
//    char player1Symbol = 'X';
    int activePlayer = 0;  //Player = 0, Computer = 1
    CountDownTimer moveTimer;
    boolean gameActive = false;
    boolean gameStarted = false;
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int line = 0;

    Vector<String> board;
    char userSymbol = 'X';
    Pair<Integer, Pair<Integer, Integer>> cpuTurnPair;

    int bid1 = 1, bid2 = 1;
    int total1 = 100, total2 = 100;
    boolean updatedBid1 = false, updatedBid2 = false;   // put it to zero again when player moves.
    int[][] winningPositions = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}};

    BiddingTicTacToeAi biddingAiObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_play_cpubidding);
//        Log.d("TAG123","onCreate: ");
        tvBid1 = (TextView) findViewById(R.id.tv_bid1);
        tvBid2 = (TextView) findViewById(R.id.tv_bid2);
        tvBidTime = (TextView) findViewById(R.id.tv_bid_time);
        tvTotal1 = (TextView) findViewById(R.id.tv_total1);
        tvTotal2 = (TextView) findViewById(R.id.tv_total2);
        tvPlayerTitle = (TextView) findViewById(R.id.tv_player_title);

        gridLayout = (GridLayout)findViewById(R.id.gridLayout);
        winLine = (ImageView) findViewById(R.id.win_line);

        radioBtnCross = (RadioButton) findViewById(R.id.radiobtn_cross);
        radioBtnCircle = (RadioButton) findViewById(R.id.radiobtn_circle);

        layoutPlayer1 = (LinearLayout) findViewById(R.id.layout_player1);
        layoutPlayer2 = (LinearLayout) findViewById(R.id.layout_player2);

        layoutPlayer1.getBackground().setAlpha(40);
        layoutPlayer2.getBackground().setAlpha(40);

        radioBtnCross.setChecked(true);

        biddingAiObj = new BiddingTicTacToeAi();
        board = new Vector<>();
        board.add("___");
        board.add("___");
        board.add("___");

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                char cpuSymbol = (char) ('X' + 'O' - userSymbol);   //check if any error due to predefining.
//                cpuTurnPair = biddingAiObj.getSolution(board,total2,cpuSymbol);
//                bid2 = cpuTurnPair.first;
//                Log.d("TAG124","bid1: "+ bid1 + "  bid2: "+ bid2);
//                updatedBid2 = true;
//                //callTimer(updatedBid1, updatedBid2);
//                Log.d("TAG124","Let's start.");
////                Toast.makeText(getApplicationContext(), "Lets start.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        thread.start();

        char cpuSymbol = (char) ('X' + 'O' - userSymbol);
        Pair<Integer,Integer> temp = new Pair<>(1,1);
        cpuTurnPair = new Pair<>(32,temp);

        bid2 = cpuTurnPair.first;
        updatedBid2 = true;
        //Set bid prompt.

        moveTimer = new CountDownTimer(3300,1000) {
            @Override
            public void onTick(long l) {

                int time = (int) (l/1000);
                Log.d("TAG123",(time)+"");
                tvBidTime.setAlpha(1f);
                if(time>1) {
                    tvBidTime.setText(String.valueOf(time));
                }
                else{
                    tvBidTime.setText("Play");
                }

                tvBidTime.animate().alpha(0).setDuration(900);
            }

            @Override
            public void onFinish() {
                gameActive = true;
                tvBid1.animate().alpha(0).setDuration(200);
                tvBid2.animate().alpha(0).setDuration(200);
                if(bid1!=bid2) {
                    tvTotal1.animate().alpha(0).setDuration(200);
                    tvTotal2.animate().alpha(0).setDuration(200);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvBid1 = (TextView) findViewById(R.id.tv_bid1);
                        tvBid2 = (TextView) findViewById(R.id.tv_bid2);

                        tvBid1.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                        tvBid2.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                        tvBid1.setText(String.valueOf(bid1));
                        tvBid2.setText(String.valueOf(bid2));
                        tvBid1.animate().alpha(1f).setDuration(200);
                        tvBid2.animate().alpha(1f).setDuration(200);
                        if(bid1==bid2){
                            gameActive = false;
                            Toast.makeText(BoardPlayCPUBiddingActivity.this, "What a coincidence! Please choose again",
                                    Toast.LENGTH_SHORT).show();
                            updatedBid1 = false;
                            updatedBid2 = false;
                        }
                        else if(bid1>bid2){
                            Toast.makeText(BoardPlayCPUBiddingActivity.this, "Player turn", Toast.LENGTH_SHORT).show();
                            total1 = total1 - bid1;
                            total2 = total2 + bid1;
                            tvTotal1.setText(String.valueOf(total1));
                            tvTotal2.setText(String.valueOf(total2));
                            activePlayer = 0;
                            tvTotal1.animate().alpha(1f).setDuration(200);
                            tvTotal2.animate().alpha(1f).setDuration(200);
                            tvBid1.setClickable(false);
                        }
                        else{
                            Toast.makeText(BoardPlayCPUBiddingActivity.this, "Computer turn", Toast.LENGTH_SHORT).show();
                            total1 = total1 + bid2;
                            total2 = total2 - bid2;
                            tvTotal1.setText(String.valueOf(total1));
                            tvTotal2.setText(String.valueOf(total2));
                            activePlayer = 1;
                            tvTotal1.animate().alpha(1f).setDuration(200);
                            tvTotal2.animate().alpha(1f).setDuration(200);
                            tvBid1.setClickable(false);
                            Log.d("checkBeforePlay: ", "bid2: " + bid2 + "  row: " + cpuTurnPair.second.first + " col: " +
                                    cpuTurnPair.second.second);
                            computerPlaying();
                        }
                    }
                },200);

                tvBidTime.setVisibility(View.GONE);
                //hide/display Options
            }
        };

        radioGroupSymbol = (RadioGroup) findViewById(R.id.radiogroup_symbol);
        radioGroupSymbol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radiobtn_cross){
//                    player1Symbol = 'X';
                    userSymbol = 'X';
                    layoutPlayer1.animate().alpha(0).setDuration(200);
                    layoutPlayer2.animate().alpha(0).setDuration(200);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutPlayer1.setBackgroundResource(R.drawable.lightcross);
                            layoutPlayer2.setBackgroundResource(R.drawable.lightcircle);
                            layoutPlayer1.animate().alpha(1f).setDuration(200);
                            layoutPlayer2.animate().alpha(1f).setDuration(200);
                        }
                    },200);
                }

                if(i==R.id.radiobtn_circle){
//                    player1Symbol = 'O';
                    userSymbol = 'O';
                    layoutPlayer1.animate().alpha(0).setDuration(200);
                    layoutPlayer2.animate().alpha(0).setDuration(200);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutPlayer1.setBackgroundResource(R.drawable.lightcircle);
                            layoutPlayer2.setBackgroundResource(R.drawable.lightcross);
                            layoutPlayer1.animate().alpha(1f).setDuration(200);
                            layoutPlayer2.animate().alpha(1f).setDuration(200);
                        }
                    },200);
                }
            }
        });

        tvBid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTimer.cancel();
                tvBidTime.setVisibility(View.GONE);
                setBid(1);
                setBid(2);
            }
        });

    }

    private void computerPlaying() {

        char compSymbol = (char) ('X' + 'O' - userSymbol);
        int symbol = R.drawable.circle;
        if (userSymbol=='O') {
            symbol = R.drawable.cross;
        }
        final ImageView counter;

        int compRow = cpuTurnPair.second.first;
        int compCol = cpuTurnPair.second.second;
        int tag = compRow*3 + compCol;
        Log.d("TAG124","row: "+ compRow + " col: "+compCol + " tag: " + tag);
//        String string = "imageView"+tag;
        counter = (ImageView) findViewById(R.id.activity_board_play_cpubidding).findViewWithTag(tag+"");
        counter.setImageResource(symbol);

        final int tappedCounter = tag;

        setInitialPositions(counter, tappedCounter);
        gameState[tappedCounter] = activePlayer;
        board = updateBoardConfig(board, tappedCounter, activePlayer, userSymbol);
        Log.d("boardConfig: ", board.get(0)+"\n"+board.get(1)+"\n"+board.get(2));

        final int[] flag = {0};
        Handler handler0 = new Handler();
        handler0.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!checkWinner()) {
                    flag[0] = 1;
                }
            }
        },1000);


        updatedBid1 = false;
        updatedBid2 = false;

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateCounters(counter, tappedCounter);
                gameActive = false;
            }
        },1000);



        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvBid1.animate().alpha(0).setDuration(200);
                tvBid2.animate().alpha(0).setDuration(200);
            }
        }, 1800);


        Handler handler3 = new Handler();
        final int finalFlag = flag[0];
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(finalFlag ==1){
                    Toast.makeText(BoardPlayCPUBiddingActivity.this, "Time to Bid", Toast.LENGTH_SHORT).show();
                }
                tvBid1.setClickable(true);
                tvBid1 = (TextView) findViewById(R.id.tv_bid1);
                tvBid2 = (TextView) findViewById(R.id.tv_bid2);
                tvBid1.setText("00");
                tvBid1.animate().alpha(1f).setDuration(200);
                tvBid2.setText("00");
                tvBid2.animate().alpha(1f).setDuration(200);
            }
        }, 2000);

        //getting new Solution
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                char cpuSymbol = (char) ('X' + 'O' - userSymbol);   //check if any error due to predefining.
                Log.d("checkBeforeCall", " board : \n"+board.get(0)+"\n"+board.get(1)+"\n"+board.get(2)+ "\n" + "total2 : "
                        + total2 + " cpuSymbol : " + cpuSymbol);
                cpuTurnPair = biddingAiObj.getSolution(board,total2,cpuSymbol);
                Log.d("checkBeforePlay2: ", "bid2: " + bid2 + "  row: " + cpuTurnPair.second.first + " col: " +
                        cpuTurnPair.second.second);
                bid2 = cpuTurnPair.first;
                Log.d("TAG124","bid1: "+ bid1 + "  bid2: "+ bid2);
                updatedBid2 = true;
                //callTimer(updatedBid1, updatedBid2);
            }
        });

        thread.start();

    }


    public void dropIn(View view){  //set in XML

        counter = (ImageView) view;
        int tappedCounter = Integer.parseInt(counter.getTag().toString());

        if(gameActive){
            if(activePlayer==0) {
                if (!gameStarted) {
                    gameStarted = true;
                    radioGroupSymbol.setClickable(false);
                    displayOptions(false);
                }

                if (gameState[tappedCounter] != 2) {
                    Toast.makeText(this, "Play somewhere else", Toast.LENGTH_SHORT).show();
                } else {

                    board = updateBoardConfig(board, tappedCounter, activePlayer, userSymbol);
                    Log.d("boardConfig: ", board.get(0)+"\n"+board.get(1)+"\n"+board.get(2));

                    //getting new computer solution
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            char cpuSymbol = (char) ('X' + 'O' - userSymbol);   //check if any error due to predefining.
                            Log.d("checkBeforeCall", " board : \n"+board.get(0)+"\n"+board.get(1)+"\n"+board.get(2)+ "\n" + "total2 : "
                                    + total2 + " cpuSymbol : " + cpuSymbol);
                            cpuTurnPair = biddingAiObj.getSolution(board,total2,cpuSymbol);
                            Log.d("checkBeforePlay2: ", "bid2: " + bid2 + "  row: " + cpuTurnPair.second.first + " col: " +
                                    cpuTurnPair.second.second);
                            bid2 = cpuTurnPair.first;
                            Log.d("TAG124","bid1: "+ bid1 + "  bid2: "+ bid2);
                            updatedBid2 = true;
                            //callTimer(updatedBid1, updatedBid2);
                        }
                    });

                    thread.start();


                    int symbol = R.drawable.cross;
                    if (userSymbol=='O') {
                        symbol = R.drawable.circle;
                    }

                    counter.setImageResource(symbol);

                    setInitialPositions(counter, tappedCounter);
                    gameState[tappedCounter] = activePlayer;
                    animateCounters(counter, tappedCounter);

                    if (!checkWinner()) {
                        Toast.makeText(this, "Time to Bid", Toast.LENGTH_SHORT).show();
                    }

                    tvBid1.setClickable(true);
                    updatedBid1 = false;
                    updatedBid2 = false;
                    tvBid1.animate().alpha(0).setDuration(200);
                    tvBid2.animate().alpha(0).setDuration(200);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvBid1 = (TextView) findViewById(R.id.tv_bid1);
                            tvBid2 = (TextView) findViewById(R.id.tv_bid2);
                            tvBid1.setText("00");
                            tvBid1.animate().alpha(1f).setDuration(200);
                            tvBid2.setText("00");
                            tvBid2.animate().alpha(1f).setDuration(200);
                        }
                    }, 200);
                }

                gameActive = false;
            }
            else{
                Toast.makeText(this, "Computer's turn. Please wait.", Toast.LENGTH_SHORT).show();
            }
        }

        else{
            Toast.makeText(this, "Select Bid First", Toast.LENGTH_SHORT).show();
        }


    }

    private Vector<String> updateBoardConfig(Vector<String> oldBoard, int tappedCounter, int activePlayer, char userSymbol) {
        Vector<String> newBoard = oldBoard;

        int row = tappedCounter/3;
        int col = tappedCounter%3;
        char symbol;

        symbol = 'X';
        if(activePlayer==0&&userSymbol=='O'||activePlayer==1&&userSymbol=='X'){
            symbol = 'O';
        }

        String oldStr = oldBoard.elementAt(row);
        StringBuilder strBuilder = new StringBuilder(oldStr);
        strBuilder.setCharAt(col, symbol);
        String newStr = String.valueOf(strBuilder);
        newBoard.set(row, newStr);

        return newBoard;

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DecidePlayOptionsBiddingActivity.class);
        startActivity(intent);
    }

    private void displayOptions(boolean display) {
        if(display) {
            radioGroupSymbol.animate().translationYBy(-1000f).setDuration(500);
            tvPlayerTitle.animate().translationYBy(-1000f).setDuration(500);
        }
        else{
            radioGroupSymbol.animate().translationY(1000f).setDuration(500);
            tvPlayerTitle.animate().translationYBy(1000f).setDuration(500);
        }


    }

    private boolean checkWinner() {
        int i = 0;
        for (int[] winningPosition : winningPositions) {

            if (gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                    gameState[winningPosition[0]] != 2) {

                // Someone has won!

                gameActive = false;

                String winner = "Computer";

                if (gameState[winningPosition[0]] == 0) {

                    winner = "Player";

                }
                Toast.makeText(this, winner + " wins.", Toast.LENGTH_SHORT).show();


                final int winnerPlayer = gameState[winningPosition[0]];

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        gameOverMessage(winnerPlayer);
                    }
                }, 500);
                declareWinner(winningPosition, winner, i);
                return true;

            } else {

                i++;

                boolean gameIsOver = true;

                for (int counterState : gameState) {

                    if (counterState == 2) gameIsOver = false;

                }

                if (gameIsOver) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gameOverMessage(2);
                        }
                    }, 500);

                }

            }

        }
        return false;
    }

    public void playAgain(View view) {

        Intent intent = new Intent(this, BoardPlayCPUBiddingActivity.class);
        startActivity(intent);
    }

    private void gameOverMessage(int i) {

        LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);
        TextView winnerMessage = (TextView) findViewById(R.id.winnerMessage);
        layout.setVisibility(View.VISIBLE);
        layout.setAlpha(0);

        winnerMessage.setText("Player wins");

        if(i==1){
            winnerMessage.setText("Computer wins");
        }
        else if(i==2) {
            winnerMessage.setText("It's a draw");
        }

        layout.animate().alpha(1).setDuration(300);
    }

    private void declareWinner(int[] winningPosition, String winner, int i) {

        winLine.setVisibility(View.VISIBLE);
        winLine.setScaleX(0f);

        line = 0;

        if(i<=2){
            line = 1;
            if(winningPosition[0]==0){
                line  = 0;
                winLine.setTranslationY(-(gridLayout.getWidth() / 3));
            }
            else if(winningPosition[0]==6){
                line  = 2;
                winLine.setTranslationY((gridLayout.getWidth() / 3));
            }
            winLine.setScaleX(0f);
            winLine.animate().scaleX(1f).setDuration(200);
        }

        else if(i<=5){
            line = 4;
            winLine.setRotation(90);

            if(winningPosition[0]==0){
                line = 3;
                winLine.setTranslationX(-(gridLayout.getWidth() / 3));
            }
            else if(winningPosition[0]==2){
                line = 5;
                winLine.setTranslationX((gridLayout.getWidth() / 3));
            }
            winLine.setScaleX(0f);
            winLine.animate().scaleX(1f).setDuration(200);

        }

        else{

            if(winningPosition[0]==0){
                line = 6;
                winLine.setRotation(45);
            }
            else if(winningPosition[0]==2){
                line = 7;
                winLine.setRotation(-45);
            }

            winLine.animate().scaleX(1.2f).setDuration(200);
        }

//        try {
//
//            wait(200);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        mergeImages(line);
    }

    private void mergeImages(int line) {

    }

    private void animateCounters(ImageView counter, int tappedCounter) {

        switch (tappedCounter){
            case 0:
                counter.animate().translationYBy(1000f).translationXBy(1000f).rotation(360).setDuration(300);
                break;
            case 1:
                counter.animate().translationYBy(1000f).rotation(360).setDuration(300);
                break;
            case 2:
                counter.animate().translationYBy(1000f).translationXBy(-1000f).rotation(360).setDuration(300);
                break;
            case 3:
                counter.animate().translationXBy(1000f).rotation(360).setDuration(300);
                break;
            case 4:
                counter.animate().scaleX(1f).scaleY(1f).rotation(360).setDuration(300);
                break;
            case 5:
                counter.animate().translationXBy(-1000f).rotation(360).setDuration(300);
                break;
            case 6:
                counter.animate().translationYBy(-1000f).translationXBy(1000f).rotation(360).setDuration(300);
                break;
            case 7:
                counter.animate().translationYBy(-1000f).rotation(360).setDuration(300);
                break;
            case 8:
                counter.animate().translationYBy(-1000f).translationXBy(-1000f).rotation(360).setDuration(300);
                break;
        }

    }

    private void setInitialPositions(ImageView counter, int tappedCounter) {

        if(tappedCounter==0||tappedCounter==1||tappedCounter==2) {
            counter.setTranslationY(-1000f);
        }
        if(tappedCounter==0||tappedCounter==3||tappedCounter==6){
            counter.setTranslationX(-1000f);
        }
        if(tappedCounter==2||tappedCounter==5||tappedCounter==8){
            counter.setTranslationX(1000f);
        }
        if(tappedCounter==6||tappedCounter==7||tappedCounter==8){
            counter.setTranslationY(1000f);
        }
        if(tappedCounter==4){
            counter.setScaleX(0.1f);
            counter.setScaleY(0.1f);
        }

    }

    public void setBid(final int bidNumber){

        if(bidNumber==1) {
            final Dialog d = new Dialog(this);
            d.setTitle("NumberPicker");
            d.setContentView(R.layout.dialog_number_picker);
            btnSetBid = (Button) d.findViewById(R.id.btn_set_bid);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.num_picker);
            np.setMaxValue(total1); // max value 100
            np.setMinValue(1);   // min value 0
            np.setValue(bid1);
//        if(bidNumber==2) {
//            np.setMaxValue(total2);
//            np.setValue(bid2);
//        }
            np.setWrapSelectorWheel(true);

//        final int[] bid = new int[1];
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    //numPickerBid1.setValue(i);
                }
            });

            btnSetBid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    tvBid1 = (TextView) findViewById(R.id.tv_bid1);

                    if (bidNumber == 1) {
                        tvBid1.setText("Hidden");
                        tvBid1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                        bid1 = np.getValue();
                        updatedBid1 = true;
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!gameStarted) {
                                gameStarted = true;
                                radioGroupSymbol.setClickable(false);
                                displayOptions(false);
                            }

                            tvBid2.setText("Hidden");
                            tvBid2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
//                    updatedBid2 = true;
                            callTimer(updatedBid1);
                        }
                    },1000);

//                    else {
//                        tvBid2.setText("Hidden");
//                        tvBid2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
//                        bid2 = np.getValue();
//                        updatedBid2 = true;
//                    }
//                Log.d("TAG124","bid1: "+ bid1 + "  bid2: "+ bid2);
                    d.dismiss();
                }
            });

            d.show();
        }
    }

    private void callTimer(boolean updatedBid1) {

        if(updatedBid1){
            Log.d("TAG124","Calling Timer: ");
            tvBidTime.setVisibility(View.VISIBLE);
            moveTimer.start();
        }
    }
}

