package com.mindsortlabs.biddingtictactoe.custom;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mindsortlabs.biddingtictactoe.R;
import com.mindsortlabs.biddingtictactoe.SoundActivity;
import com.mindsortlabs.biddingtictactoe.log.LogUtil;
import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

public class TwoPlayerCustom extends AppCompatActivity {

    LinearLayout layoutPlayer1, layoutPlayer2;


    int playerTurn = 1;
    int board_size;
    int objectives;

    CountDownTimer moveTimer;

    int player1Image = 1;// 1--> Cross
    int player2Image = 2;// 2--> Circle

    TextView tvView;
    TextView display;
    GridView gridview;

    int player1Score = 0;
    int player2Score = 0;


    int gameFinished = 0;//NOt finished
    int totalTurns = 0;

    SoundPool turnSound, winSound, drawSound, loseSound;
    boolean turnSoundLoaded = false, winSoundLoaded = false, drawSoundLoaded = false, loseSoundLoaded = false;
    int turnSoundId, winSoundId, drawSoundId, loseSoundId;
    int dx[] = {0, 1, 1, 1};
    int dy[] = {1, 0, 1, -1};

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_two_player_custom);


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



        Intent intent = getIntent();
        board_size = intent.getIntExtra("board_sizes", 3);
        objectives = intent.getIntExtra("objectives", 3);
        if (LogUtil.islogOn()) {
            Log.d("OBJECTIVE", "" + objectives);
        }
        TextView textView = findViewById(R.id.objective);
        textView.setText(" OBJECTIVE  :  " + objectives);

        gridview = findViewById(R.id.gridView);

        tvView = findViewById(R.id.tv_View);
        display = findViewById(R.id.display);

        layoutPlayer1 = findViewById(R.id.Player1);
        layoutPlayer2 = findViewById(R.id.Player2);

        layoutPlayer1.getBackground().setAlpha(65);
        layoutPlayer2.getBackground().setAlpha(65);


        gridview.setNumColumns(board_size);

        gridview.setAdapter(new ImageAdapter(this, board_size));

        gridview.setOnTouchListener(new View.OnTouchListener() {

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

                if (isgameFinished())
                    return;

                Pair pos = getPositionPair(position);

                //Replace with new Image according to player
                ImageView img = (ImageView) v;

                if (!img.getTag().equals(R.drawable.back))
                    return;

                totalTurns++;
                if (totalTurns == board_size * board_size) {

                    display.setText("ITS A DRAW");
                    gameOverMessage(0);

                }

                setImage(img);

                if (checkForSolution(playerTurn, pos)) {
                    // This player Wins
                    increamentWins(playerTurn);
                    UpdateLayout();
                    gameOverMessage(playerTurn);
                    //restartGame();

                } else {
                    SoundActivity.playSound(TwoPlayerCustom.this, turnSound, turnSoundLoaded, turnSoundId);
                    // Update for Second player Turn


                }


                if (!isgameFinished()) {
                    changePlayerTurn();
                    changeTextForPlayerTurn();
                }

            }
        });

        moveTimer = new CountDownTimer(3300, 1000) {
            @Override
            public void onTick(long l) {

                int time = (int) (l / 1000);
                if (LogUtil.islogOn()) {
                    Log.d("TAG123", (time) + "");
                }
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

        loseSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                loseSoundLoaded = true;
//                playSound(1);
            }
        });
    }

    public void playAgain(View view) {

        gridview.setAdapter(new ImageAdapter(this, board_size));
        LinearLayout layout = findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.GONE);

        player1Image = player1Image ^ player2Image ^ (player2Image = player1Image);

        // layoutPlayer1.getBackground().setAlpha(40);
        //layoutPlayer2.getBackground().setAlpha(40);


        layoutPlayer1 = findViewById(R.id.Player1);
        layoutPlayer2 = findViewById(R.id.Player2);

        layoutPlayer1.setBackground(getResources().getDrawable(getPlayer1Image1()));
        layoutPlayer2.setBackground(getResources().getDrawable(getPlayer2Image1()));

        layoutPlayer1.getBackground().setAlpha(65);
        layoutPlayer2.getBackground().setAlpha(65);


        changePlayerTurn();

        display.setText("Player " + playerTurn + "  Turn ");

        gameFinished = 0;
        totalTurns = 0;

        moveTimer.start();
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
        if (loseSound != null) {
            loseSound.setOnLoadCompleteListener(null);
            loseSound.release();
        }

    }



    private void gameOverMessage(int i) {

        LinearLayout layout = findViewById(R.id.playAgainLayout);
        TextView winnerMessage = findViewById(R.id.winnerMessage);
        layout.setVisibility(View.VISIBLE);
        layout.setAlpha(0);

        if (i == 1) {
            winnerMessage.setText("Player 1 wins");
            SoundActivity.playSound(this, winSound, winSoundLoaded, winSoundId);
        } else if (i == 2) {
            winnerMessage.setText("Player 2 wins");
            SoundActivity.playSound(this, winSound, winSoundLoaded, winSoundId);
        } else {
            winnerMessage.setText("It's a draw");
            SoundActivity.playSound(this, drawSound, drawSoundLoaded, drawSoundId);
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
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }

        }
        myPreferences.saveGamePlayed(this, gamePlayed);

    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void UpdateLayout() {

        if (playerTurn == 1) {
            display.setText(" Player 1  Wins  ");
        } else {
            display.setText(" Player 2  Wins  ");
        }


    }

    @Override
    public void onBackPressed() {
        releaseSound();
        super.onBackPressed();
    }

    private void increamentWins(int playerTurn) {
        if (playerTurn == 1)
            player1Score++;
        else
            player2Score++;

        TextView player2scoretv = findViewById(R.id.player2score);
        player2scoretv.setText("" + player2Score);

        //Log.d("plaier1",""+player2score);


        TextView player1scoretv = findViewById(R.id.player1score);
        player1scoretv.setText("" + player1Score);
    }

    private boolean checkForSolution(int playerTurn, Pair pos) {
        int counter = objectives;
        boolean temp = checkForSolutionHelper(pos, counter);
        if (temp) {
            gameFinished = 1;
        }
        return temp;

    }

    private boolean checkForSolutionHelper(Pair pos, int counter) {

        if (counter == 0)
            return true;

        int row_i = (int) pos.first, col_j = (int) pos.second;

        boolean flag = false;

        for (int i = 0; i < dx.length; i++) {
            int x = row_i, y = col_j;
            int tempcounter = counter;
            while (inRange(x, y)) {

                //Log.d("PLayer Turn" + counter, " " + getPosition(x, y) + "   ::" + x + "  " + y);
                ImageView img = (ImageView) gridview.getChildAt(getPosition(x, y));
                if (img != null && (int) img.getTag() == getPLayerTurnId()) {
                    tempcounter--;
                } else {
                    break;
                }
                x += dx[i];
                y += dy[i];
            }
            x = row_i - dx[i];
            y = col_j - dy[i];
            while (inRange(x, y)) {

                //Log.d("PLayer Turn" + counter, " " + getPosition(x, y) + "   ::" + x + "  " + y);
                ImageView img = (ImageView) gridview.getChildAt(getPosition(x, y));
                if (img != null && (int) img.getTag() == getPLayerTurnId()) {
                    tempcounter--;
                } else {
                    break;
                }
                x -= dx[i];
                y -= dy[i];
            }


            x += dx[i];
            y += dy[i];
            if (tempcounter <= 0) {
                flag = true;
                while (inRange(x, y)) {
                    ImageView img = (ImageView) gridview.getChildAt(getPosition(x, y));
                    if (img != null && (int) img.getTag() == getPLayerTurnId()) {
                        img.setBackgroundColor(getResources().getColor(R.color.lime));
                        x += dx[i];
                        y += dy[i];
                    } else {
                        break;
                    }
                }
            }


        }

        return flag;
    }

    private boolean inRange(int x, int y) {
        return x >= 0 && y >= 0 && x < board_size && y < board_size;
    }

    private boolean isgameFinished() {
        return gameFinished == 1;
    }

    private void changeTextForPlayerTurn() {
        display.setText("Player " + Integer.toString(playerTurn) + "  Turn");
    }

    private void changePlayerTurn() {
        if (playerTurn == 1)
            playerTurn = 2;
        else
            playerTurn = 1;
    }

    public int getPlayer1Image() {
        if (player1Image == 1)
            return R.drawable.cross;
        else
            return R.drawable.circletwo;
    }

    public int getPlayer2Image() {
        if (player2Image == 1)
            return R.drawable.cross;
        else
            return R.drawable.circletwo;
    }

    public int getPlayer1Image1() {
        if (player1Image == 1)
            return R.drawable.lightcross;
        else
            return R.drawable.lightcircle;
    }

    public int getPlayer2Image1() {
        if (player2Image == 1)
            return R.drawable.lightcross;
        else
            return R.drawable.lightcircle;
    }

    private void setImage(ImageView img) {


        if (img.getTag().equals(R.drawable.back)) {

            if (playerTurn == 1) {
                img.setImageResource(getPlayer1Image());
                img.setTag(getPlayer1Image());
            } else {
                img.setImageResource(getPlayer2Image());
                img.setTag(getPlayer2Image());
            }
        }

        //img.setAlpha(255);
    }

    int getPLayerTurnId() {
        if (playerTurn == 1) {
            return getPlayer1Image();
        } else {
            return getPlayer2Image();
        }
    }

    Pair<Integer, Integer> getPositionPair(int position) {
        int row_i = position / board_size;
        int col_j = position % board_size;

        return Pair.create(row_i, col_j);
    }

    int getPosition(int x, int y) {

        return x * board_size + y;

    }


    private void loadSound() {
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            turnSound = new SoundPool.Builder().build();
            winSound = new SoundPool.Builder().build();
            drawSound = new SoundPool.Builder().build();
            loseSound = new SoundPool.Builder().build();
        } else {
            turnSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            winSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            drawSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            loseSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        turnSoundId = turnSound.load(this, R.raw.sound1, 1);

        //Change -------------------------------------------------
        winSoundId = winSound.load(this, R.raw.sound2, 2);
        drawSoundId = drawSound.load(this, R.raw.sound2, 2);
        loseSoundId = loseSound.load(this, R.raw.sound2, 2);
    }


}
