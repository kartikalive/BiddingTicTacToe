package com.mindsortlabs.biddingtictactoe;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

public class BoardPlay2PlayerNormalActivity extends AppCompatActivity {

    // 0 = Circle, 1 = Cross

    int activePlayer = 0;

    boolean gameIsActive = true;
    MediaPlayer turnMediaPlayer, winMediaPlayer;

    // 2 means unplayed

    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    int[][] winningPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    ImageView winLine;
    GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_board_play2_player_normal);

        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        turnMediaPlayer = MediaPlayer.create(this, R.raw.sound1);
        winMediaPlayer = MediaPlayer.create(this, R.raw.sound2);
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void dropIn(View view) {

        ImageView counter = (ImageView) view;

        int tappedCounter = Integer.parseInt(counter.getTag().toString());

        if (gameState[tappedCounter] == 2 && gameIsActive) {

            if (SettingsActivity.soundEffects == 1) {
                if (turnMediaPlayer.isPlaying()) {
                    turnMediaPlayer.stop();
                    turnMediaPlayer.start();
                }
            }

            gameState[tappedCounter] = activePlayer;

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


            if (activePlayer == 0) {

                counter.setImageResource(R.drawable.cross);

                activePlayer = 1;

            } else {

                counter.setImageResource(R.drawable.circle);

                activePlayer = 0;

            }

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
//            counter.animate().translationYBy(1000f).rotation(360).setDuration(300);

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

                } else {

                    i++;

                    boolean gameIsOver = true;

                    for (int counterState : gameState) {

                        if (counterState == 2) gameIsOver = false;

                    }

                    if (gameIsOver) {

                        TextView winnerMessage = (TextView) findViewById(R.id.winnerMessage);

                        winnerMessage.setText("It's a draw");

                        LinearLayout layout = (LinearLayout) findViewById(R.id.playAgainLayout);

                        layout.setVisibility(View.VISIBLE);


                        MyPreferences myPreferences = new MyPreferences();
                        int gamePlayed = myPreferences.getGamePlayed(this);
                        gamePlayed +=1 ;

                        if(gamePlayed==MyPreferences.SHOW_ADS_AFTER_PLAYED_GAMES){
                            //show AD

                            gamePlayed = 0;
                        }
                        myPreferences.saveGamePlayed(this, gamePlayed);



                    }

                }

            }
        }


    }

    private void declareWinner(int[] winningPosition, String winner, int i) {

        if (SettingsActivity.soundEffects == 1) {
            if (turnMediaPlayer.isPlaying()) {
                turnMediaPlayer.stop();
                winMediaPlayer.start();
            }
        }

        winLine = (ImageView) findViewById(R.id.win_line);
        winLine.setVisibility(View.VISIBLE);
        winLine.setScaleX(0f);
        int line = 0;

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

    public void playAgain(View view) {

        gameIsActive = true;

        LinearLayout layout = (LinearLayout) findViewById(R.id.playAgainLayout);

        layout.setVisibility(View.INVISIBLE);

        activePlayer = 0;

        for (int i = 0; i < gameState.length; i++) {

            gameState[i] = 2;

        }


        for (int i = 0; i < gridLayout.getChildCount(); i++) {

            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);

        }

    }
}
