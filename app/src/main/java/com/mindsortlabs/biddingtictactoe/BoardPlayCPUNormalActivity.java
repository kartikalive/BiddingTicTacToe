package com.mindsortlabs.biddingtictactoe;

import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;

import java.util.Vector;

public class BoardPlayCPUNormalActivity extends AppCompatActivity {


    // 0 = Circle, 1 = Cross

    int activePlayer = 1;

    boolean gameIsActive = true;
    boolean gameStarted = false;
    boolean cpuTurn = false;

    // 2 means unplayed

    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    Vector<String> board;

    int[][] winningPositions = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}};

    ImageView winLine, counter;
    GridLayout gridLayout;
    RadioGroup radioGroupSymbol;
    RadioGroup radioGroupTurn;
    RadioButton radioBtnFirstTurn, radioBtnSecondTurn, radioBtnCross, radioBtnCircle;

    NormalTicTacAi normalAiObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_play_cpu_normal);

        gridLayout = (GridLayout)findViewById(R.id.gridLayout);
        normalAiObj = new NormalTicTacAi();
        board = new Vector<>();
        board.add("___");
        board.add("___");
        board.add("___");

        radioGroupTurn = (RadioGroup) findViewById(R.id.radiogroup_turn);
        radioBtnFirstTurn = (RadioButton) findViewById(R.id.radiobtn_first_turn);
        radioBtnSecondTurn = (RadioButton) findViewById(R.id.radiobtn_second_turn);

        radioBtnCross = (RadioButton) findViewById(R.id.radiobtn_cross);
        radioBtnCircle = (RadioButton) findViewById(R.id.radiobtn_circle);

        radioBtnFirstTurn.setChecked(true);
        radioBtnCross.setChecked(true);

        radioGroupTurn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radiobtn_second_turn){
                    //call CPU with empty board.
                    //visibility off function
                }
            }
        });

        radioGroupSymbol = (RadioGroup) findViewById(R.id.radiogroup_symbol);


    }

    public void dropIn(View view) {

        if(cpuTurn){
            cpuTurn = false;
        }
        else{
            cpuTurn = true;
        }

        counter = (ImageView) view;


        int tappedCounter = Integer.parseInt(counter.getTag().toString());

        if (gameState[tappedCounter] == 2 && gameIsActive) {

            if(!gameStarted){
                gameStarted = true;
                //visibility off func
            }

            setInitialPositions(counter, tappedCounter);

            gameState[tappedCounter] = activePlayer;
            board = updateBoardConfig(board, tappedCounter, activePlayer);
            Log.d("boardConfig: ", board.get(0)+"\n"+board.get(1)+"\n"+board.get(2));

            if (activePlayer == 1) {

                counter.setImageResource(R.drawable.cross);

                activePlayer = 0;

            } else {

                counter.setImageResource(R.drawable.circle);

                activePlayer = 1;

            }

            animateCounters(counter, tappedCounter);

//            counter.animate().translationYBy(1000f).rotation(360).setDuration(300);

            if(!checkWinner()) {

                if (cpuTurn) {
                    android.util.Pair<Integer, Integer> compTurn = normalAiObj.getSolution(board);
                    int tag = 3 * compTurn.first + compTurn.second;

                    view = findViewById(R.id.activity_board_play_cpu_normal).findViewWithTag(tag + "");
                    dropIn(view);
                }
            }

        }


    }

    private Vector<String> updateBoardConfig(Vector<String> oldBoard, int tappedCounter, int activePlayer) {

        Vector<String> newBoard = oldBoard;
        int row = tappedCounter/3;
        int col = tappedCounter%3;
        char symbol;

        symbol = 'X';
        if(activePlayer==1){
            symbol = 'O';
        }

        String oldStr = oldBoard.elementAt(row);
        StringBuilder strBuilder = new StringBuilder(oldStr);
        strBuilder.setCharAt(col, symbol);
        String newStr = String.valueOf(strBuilder);
        newBoard.set(row, newStr);

        return newBoard;
    }

    private boolean checkWinner() {
        int i = 0;
        for (int[] winningPosition : winningPositions) {

            if (gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                    gameState[winningPosition[0]] != 2) {

                // Someone has won!

                gameIsActive = false;

                String winner = "Cross";

                if (gameState[winningPosition[0]] == 0) {

                    winner = "Circle";

                }


                declareWinner(winningPosition, winner, i);
                return true;

            } else {

                i++;

                boolean gameIsOver = true;

                for (int counterState : gameState) {

                    if (counterState == 2) gameIsOver = false;

                }

                if (gameIsOver) {

                    TextView winnerMessage = (TextView) findViewById(R.id.winnerMessage);

                    winnerMessage.setText("It's a draw");

                    LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);

                    layout.setVisibility(View.VISIBLE);

                }

            }

        }
        return false;
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

    private void declareWinner(int[] winningPosition, String winner, int i) {

        winLine = (ImageView) findViewById(R.id.win_line);
        winLine.setVisibility(View.VISIBLE);
        winLine.setScaleX(0f);

        int line = 0;

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

    public void playAgain(View view) {

        gameIsActive = true;

        LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);

        layout.setVisibility(View.INVISIBLE);

        activePlayer = 0;

        for (int i = 0; i < gameState.length; i++) {

            gameState[i] = 2;

        }


        for (int i = 0; i< gridLayout.getChildCount(); i++) {

            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);

        }

    }
}
