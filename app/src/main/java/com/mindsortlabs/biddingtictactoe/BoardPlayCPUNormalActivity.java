package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;

import java.util.Vector;

public class BoardPlayCPUNormalActivity extends AppCompatActivity {

    int activePlayer = 1;
    int userTurn = 1;
    char userSymbol = 'X';

    boolean gameIsActive = true;
    boolean gameStarted = false;
    boolean cpuTurn = false;

    int line = 0;
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

    SoundPool turnSound, winSound , drawSound;
    boolean turnSoundLoaded = false, winSoundLoaded = false, drawSoundLoaded;
    int turnSoundId, winSoundId, drawSoundId ;

    LinearLayout layoutPlayer1,layoutPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_board_play_cpu_normal);

        loadSound();

        turnSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                turnSoundLoaded = true;
//                playSound(1);
            }
        });

        winSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                winSoundLoaded = true;
            }
        });

        drawSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                drawSoundLoaded = true;
//                playSound(1);
            }
        });


        layoutPlayer1 = (LinearLayout) findViewById(R.id.layout_player1);
        layoutPlayer2 = (LinearLayout) findViewById(R.id.layout_player2);

        layoutPlayer1.getBackground().setAlpha(40);
        layoutPlayer2.getBackground().setAlpha(40);


//        turnMediaPlayer = MediaPlayer.create(this, R.raw.sound1);
//        winMediaPlayer = MediaPlayer.create(this, R.raw.sound2);

        gridLayout = (GridLayout)findViewById(R.id.gridLayout);
        normalAiObj = new NormalTicTacAi();
        board = new Vector<>();
        board.add("___");
        board.add("___");
        board.add("___");

        winLine = (ImageView) findViewById(R.id.win_line);

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

                    userTurn = 2;

                    char cpuSymbol = (char) ('X' + 'O' - userSymbol);

                    cpuTurn = true;
                    Pair<Integer, Integer> cpuTurnPair = normalAiObj.getSolution(board, cpuSymbol);

                    int tag = 3 * cpuTurnPair.first + cpuTurnPair.second;

                    View view = findViewById(R.id.activity_board_play_cpu_normal).findViewWithTag(tag + "");
                    dropIn(view);
                    //visibility off function
                    displayOptions(false);
                }
            }
        });

        radioGroupSymbol = (RadioGroup) findViewById(R.id.radiogroup_symbol);

        radioGroupSymbol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radiobtn_cross){
                    userSymbol = 'X';
                }
                if(i==R.id.radiobtn_circle){
                    userSymbol = 'O';
                }
            }
        });

    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void displayOptions(boolean display) {
        if(display) {
            radioGroupTurn.animate().translationYBy(-1000f).setDuration(500);
            radioGroupSymbol.animate().translationYBy(-1000f).setDuration(500);
        }
        else{
            radioGroupTurn.animate().translationY(1000f).setDuration(500);
            radioGroupSymbol.animate().translationY(1000f).setDuration(500);
        }


    }

    public void dropIn(View view) {

        if(cpuTurn){
            cpuTurn = false;
            activePlayer = 1;
        }
        else{
            cpuTurn = true;
            activePlayer = 0;
        }

        counter = (ImageView) view;

        int tappedCounter = Integer.parseInt(counter.getTag().toString());

        if (gameState[tappedCounter] == 2 && gameIsActive) {

            if(!gameStarted){
                gameStarted = true;
                displayOptions(false);
            }

            setInitialPositions(counter, tappedCounter);

            gameState[tappedCounter] = activePlayer;
            board = updateBoardConfig(board, tappedCounter, activePlayer, userSymbol);
            Log.d("boardConfig: ", board.get(0)+"\n"+board.get(1)+"\n"+board.get(2));

            if (userSymbol=='X'&&cpuTurn||userSymbol=='O'&&!cpuTurn) {

                counter.setImageResource(R.drawable.cross);

//                activePlayer = 0;

            } else {

                counter.setImageResource(R.drawable.circletwo);

//                activePlayer = 1;

            }

            animateCounters(counter, tappedCounter);

//            counter.animate().translationYBy(1000f).rotation(360).setDuration(300);

            if(!checkWinner()) {

                SoundActivity.playSound(this,turnSound,turnSoundLoaded,turnSoundId);

                if (cpuTurn) {

                    char cpuSymbol = (char) ('X' + 'O' - userSymbol);

                    Pair<Integer, Integer> compTurn = normalAiObj.getSolution(board,cpuSymbol);
                    int tag = 3 * compTurn.first + compTurn.second;

                    view = findViewById(R.id.activity_board_play_cpu_normal).findViewWithTag(tag + "");
                    Handler handler = new Handler();
                    final View finalView = view;
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dropIn(finalView);
                        }
                    }, 500);

                }
            }

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

    private boolean checkWinner() {
        int i = 0;
        for (int[] winningPosition : winningPositions) {

            if (gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                    gameState[winningPosition[0]] != 2) {

                // Someone has won!

                gameIsActive = false;

                String winner = "CPU";

                if (gameState[winningPosition[0]] == 0) {

                    winner = "Player";

                }
                Toast.makeText(this, winner + " wins.", Toast.LENGTH_SHORT).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        gameOverMessage(1);
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

    private void gameOverMessage(int i) {

        LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);
        TextView winnerMessage = (TextView) findViewById(R.id.winnerMessage);
        layout.setVisibility(View.VISIBLE);
        layout.setAlpha(0);

        SoundActivity.playSound(this,winSound,winSoundLoaded,winSoundId);
        winnerMessage.setText("CPU Wins");

        if(i==2) {
            SoundActivity.playSound(this,drawSound,drawSoundLoaded,drawSoundId);
            winnerMessage.setText("It's a draw");
        }

        layout.animate().alpha(1).setDuration(300);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DecidePlayOptionsNormalActivity.class);
        startActivity(intent);
        finish();
    }

    public void playAgain(View view) {


        Intent intent = new Intent(this, BoardPlayCPUNormalActivity.class);
        startActivity(intent);

        gameIsActive = true;

        LinearLayout layout = (LinearLayout)findViewById(R.id.playAgainLayout);

        layout.setVisibility(View.INVISIBLE);

        //PROBLEM WITH LINE IN NEXT GAME
        //winLine = (ImageView) findViewById(R.id.win_line);
//        winLine.setImageResource(R.drawable.linehorizontal);
//        winLine.layout(15,190,15,0);
        winLine.setVisibility(View.INVISIBLE);
        displayOptions(true);

        activePlayer = 1;
        userTurn = 1;
        userSymbol = 'X';
        cpuTurn = false;
        gameStarted = false;

        board = new Vector<>();
        board.add("___");
        board.add("___");
        board.add("___");

        for (int i = 0; i < gameState.length; i++) {

            gameState[i] = 2;

        }

        for (int i = 0; i< gridLayout.getChildCount(); i++) {

            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);

        }

    }

    private void loadSound() {
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            turnSound = new SoundPool.Builder().build();
            winSound = new SoundPool.Builder().build();
            drawSound = new SoundPool.Builder().build();
        }

        else {
            turnSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            winSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            drawSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        turnSoundId = turnSound.load(this, R.raw.sound1, 1);
        winSoundId = winSound.load(this, R.raw.sound2, 2);
        drawSoundId = drawSound.load(this, R.raw.sound2, 2);
    }

}
