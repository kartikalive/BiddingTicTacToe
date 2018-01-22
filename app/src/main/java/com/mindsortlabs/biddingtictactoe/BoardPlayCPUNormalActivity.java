package com.mindsortlabs.biddingtictactoe;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.targets.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mindsortlabs.biddingtictactoe.ai.NormalTicTacAi;
import com.mindsortlabs.biddingtictactoe.log.LogUtil;
import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

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
    int level=0;
    String winner = "CPU";

    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    Vector<String> board;

    int[][] winningPositions = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    ImageView winLine, counter;
    GridLayout gridLayout;
    RadioGroup radioGroupSymbol;
    RadioGroup radioGroupTurn;
    RadioButton radioBtnFirstTurn, radioBtnSecondTurn, radioBtnCross, radioBtnCircle;

    NormalTicTacAi normalAiObj;

    SoundPool turnSound, winSound, drawSound;
    boolean turnSoundLoaded = false, winSoundLoaded = false, drawSoundLoaded;
    int turnSoundId, winSoundId, drawSoundId;

    LinearLayout layoutPlayer1, layoutPlayer2;

    Handler handler;
    private InterstitialAd mInterstitialAd;

    // To control radio button visibility first player and x and o choose
    boolean isDisplayOn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_board_play_cpu_normal);


        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

            }
        });


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


        layoutPlayer1 = findViewById(R.id.layout_player1);
        layoutPlayer2 = findViewById(R.id.layout_player2);

//        gridContainerLayout = (LinearLayout) findViewById(R.id.full_container);

//        int height = gridContainerLayout.getWidth();
//        Log.d("checkHeight: ", height+"");
//        gridContainerLayout.setMinimumHeight(height);

        layoutPlayer1.getBackground().setAlpha(65);
        layoutPlayer2.getBackground().setAlpha(65);


//        turnMediaPlayer = MediaPlayer.create(this, R.raw.sound1);
//        winMediaPlayer = MediaPlayer.create(this, R.raw.sound2);

        gridLayout = findViewById(R.id.gridLayout);
        normalAiObj = new NormalTicTacAi();
        board = new Vector<>();
        board.add("___");
        board.add("___");
        board.add("___");

        handler = new Handler();
        winLine = findViewById(R.id.win_line);

        radioGroupTurn = findViewById(R.id.radiogroup_turn);
        radioBtnFirstTurn = findViewById(R.id.radiobtn_first_turn);
        radioBtnSecondTurn = findViewById(R.id.radiobtn_second_turn);

        radioBtnCross = findViewById(R.id.radiobtn_cross);
        radioBtnCircle = findViewById(R.id.radiobtn_circle);

        radioBtnFirstTurn.setChecked(true);
        radioBtnCross.setChecked(true);
        isDisplayOn = true;
        radioGroupTurn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radiobtn_second_turn) {

                    userTurn = 2;

                    char cpuSymbol = (char) ('X' + 'O' - userSymbol);

                    cpuTurn = true;
                    Pair<Integer, Integer> cpuTurnPair = normalAiObj.getSolution(board, cpuSymbol,level);

                    int tag = 3 * cpuTurnPair.first + cpuTurnPair.second;

                    View view = findViewById(R.id.activity_board_play_cpu_normal).findViewWithTag(tag + "");
                    dropInFull(view);
                    //visibility off function
                    displayOptions(false);
                }
            }
        });

        radioGroupSymbol = findViewById(R.id.radiogroup_symbol);

        radioGroupSymbol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radiobtn_cross) {
                    userSymbol = 'X';

                    layoutPlayer1.animate().alpha(0).setDuration(200);
                    layoutPlayer2.animate().alpha(0).setDuration(200);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutPlayer1.setBackgroundResource(R.drawable.lightcross);
                            layoutPlayer2.setBackgroundResource(R.drawable.lightcircle);
                            layoutPlayer1.animate().alpha(1f).setDuration(200);
                            layoutPlayer2.animate().alpha(1f).setDuration(200);
                        }
                    }, 200);
                }
                if (i == R.id.radiobtn_circle) {
                    userSymbol = 'O';

                    layoutPlayer1.animate().alpha(0).setDuration(200);
                    layoutPlayer2.animate().alpha(0).setDuration(200);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutPlayer1.setBackgroundResource(R.drawable.lightcircle);
                            layoutPlayer2.setBackgroundResource(R.drawable.lightcross);
                            layoutPlayer1.animate().alpha(1f).setDuration(200);
                            layoutPlayer2.animate().alpha(1f).setDuration(200);
                        }
                    }, 200);
                }
            }
        });

        RadioGroup rg =  findViewById(R.id.radioGroup);
        level = 0;//DEFAULT EASY
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioEasy:
                        level = 0 ;
                        break;
                    case R.id.radioMedium:
                        level = 1;
                        break;
                    case R.id.radioHard:
                        level = 2;
                        break;
                }
                playAgain();
            }
        });

    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void displayOptions(boolean display) {

        if(display==isDisplayOn)
            return;

        if (display) {
            isDisplayOn = true;
            radioGroupTurn.animate().translationYBy(-1000f).setDuration(500);
            radioGroupSymbol.animate().translationYBy(-1000f).setDuration(500);
        } else {
            isDisplayOn = false ;
            radioGroupTurn.animate().translationY(1000f).setDuration(500);
            radioGroupSymbol.animate().translationY(1000f).setDuration(500);
        }

    }

    public void dropIn(View view){
        if (LogUtil.islogOn()) {
            Log.d("CPU TRN ", "pPLEASE WAIT");
        }
        if(cpuTurn==false){
            dropInFull(view);
        }
    }

    public void dropInFull(View view) {

        counter = (ImageView) view;

        int tappedCounter = Integer.parseInt(counter.getTag().toString());

        if (gameState[tappedCounter] == 2 && gameIsActive) {

            if (cpuTurn) {
                cpuTurn = false;
                activePlayer = 1;
            } else {
                cpuTurn = true;
                activePlayer = 0;
            }

            if (!gameStarted) {
                gameStarted = true;
                displayOptions(false);
            }

            setInitialPositions(counter, tappedCounter);

            gameState[tappedCounter] = activePlayer;
            board = updateBoardConfig(board, tappedCounter, activePlayer, userSymbol);
            if (LogUtil.islogOn()) {
                Log.d("boardConfig: ", board.get(0) + "\n" + board.get(1) + "\n" + board.get(2));
            }
            if (userSymbol == 'X' && cpuTurn || userSymbol == 'O' && !cpuTurn) {

                counter.setImageResource(R.drawable.cross);

//                activePlayer = 0;

            } else {

                counter.setImageResource(R.drawable.circletwo);

//                activePlayer = 1;

            }

            animateCounters(counter, tappedCounter);

//            counter.animate().translationYBy(1000f).rotation(360).setDuration(300);

            if (!checkWinner()) {
                if (LogUtil.islogOn()) {
                    Log.d("Soundcheck1", "here: ");
                }
                SoundActivity.playSound(this, turnSound, turnSoundLoaded, turnSoundId);

                if (cpuTurn) {

                    char cpuSymbol = (char) ('X' + 'O' - userSymbol);

                    Pair<Integer, Integer> compTurn = normalAiObj.getSolution(board, cpuSymbol,level);
                    int tag = 3 * compTurn.first + compTurn.second;

                    view = findViewById(R.id.activity_board_play_cpu_normal).findViewWithTag(tag + "");
                    final View finalView = view;
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dropInFull(finalView);
                        }
                    }, 500);

                }
            }
//            else{
//                SoundActivity.playSound(this, winSound, winSoundLoaded, winSoundId);
//            }
        }


    }

    private Vector<String> updateBoardConfig(Vector<String> oldBoard, int tappedCounter, int activePlayer, char userSymbol) {

        Vector<String> newBoard = oldBoard;
        int row = tappedCounter / 3;
        int col = tappedCounter % 3;
        char symbol;

        symbol = 'X';
        if (activePlayer == 0 && userSymbol == 'O' || activePlayer == 1 && userSymbol == 'X') {
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
        boolean gameIsOver = true;
        for (int[] winningPosition : winningPositions) {

            if (gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                    gameState[winningPosition[0]] != 2) {

                // Someone has won!

                gameIsActive = false;

                winner = "CPU";

                if (gameState[winningPosition[0]] == 0) {

                    winner = "You";
                    //winner = "CPU";

                }
//                Toast.makeText(this, winner + " wins.", Toast.LENGTH_SHORT).show();

                handler.postDelayed(new Runnable() {
                    public void run() {
                        gameOverMessage(1);
                    }
                }, 500);
                declareWinner(winningPosition, winner, i);
                return true;

            } else {

                i++;

//                boolean gameIsOver = true;

                for (int counterState : gameState) {

                    if (counterState == 2) gameIsOver = false;

                }

                if (gameIsOver) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gameOverMessage(2);
                        }
                    }, 500);
                    gameIsOver = false;
                }

            }

        }
        return false;
    }

    private void gameOverMessage(int i) {

        LinearLayout layout = findViewById(R.id.playAgainLayout);
        TextView winnerMessage = findViewById(R.id.winnerMessage);
        layout.setVisibility(View.VISIBLE);
        layout.setAlpha(0);

        if (i == 1) {
            SoundActivity.playSound(this, winSound, winSoundLoaded, winSoundId);
            winnerMessage.setText(winner + " won");
        }

        if (i == 2) {
            SoundActivity.playSound(this,drawSound,drawSoundLoaded,drawSoundId);
            winnerMessage.setText("It's a draw");
        }

        layout.animate().alpha(1).setDuration(300);


        MyPreferences myPreferences = new MyPreferences();
        int gamePlayed = myPreferences.getGamePlayed(this);
        gamePlayed +=1 ;

        if(gamePlayed>=MyPreferences.SHOW_ADS_AFTER_PLAYED_GAMES){
            //show AD
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                gamePlayed = 0;
            } else {
                if (LogUtil.islogOn()) {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }

        }
        myPreferences.saveGamePlayed(this, gamePlayed);
        if (LogUtil.islogOn()) {
            Log.d("GAME PLAYED", ":::" + gamePlayed);
        }

    }

    private void animateCounters(ImageView counter, int tappedCounter) {

        switch (tappedCounter) {
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

        if (tappedCounter == 0 || tappedCounter == 1 || tappedCounter == 2) {
            counter.setTranslationY(-1000f);
        }
        if (tappedCounter == 0 || tappedCounter == 3 || tappedCounter == 6) {
            counter.setTranslationX(-1000f);
        }
        if (tappedCounter == 2 || tappedCounter == 5 || tappedCounter == 8) {
            counter.setTranslationX(1000f);
        }
        if (tappedCounter == 6 || tappedCounter == 7 || tappedCounter == 8) {
            counter.setTranslationY(1000f);
        }
        if (tappedCounter == 4) {
            counter.setScaleX(0.1f);
            counter.setScaleY(0.1f);
        }

    }

    private void declareWinner(int[] winningPosition, String winner, int i) {

        winLine.setVisibility(View.VISIBLE);
        winLine.setScaleX(0f);

        line = 0;

        if (i <= 2) {
            line = 1;
            if (winningPosition[0] == 0) {
                line = 0;
                winLine.setTranslationY(-(gridLayout.getWidth() / 3));
            } else if (winningPosition[0] == 6) {
                line = 2;
                winLine.setTranslationY((gridLayout.getWidth() / 3));
            }
            winLine.setScaleX(0f);
            winLine.animate().scaleX(1f).setDuration(200);
        } else if (i <= 5) {
            line = 4;
            winLine.setRotation(90);

            if (winningPosition[0] == 0) {
                line = 3;
                winLine.setTranslationX(-(gridLayout.getWidth() / 3));
            } else if (winningPosition[0] == 2) {
                line = 5;
                winLine.setTranslationX((gridLayout.getWidth() / 3));
            }
            winLine.setScaleX(0f);
            winLine.animate().scaleX(1f).setDuration(200);

        } else {

            if (winningPosition[0] == 0) {
                line = 6;
                winLine.setRotation(45);
            } else if (winningPosition[0] == 2) {
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
        removeFromMemory();
        super.onBackPressed();
    }

    private void removeFromMemory() {
        releaseSound();
        normalAiObj = null;
        if(radioGroupTurn!=null) {
            radioGroupTurn.setOnCheckedChangeListener(null);
        }
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }

    }


    private void releaseSound() {
        if (turnSound != null) {
            turnSound.setOnLoadCompleteListener(null);
            turnSound.release();
        }
        if (winSound != null) {
            winSound.setOnLoadCompleteListener(null);
            winSound.release();
        }
        if (drawSound != null) {
            drawSound.setOnLoadCompleteListener(null);
            drawSound.release();
        }

    }

    public void playAgain(View view) {


        //releaseSound();
//        loadSound();
       /* Intent intent = new Intent(this, BoardPlayCPUNormalActivity.class);
        startActivity(intent);
        finish();*/

        playAgain();
    }

    public void playAgain(){


        LinearLayout layout = findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.INVISIBLE);

        //PROBLEM WITH LINE IN NEXT GAME

        winLine.setTranslationX(0);
        winLine.setTranslationY(0);
        winLine.setRotation(0);
        winLine.setScaleX(1f);
        winLine.setVisibility(View.GONE);

        winLine = findViewById(R.id.win_line);
        //setContentView(R.layout.activity_board_play_cpu_normal);
        // winLine.setImageResource(R.drawable.linehorizontal);
        // winLine.layout(15,190,15,0);
        winLine.setVisibility(View.GONE);
        displayOptions(true);


        activePlayer = 1;
        userTurn = 1;
        userSymbol = 'X';

        gameIsActive = true;
        gameStarted = false;
        cpuTurn = false;

        radioBtnFirstTurn.setChecked(true);

        board = new Vector<>();
        board.add("___");
        board.add("___");
        board.add("___");

        for (int i = 0; i < gameState.length; i++) {

            gameState[i] = 2;

        }

        for (int i = 0; i < gridLayout.getChildCount(); i++) {

            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);

        }

    }

    private void loadSound() {
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            turnSound = new SoundPool.Builder().setMaxStreams(10).build();
            winSound = new SoundPool.Builder().setMaxStreams(10).build();
            drawSound = new SoundPool.Builder().setMaxStreams(10).build();
        }
//
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
