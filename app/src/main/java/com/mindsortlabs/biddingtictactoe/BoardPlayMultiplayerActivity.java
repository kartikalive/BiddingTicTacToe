package com.mindsortlabs.biddingtictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.targets.Target;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mindsortlabs.biddingtictactoe.ads.LazyAds;
import com.mindsortlabs.biddingtictactoe.log.LogUtil;
import com.mindsortlabs.biddingtictactoe.preferences.MyPreferences;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mindsortlabs.biddingtictactoe.SettingsActivity.messagesPrefAccessKey;
import static com.mindsortlabs.biddingtictactoe.SettingsActivity.prefKey;
import static com.mindsortlabs.biddingtictactoe.SettingsActivity.soundPrefAccessKey;

public class BoardPlayMultiplayerActivity extends Activity implements
        View.OnClickListener,LazyAds.Implementable {

    /*
     * API INTEGRATION SECTION. This section contains the code that integrates
     * the game with the Google Play game services API.
     */

    final static String TAG = BoardPlayMultiplayerActivity.class.getSimpleName();

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    private static final int RC_LEADERBOARD_UI = 9004;


    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    // Client used to interact with the real time multiplayer system.
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;

    // Holds the configuration of the current room.
    RoomConfig mRoomConfig;

    // Are we playing in multiplayer mode?
    boolean mMultiplayer = false;

    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;

    // My participant ID in the currently active game
    String mMyId = null;

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];


    //Bidding T3 Variables--------------------------------------------------------
    TextView tvBid1, tvBid2, tvBidTime, tvTotal1, tvTotal2, tvPlayerTitle;
    ImageView imgGoldStack;
    ImageView counter, winLine;
    GridLayout gridLayout;
    Button btnSetBid;
    CountDownTimer moveTimer;
    Toast mToast;
    ImageButton btnMessage, btnSettings;

    RadioGroup radioGroupSymbol;
    RadioButton radioBtnCross, radioBtnCircle;
    LinearLayout layoutPlayer1, layoutPlayer2;

    boolean updatedBid1 = false, updatedBid2 = false;
    int bid1 = 1, bid2 = 1;
    int total1 = 100, total2 = 100;

    String opponentNickname = "Other";
    int opponentScore = -2;
    int oppWin = -2;
    int oppLoss = -2;
    int oppDraw = -2;

    boolean soundEffects;
    boolean messageNotifications;

    boolean gameActive = false;
    boolean gameStarted = false;
    boolean msgBoxOpen = false;
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int[][] winningPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
    int mySymbol = 0;
    int oppSymbol = 0;
    boolean myTurn = false;
    int line = 0;
    NumberPicker np;
    android.support.v7.app.AlertDialog alertDialog ;
    android.support.v7.app.AlertDialog msgDialog ;
    android.support.v7.app.AlertDialog settingsDialog ;
    boolean mPlayAgain = false;
    boolean oppPlayAgain = false;

    public static final char BID_SMALL = 'b';
    public static final char BID_BIG = 'B';
    public static final char POSITION = 'p';
    public static final char EVENT_CANCEL = 'c';
    public static final char MESSAGE = 'm';
    public static final char OWN_MESSAGE = 'o';
//    public static final char SOUND = 's';
    public static final char INITIAL_NICKNAME = 'n';
    public static final char INITIAL_SCORE = 's';
    public static final char INITIAL_TOTAL_COINS = 't';
    public static final char INITIAL_GAME_STATS = 'g';
    public static final int MESSAGE_BRB = 0;
    public static final int MESSAGE_THERE = 1;
    public static final int MESSAGE_PLAY_AGAIN = 2;

    private String[] MESSAGES = {"Be right back", "Hellooo, Are you there? ", "Let's play again?"
            , "Opponent left the room"};

    Handler handler = null;
    boolean playShown = false;

    SoundPool turnSound, winSound, drawSound, loseSound;
    boolean turnSoundLoaded = false, winSoundLoaded = false, drawSoundLoaded = false, loseSoundLoaded = false;
    int turnSoundId, winSoundId, drawSoundId, loseSoundId;

//    NumberPicker np;
    EditText mInputText;
//    android.support.v7.app.AlertDialog bidDialog ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_board_play_multiplayer);

        // Create the client used to sign in.
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        // set up a click listener for everything we care about
        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }
//        findViewById(R.id.button_sign_in).setOnClickListener(this);

//        gameOverMessage(1);
        if (LogUtil.islogOn()) {
            Log.d(TAG,"onCreate : ");
        }
        handler = new Handler();
        bindViews();
        loadRewardViews();
        initializeGameplayExtras();
        switchToMainScreen();
        checkPlaceholderIds();
        settingsVariables();
        btnSettings.setClickable(false);


//----------------------------------------SOUND---------------------------------------
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

    private void updateMyNickname() {
        MyPreferences myPreferences = new MyPreferences();
        String nickname = myPreferences.getNickname(this);
        TextView tvBidTitle1 = findViewById(R.id.tv_bid_title1);
        if(nickname.equals("")||nickname.equals("Guest")){
            tvBidTitle1.setText("My"+" Bid");
        }
        else {
            tvBidTitle1.setText(nickname + "\'s" + " Bid");
        }
    }

    private void initialBroadcast(int total1) {
        broadcastNickname();
        broadcastTotalCoins(total1);
        broadcastLeaderboardScore();
        broadcastGameStats();
    }

    private void updateMyTotalCoins() {
        MyPreferences myPreferences = new MyPreferences();
        total1 += myPreferences.getRewardedCoins(this);
        if (LogUtil.islogOn()) {
            Log.d("MultiplayerMyCoins: ", myPreferences.getRewardedCoins(this)+"");
        }
        tvTotal1.setText(total1+"");
        initialBroadcast(total1);//nickname and total coins
    }

    private void updateOpponentTotalCoins(String total) {
        tvTotal2.setText(total+"");
        total2 = Integer.parseInt(total);
    }

    private void updateOpponentNickname(String oppNickname) {

        if(oppNickname.equals("")){
            oppNickname = "Other";
        }

        opponentNickname = oppNickname;
        TextView tvBidTitle2 = findViewById(R.id.tv_bid_title2);
        tvBidTitle2.setText(oppNickname+"\'s"+" Bid");
    }

    private void updateOppLeaderboardScore(String oppScore){
        if(oppScore!="NA") {
            opponentScore = Integer.parseInt(oppScore);
        }
    }

    private void updateOppGameStats(String gameStats){

        //win draw loss
        int n = gameStats.length();
        int len1 = 0, len2 = 0, len3 = 0;
        for(int i=0;i<n;i++){
            if(gameStats.charAt(i)=='_'){
                len1 = i;
                break;
            }
        }

        for(int i=len1+1;i<n;i++){
            if(gameStats.charAt(i)=='_'){
                len2 = i - (len1+1);
                break;
            }
        }

        len3 = n - 2 - len1 - len2;
        String str1 = "0", str2 = "0", str3 = "0";
        str1 = gameStats.substring(0,len1);
        str2 = gameStats.substring(len1+1,len1+1+len2);
        str3 = gameStats.substring(len1+len2+2);

        oppWin = Integer.parseInt(str1);
        oppDraw = Integer.parseInt(str2);
        oppLoss = Integer.parseInt(str3);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void settingsVariables() {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.prefKey, Context.MODE_PRIVATE);

        if (prefs.getBoolean(SettingsActivity.soundPrefAccessKey, true)) {
            SettingsActivity.soundEffects = 1;
            soundEffects = true;
        }

        if (prefs.getBoolean(SettingsActivity.messagesPrefAccessKey, true)) {
            SettingsActivity.messageNotifications = 1;
            messageNotifications = true;
        }

    }

    private void initializeGameplayExtras() {

        initializeTimer();
        setListeners();

    }

    private void setListeners() {
        tvBid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change updatedBid1 to false and broadcast
                if(tvBidTime.getVisibility()==View.VISIBLE) {
                    broadcastEvent();
                    moveTimer.cancel();
                    tvBidTime.setVisibility(View.GONE);
                }
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                setBid();
            }
        });

        radioGroupSymbol = findViewById(R.id.radiogroup_symbol);
        radioGroupSymbol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radiobtn_cross) {
                    mySymbol = R.drawable.cross;
                    oppSymbol = R.drawable.circletwo;
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
                    mySymbol = R.drawable.circletwo;
                    oppSymbol = R.drawable.cross;
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

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessageBox();
                msgBoxOpen = true;
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsDialog();
            }
        });
    }

    private void showSettingsDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View theView = inflater.inflate(R.layout.dialog_settings, null);

        RadioGroup radioGroupMsg = theView.findViewById(R.id.radiogroup_msg);
        RadioGroup radioGroupSound = theView.findViewById(R.id.radiogroup_sound);

        final boolean[] tempSound = {soundEffects};

        final boolean[] tempMsg = {messageNotifications};

        if(soundEffects) {
            radioGroupSound.check(R.id.radiobtn_sound_on);
        }
        else{
            radioGroupSound.check(R.id.radiobtn_sound_off);
        }

        if(messageNotifications){
            radioGroupMsg.check(R.id.radiobtn_msg_on);
        }
        else{
            radioGroupMsg.check(R.id.radiobtn_msg_off);
        }

        radioGroupMsg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                tempMsg[0] = i == R.id.radiobtn_msg_on;
            }
        });

        radioGroupSound.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                tempSound[0] = i == R.id.radiobtn_sound_on;
            }
        });

        builder.setView(theView);
        builder.setView(theView)
                .setPositiveButton("Save",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        cancelToast();
                        mToast = Toast.makeText(BoardPlayMultiplayerActivity.this, "Settings saved", Toast.LENGTH_SHORT);
                        mToast.show();
                        soundEffects = tempSound[0];
                        messageNotifications = tempMsg[0];

                        if(soundEffects){
                            SettingsActivity.soundEffects = 1;
                        }
                        else{
                            SettingsActivity.soundEffects = 0;
                        }

                        if(messageNotifications){
                            SettingsActivity.messageNotifications = 1;
                        }
                        else{
                            SettingsActivity.messageNotifications = 0;
                        }

                        saveData(soundEffects,1);
                        saveData(messageNotifications,3);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        msgDialog = builder.create();
        msgDialog.show();
    }

    private void showLeaderboard() {
        if (LogUtil.islogOn()) {
            Log.d("leaderboard: ", "called");
        }
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getLeaderboardIntent(getString(R.string.leaderboard_id))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    private void saveData(boolean b, int variable) {
        SharedPreferences prefs = getSharedPreferences(prefKey, Context.MODE_PRIVATE);

        if (variable == 1) {
            prefs.edit().putBoolean(soundPrefAccessKey, b).apply();
        } else if (variable == 3) {
            prefs.edit().putBoolean(messagesPrefAccessKey, b).apply();
        }
    }

    private void setMessageBox() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View theView = inflater.inflate(R.layout.dialog_message_sender, null);

        final EditText etMsg = theView.findViewById(R.id.et_msg_content);

        builder.setView(theView);
        builder.setView(theView)
                .setPositiveButton("Send",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        msgBoxOpen = false;
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        String msgContent = String.valueOf(etMsg.getText());
                        broadcastOwnMessage(msgContent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                msgBoxOpen = false;
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        msgDialog = builder.create();
        msgDialog.show();
        msgDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        msgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void setBid() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View theView = inflater.inflate(R.layout.dialog_number_picker, null);

        np = theView.findViewById(R.id.num_picker);
        imgGoldStack = theView.findViewById(R.id.goldStackImg);
        builder.setView(theView);

        builder.setView(theView)
                .setPositiveButton("Set",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        tvBid1 = findViewById(R.id.tv_bid1);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        tvBid1.setText("Hidden");
                        tvBid1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
//                        bid1 = np.getValue();
                        mInputText = findInput(np);
                        mInputText.setFilters(new InputFilter[]{ new InputTextFilter() });
                        Log.d("TAGNumberPicker: ",mInputText.getText()+"");
                        bid1 = Integer.parseInt(mInputText.getText()+"");
                        updatedBid1 = true;

                        broadcastBid(bid1);
                        callTimer(updatedBid1, updatedBid2);
//                Log.d("TAG124","bid1: "+ bid1 + "  bid2: "+ bid2);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });


        np.setMaxValue(total1); // max value 100
        np.setMinValue(1);   // min value 0
        np.setValue(bid1);

        np.setWrapSelectorWheel(true);

        np.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                int bidSelected = view.getValue();
                setGoldStackImage(bidSelected);
            }
        });
//        final int[] bid = new int[1];
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                //numPickerBid1.setValue(i);
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private EditText findInput(ViewGroup np) {
        int count = np.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = np.getChildAt(i);
            if (child instanceof ViewGroup) {
                findInput((ViewGroup) child);
            } else if (child instanceof EditText) {
                return (EditText) child;
            }
        }
        return null;
    }

    private void setGoldStackImage(int bidSelected) {
        if(bidSelected<=10){
            imgGoldStack.setImageResource(R.drawable.goldcoin9);
        }else if(bidSelected<=20){
            imgGoldStack.setImageResource(R.drawable.goldcoin8);
        }else if(bidSelected<=30){
            imgGoldStack.setImageResource(R.drawable.goldcoin7);
        }else if(bidSelected<=40){
            imgGoldStack.setImageResource(R.drawable.goldcoin6);
        }else if(bidSelected<=50){
            imgGoldStack.setImageResource(R.drawable.goldcoin5);
        }else if(bidSelected<=60){
            imgGoldStack.setImageResource(R.drawable.goldcoin4);
        }else if(bidSelected<=70){
            imgGoldStack.setImageResource(R.drawable.goldcoin3);
        }else if(bidSelected<=80){
            imgGoldStack.setImageResource(R.drawable.goldcoin2);
        }else{
            imgGoldStack.setImageResource(R.drawable.goldcoin1);
        }
    }

    private void callTimer(boolean updatedBid1, boolean updatedBid2) {

        if (updatedBid1 && updatedBid2) {
            tvBidTime.setVisibility(View.VISIBLE);
            moveTimer.start();
        }
    }

    private void showProfileDialog(int title) {
        //title :
        // my = 1, opponent = 2
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View theView = inflater.inflate(R.layout.dialog_profile, null);

        final EditText etNickname = theView.findViewById(R.id.et_nickname);
        TextView tvNickname = theView.findViewById(R.id.tv_nickname);
        TextView tvRank = theView.findViewById(R.id.tv_rank);
        TextView tvWin = theView.findViewById(R.id.tv_win);
        TextView tvDraw = theView.findViewById(R.id.tv_draw);
        TextView tvLoss = theView.findViewById(R.id.tv_loss);
        TextView tvProfileTitle = theView.findViewById(R.id.tv_profile_title);

//        etNickname.setClickable(false);
        etNickname.setVisibility(View.GONE);
        tvNickname.setVisibility(View.VISIBLE);

        MyPreferences myPreferences = new MyPreferences();

        if(title==1) {
//            etNickname.setText(myPreferences.getNickname(this));
            tvNickname.setText(myPreferences.getNickname(this));
            tvRank.setText(myPreferences.getLeaderboardScore(this));
            tvWin.setText(myPreferences.getUserWin(this));
            tvDraw.setText(myPreferences.getUserDraw(this));
            tvLoss.setText(myPreferences.getUserLoss(this));
            tvProfileTitle.setText("My Profile");
        }
        else{
            tvNickname.setText(opponentNickname+"");
            tvRank.setText(opponentScore+"");
            tvWin.setText(oppWin+"");
            tvDraw.setText(oppDraw+"");
            tvLoss.setText(oppLoss+"");
            tvProfileTitle.setText("Opponent Profile");
        }

        builder.setView(theView);
        builder.setView(theView)
                .setPositiveButton("Close",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        android.support.v7.app.AlertDialog profileDialog;
        profileDialog = builder.create();
        profileDialog.show();
    }

    private void broadcastOwnMessage(String message) {
        byte[] buf = null;
        try {
            buf = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(buf!=null) {
            byte[] longMsgBuf = new byte[buf.length + 1]; //check maximum limit
            longMsgBuf[0] = OWN_MESSAGE;
            int i = 1;
            for (byte b : buf) {
                longMsgBuf[i] = b;
                i++;
            }

            try {
                String s = new String(longMsgBuf, "UTF-8");
                if (LogUtil.islogOn()) {
                    Log.d(TAG, "Message: " + s);
                }
//                customToast(s,Toast.LENGTH_SHORT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            if (mParticipants != null) {
                for (Participant p : mParticipants) {
                    if (p.getParticipantId().equals(mMyId)) {
                        continue;
                    }
                    if (p.getStatus() != Participant.STATUS_JOINED) {
                        continue;
                    }
                    mRealTimeMultiplayerClient.sendReliableMessage(longMsgBuf,
                            mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                                @Override
                                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "RealTime message sent");
                                        Log.d(TAG, "  statusCode: " + statusCode);
                                        Log.d(TAG, "  tokenId: " + tokenId);
                                        Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                                    }
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer tokenId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                                    }
                                }
                            });
                }
            }
        }
    }

    private void broadcastMessage(int messageId) {

        if(mRoomId==null){
            return;
        }

        mMsgBuf[0] = (byte) MESSAGE;
        mMsgBuf[1] = (byte) messageId;
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            mRealTimeMultiplayerClient.sendReliableMessage(mMsgBuf,
                    mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                        @Override
                        public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "RealTime message sent");
                                Log.d(TAG, "  statusCode: " + statusCode);
                                Log.d(TAG, "  tokenId: " + tokenId);
                                Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                            }
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer tokenId) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                            }
                        }
                    });
        }
    }

    private void broadcastEvent() {
        //cancelEvent
        mMsgBuf[0] = (byte)EVENT_CANCEL;
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            mRealTimeMultiplayerClient.sendReliableMessage(mMsgBuf,
                    mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                        @Override
                        public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "RealTime message sent");
                                Log.d(TAG, "  statusCode: " + statusCode);
                                Log.d(TAG, "  tokenId: " + tokenId);
                                Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                            }
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer tokenId) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                            }
                        }
                    });
        }
    }

    private void broadcastPosition(int pos) {
        mMsgBuf[0] = (byte)POSITION;
        mMsgBuf[1] = (byte) pos;

        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            mRealTimeMultiplayerClient.sendReliableMessage(mMsgBuf,
                    mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                        @Override
                        public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "RealTime message sent");
                                Log.d(TAG, "  statusCode: " + statusCode);
                                Log.d(TAG, "  tokenId: " + tokenId);
                                Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                            }
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer tokenId) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                            }
                        }
                    });
        }
    }

    private void broadcastBid(int bid){
        if (!mMultiplayer) {
            // playing single-player mode
            return;
        }

        if(bid<128) {
            mMsgBuf[0] = (byte)BID_SMALL;
            mMsgBuf[1] = (byte) bid;
        }
        else{
            mMsgBuf[0] = (byte)BID_BIG;
            mMsgBuf[1] = (byte)(bid - 128);
        }

        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            mRealTimeMultiplayerClient.sendReliableMessage(mMsgBuf,
                    mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                        @Override
                        public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "RealTime message sent");
                                Log.d(TAG, "  statusCode: " + statusCode);
                                Log.d(TAG, "  tokenId: " + tokenId);
                                Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                            }
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer tokenId) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                            }
                        }
                    });
        }
    }

    private void broadcastLeaderboardScore() {
        MyPreferences myPreferences = new MyPreferences();
        String score = myPreferences.getLeaderboardScore(this);

        byte[] buf = null;
        try {
            buf = score.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(buf!=null) {
            byte[] scoreBuf = new byte[buf.length + 1]; //check maximum limit
            scoreBuf[0] = INITIAL_SCORE;
            int i = 1;
            for (byte b : buf) {
                scoreBuf[i] = b;
                i++;
            }

//            try {
//                String s = new String(longMsgBuf, "UTF-8");
//                Log.d(TAG, "Message: " + s);
////                customToast(s,Toast.LENGTH_SHORT);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }


            if (mParticipants != null) {
                for (Participant p : mParticipants) {
                    if (p.getParticipantId().equals(mMyId)) {
                        continue;
                    }
                    if (p.getStatus() != Participant.STATUS_JOINED) {
                        continue;
                    }
                    mRealTimeMultiplayerClient.sendReliableMessage(scoreBuf,
                            mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                                @Override
                                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "RealTime message sent");
                                        Log.d(TAG, "  statusCode: " + statusCode);
                                        Log.d(TAG, "  tokenId: " + tokenId);
                                        Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                                    }
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer tokenId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                                    }
                                }
                            });
                }
            }
        }

    }

    private void broadcastGameStats() {
        MyPreferences myPreferences = new MyPreferences();
        String myWin = myPreferences.getUserWin(this);
        String myDraw = myPreferences.getUserDraw(this);
        String myLoss = myPreferences.getUserLoss(this);

        String gameStats = myWin + "_" + myDraw + "_" + myLoss;

        byte[] buf = null;
        try {
            buf = gameStats.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(buf!=null) {
            byte[] gameStatsBuf = new byte[buf.length + 1]; //check maximum limit
            gameStatsBuf[0] = INITIAL_GAME_STATS;
            int i = 1;
            for (byte b : buf) {
                gameStatsBuf[i] = b;
                i++;
            }

//            try {
//                String s = new String(longMsgBuf, "UTF-8");
//                Log.d(TAG, "Message: " + s);
////                customToast(s,Toast.LENGTH_SHORT);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }


            if (mParticipants != null) {
                for (Participant p : mParticipants) {
                    if (p.getParticipantId().equals(mMyId)) {
                        continue;
                    }
                    if (p.getStatus() != Participant.STATUS_JOINED) {
                        continue;
                    }
                    mRealTimeMultiplayerClient.sendReliableMessage(gameStatsBuf,
                            mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                                @Override
                                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "RealTime message sent");
                                        Log.d(TAG, "  statusCode: " + statusCode);
                                        Log.d(TAG, "  tokenId: " + tokenId);
                                        Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                                    }
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer tokenId) {
                                    Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                                }
                            });
                }
            }
        }


    }

    private void broadcastNickname() {

        MyPreferences myPreferences = new MyPreferences();
        String nickname = myPreferences.getNickname(this);

        byte[] buf = null;
        try {
            buf = nickname.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(buf!=null) {
            byte[] nicknameBuf = new byte[buf.length + 1]; //check maximum limit
            nicknameBuf[0] = INITIAL_NICKNAME;
            int i = 1;
            for (byte b : buf) {
                nicknameBuf[i] = b;
                i++;
            }

//            try {
//                String s = new String(longMsgBuf, "UTF-8");
//                Log.d(TAG, "Message: " + s);
////                customToast(s,Toast.LENGTH_SHORT);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }


            if (mParticipants != null) {
                for (Participant p : mParticipants) {
                    if (p.getParticipantId().equals(mMyId)) {
                        continue;
                    }
                    if (p.getStatus() != Participant.STATUS_JOINED) {
                        continue;
                    }
                    mRealTimeMultiplayerClient.sendReliableMessage(nicknameBuf,
                            mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                                @Override
                                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "RealTime message sent");

                                        Log.d(TAG, "  statusCode: " + statusCode);
                                        Log.d(TAG, "  tokenId: " + tokenId);
                                        Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                                    }
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer tokenId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                                    }
                                }
                            });
                }
            }
        }

    }

    private void broadcastTotalCoins(int total1) {

        String totalCoinsString = String.valueOf(total1);

        byte[] buf = null;
        try {
            buf = totalCoinsString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(buf!=null) {
            byte[] totalCoinsBuf = new byte[buf.length + 1]; //check maximum limit
            totalCoinsBuf[0] = INITIAL_TOTAL_COINS;
            int i = 1;
            for (byte b : buf) {
                totalCoinsBuf[i] = b;
                i++;
            }

//            try {
//                String s = new String(longMsgBuf, "UTF-8");
//                Log.d(TAG, "Message: " + s);
////                customToast(s,Toast.LENGTH_SHORT);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }


            if (mParticipants != null) {
                for (Participant p : mParticipants) {
                    if (p.getParticipantId().equals(mMyId)) {
                        continue;
                    }
                    if (p.getStatus() != Participant.STATUS_JOINED) {
                        continue;
                    }
                    mRealTimeMultiplayerClient.sendReliableMessage(totalCoinsBuf,
                            mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                                @Override
                                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "RealTime message sent");
                                        Log.d(TAG, "  statusCode: " + statusCode);
                                        Log.d(TAG, "  tokenId: " + tokenId);
                                        Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                                    }
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer tokenId) {
                                    if (LogUtil.islogOn()) {
                                        Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                                    }
                                }
                            });
                }
            }
        }
    }


    OnRealTimeMessageReceivedListener mOnRealTimeMessageReceivedListener = new OnRealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
            byte[] buf = realTimeMessage.getMessageData();
            String sender = realTimeMessage.getSenderParticipantId();
            if (LogUtil.islogOn()) {
                Log.d(TAG, "Message received: " + (char) buf[0]+", "+(int) buf[1]);
            }

            if(buf[0]==INITIAL_NICKNAME){

                String oppNickname = "";
                try {
                    oppNickname = new String(buf, "UTF-8");
                    oppNickname = oppNickname.substring(1);
                    if (LogUtil.islogOn()) {
                        Log.d(TAG, "Message received : " + oppNickname);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                updateOpponentNickname(oppNickname);
            }

            else if(buf[0]==INITIAL_TOTAL_COINS){
                String oppTotalCoins = "100";
                try {
                    oppTotalCoins = new String(buf, "UTF-8");
                    oppTotalCoins = oppTotalCoins.substring(1);
                    if (LogUtil.islogOn()) {
                        Log.d(TAG, "Message received : " + oppTotalCoins);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                updateOpponentTotalCoins(oppTotalCoins);
            }

            else if(buf[0]==INITIAL_SCORE){
                String oppLeaderboardScore = "NA";
                try {
                    oppLeaderboardScore = new String(buf, "UTF-8");
                    oppLeaderboardScore = oppLeaderboardScore.substring(1);
                    if (LogUtil.islogOn()) {
                        Log.d(TAG, "Message received : " + oppLeaderboardScore);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                updateOppLeaderboardScore(oppLeaderboardScore);
            }

            else if(buf[0]==INITIAL_GAME_STATS){
                String gameStats = "0_0_0";
                try {
                    gameStats = new String(buf, "UTF-8");
                    gameStats = gameStats.substring(1);
                    if (LogUtil.islogOn()) {
                        Log.d(TAG, "Message received : " + gameStats);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                updateOppGameStats(gameStats);
            }


            else if(buf[0]==BID_SMALL) {
                bid2 = (int) buf[1];
                updatedBid2 = true;
                callTimer(updatedBid1, updatedBid2);
                updatePeerBid();
            }

            else if(buf[0]==BID_BIG){
                bid2 = (int) buf[1] + 128;  //i.e. a number more than 128 has arrived.
                updatedBid2 = true;
                callTimer(updatedBid1, updatedBid2);
                updatePeerBid();
            }

            else if(buf[0]==POSITION){
                int oppPos = (int) buf[1];
                peerDropIn(oppPos);
            }

            else if(buf[0]==EVENT_CANCEL){
                moveTimer.cancel();
                tvBidTime.setVisibility(View.GONE);
                cancelToast();
                mToast = Toast.makeText(BoardPlayMultiplayerActivity.this, opponentNickname + " is changing Bid", Toast.LENGTH_SHORT);
                mToast.show();
            }

            else if(buf[0]==MESSAGE){
                int msgId = buf[1];

                if(msgId==0) {
                    cancelToast();
                    customToast(MESSAGES[msgId], Toast.LENGTH_SHORT);
                }

                if(msgId==2){
                    oppPlayAgain = true;
                    if(!mPlayAgain){
                        cancelToast();
                        customToast(MESSAGES[msgId], Toast.LENGTH_SHORT);
                    }
                    else{
                        displayOptions(true);
                        resetGameVariables();
                    }
                }
//                Toast.makeText(MultiplayerActivity.this, MESSAGES[msgId], Toast.LENGTH_SHORT).show();
            }

            else if(buf[0]==OWN_MESSAGE){
                try {
                    String msg = new String(buf, "UTF-8");
                    msg = msg.substring(1);
                    if (LogUtil.islogOn()) {
                        Log.d(TAG,"Message received : "+ msg);
                    }

                    //add sound also.
                    if(msg.length()<50) {
                        cancelToast();
                        customToast(msg, Toast.LENGTH_SHORT);
                    }
                    else{
                        cancelToast();
                        customToast(msg, Toast.LENGTH_LONG);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    };

//    private void customToast(String message, int duration){
//        SuperToast.create(this, message, Style.TYPE_BUTTON)
//                .setText(message)
//                .setDuration(duration)
//                .setColor(PaletteUtils.getSolidColor((PaletteUtils.MATERIAL_PURPLE)))
//                .setAnimations(Style.ANIMATIONS_FLY).show();
//    }

    private void customSystemToast(String message, int duration) {

        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_custom_toast, null);

        ImageView ivMsg = customView.findViewById(R.id.iv_msg);
        TextView tvMsg = customView.findViewById(R.id.tv_msg);
        ivMsg.setImageResource(R.drawable.ic_message_system);
        tvMsg.setText(message+"");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(customView);

        toast.show();

    }

    private void customToast(String message, int duration) {

        if(!messageNotifications){
            return;
        }
//        SuperActivityToast.create(this, new Style(), Style.TYPE_BUTTON)
//                .setButtonText("REPLY")
//                .setButtonIconResource(R.drawable.ic_send)
//                .setOnButtonClickListener("good_tag_name", null, onSendClickListener)
////                .setProgressBarColor(Color.WHITE)
//                .setText(message+"")
//                .setDuration(duration)
//                .setGravity(Gravity.CENTER_VERTICAL)
////                .setFrame(Style.FRAME_LOLLIPOP)
//                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PURPLE))
//                .setAnimations(Style.ANIMATIONS_FLY).show();


        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_custom_toast, null);

        ImageView ivMsg = customView.findViewById(R.id.iv_msg);
        ivMsg.setImageResource(R.drawable.ic_message_btn);
        TextView tvMsg = customView.findViewById(R.id.tv_msg);
        tvMsg.setText(opponentNickname+": " + message+"");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(customView);

        toast.show();

    }

//    SuperActivityToast.OnButtonClickListener onSendClickListener = new SuperActivityToast.OnButtonClickListener() {
//        @Override
//        public void onClick(View view, Parcelable token) {
//
//            if(!msgBoxOpen) {
//                setMessageBox();
//            }
//        }
//    };

    private void updatePeerBid() {
        tvBid2.setText("Hidden");
        tvBid2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
    }

    private void initializeTimer() {
        moveTimer = new CountDownTimer(3200, 1000) {
            @Override
            public void onTick(long l) {

                int time = (int) (l / 1000);
                if (LogUtil.islogOn()) {
                    Log.d("TAG123", (time) + "");
                }
                tvBidTime.setAlpha(1f);
                if (time > 1) {
                    tvBidTime.setText(String.valueOf(time));
                }
                else {
                    if(playShown){
                        tvBidTime.setText("");
                        tvBidTime.setAlpha(0);
                    }
                    if(!playShown) {
                        tvBidTime.setText("Play");
                        playShown = true;
                    }
                }

                tvBidTime.animate().alpha(0).setDuration(900);
            }

            @Override
            public void onFinish() {

                playShown = false;
//                gameActive = true;

                if (!gameStarted) {
                    gameStarted = true;
                    radioGroupSymbol.setClickable(false);
                    displayOptions(false);
                }

                tvBid1.animate().alpha(0).setDuration(200);
                tvBid2.animate().alpha(0).setDuration(200);
                if (bid1 != bid2) {
                    tvTotal1.animate().alpha(0).setDuration(200);
                    tvTotal2.animate().alpha(0).setDuration(200);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvBid1 = findViewById(R.id.tv_bid1);
                        tvBid2 = findViewById(R.id.tv_bid2);

                        tvBid1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
                        tvBid2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
                        tvBid1.setText(String.valueOf(bid1));
                        tvBid2.setText(String.valueOf(bid2));
                        tvBid1.animate().alpha(1f).setDuration(200);
                        tvBid2.animate().alpha(1f).setDuration(200);

                        cancelToast();
                        if (bid1 == bid2) {
                            gameActive = false;
                            mToast = Toast.makeText(BoardPlayMultiplayerActivity.this, "What a coincidence! Please choose again",
                                    Toast.LENGTH_SHORT);
                            mToast.show();
                            updatedBid1 = false;
                            updatedBid2 = false;
                        } else if (bid1 > bid2) {
                            gameActive = true;
                            myTurn = true;
                            mToast = Toast.makeText(BoardPlayMultiplayerActivity.this, "Your turn", Toast.LENGTH_SHORT);
                            mToast.show();
                            total1 = total1 - bid1;
                            total2 = total2 + bid1;
                            tvTotal1.setText(String.valueOf(total1));
                            tvTotal2.setText(String.valueOf(total2));
//                            activePlayer = 0;
                            tvTotal1.animate().alpha(1f).setDuration(200);
                            tvTotal2.animate().alpha(1f).setDuration(200);
                            tvBid1.setClickable(false);
                        } else {
                            gameActive = false;
                            myTurn = false;
                            mToast = Toast.makeText(BoardPlayMultiplayerActivity.this, "Opponent's turn", Toast.LENGTH_SHORT);
                            mToast.show();
                            total1 = total1 + bid2;
                            total2 = total2 - bid2;
                            tvTotal1.setText(String.valueOf(total1));
                            tvTotal2.setText(String.valueOf(total2));
//                            activePlayer = 1;
                            tvTotal1.animate().alpha(1f).setDuration(200);
                            tvTotal2.animate().alpha(1f).setDuration(200);
                            tvBid1.setClickable(false);
                        }
                    }
                }, 200);

                tvBidTime.setVisibility(View.GONE);
                //hide/display Options
            }
        };
    }

    void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }


    private void bindViews() {
        tvBid1 = findViewById(R.id.tv_bid1);
        tvBid2 = findViewById(R.id.tv_bid2);
        tvBidTime = findViewById(R.id.tv_bid_time);
        tvPlayerTitle = findViewById(R.id.tv_player_title);
        tvTotal1 = findViewById(R.id.tv_total1);
        tvTotal2 = findViewById(R.id.tv_total2);

        gridLayout = findViewById(R.id.gridLayout);
        winLine = findViewById(R.id.win_line_horizontal2);

        radioBtnCross = findViewById(R.id.radiobtn_cross);
        radioBtnCircle = findViewById(R.id.radiobtn_circle);

        layoutPlayer1 = findViewById(R.id.layout_player1);
        layoutPlayer2 = findViewById(R.id.layout_player2);

        btnMessage = findViewById(R.id.btn_message);
        btnSettings = findViewById(R.id.btn_settings);

        layoutPlayer1.getBackground().setAlpha(65);
        layoutPlayer2.getBackground().setAlpha(65);

        radioBtnCross.setChecked(true);
        mySymbol = R.drawable.cross;
        oppSymbol = R.drawable.circletwo;

    }

    void resetGameVariables() {
        bid1 = 1;
        bid2 = 1;
        total1 = 100;
        total2 = 100;
        updatedBid1 = false;
        updatedBid2 = false;
        tvBid1.setText("00");
        tvBid2.setText("00");
        gameActive = false;
        gameStarted = false;
        msgBoxOpen = false;
        myTurn = false;
        line = 0;
        tvBid1.setClickable(true);
        tvTotal1.setText(String.valueOf(total1));
        tvTotal2.setText(String.valueOf(total2));
        radioBtnCross.setChecked(true);
        mySymbol = R.drawable.cross;
        oppSymbol = R.drawable.circletwo;
        initializeCounters();
        winLine.setTranslationX(0);
        winLine.setTranslationY(0);
        winLine.setRotation(0);
        winLine.setScaleX(1f);
        winLine.setVisibility(View.GONE);

        for(int i=0;i<=8;i++){
            gameState[i] = 2;
        }

        mPlayAgain = false;
        oppPlayAgain = false;

        LinearLayout layout = findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.GONE);
        settingsVariables();
        updateMyTotalCoins();
        updateMyNickname();

    }

    private void initializeCounters() {
//        int pos = 0;
        for(int pos=0;pos<9;pos++){
            counter = (ImageView) findViewById(R.id.activity_multiplayer).findViewWithTag(String.valueOf(pos));
            counter.setImageDrawable(null);
        }
    }

    void startBiddingGame(boolean multiplayer) {
        resetGameVariables();
        mMultiplayer = multiplayer;
        switchToScreen(R.id.screen_gameplay);
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void displayOptions(boolean display) {
        if (display) {
            btnSettings.setClickable(false);
            btnSettings.animate().alpha(0).setDuration(500);
            radioGroupSymbol.animate().translationYBy(-1000f).setDuration(500);
            tvPlayerTitle.animate().translationYBy(-1000f).setDuration(500);
        } else {
            btnSettings.setClickable(true);
            btnSettings.animate().alpha(1).setDuration(500);
            radioGroupSymbol.animate().translationY(1000f).setDuration(500);
            tvPlayerTitle.animate().translationYBy(1000f).setDuration(500);
        }

    }

    public void dropIn(View view) {  //set in XML

        counter = (ImageView) view;
        int tappedCounter = Integer.parseInt(counter.getTag().toString());

        if (gameActive) {

            if(!myTurn){
                cancelToast();
                mToast = Toast.makeText(this, "Opponent's turn", Toast.LENGTH_SHORT);
                mToast.show();
                return;
            }


            if (gameState[tappedCounter] != 2) {
                cancelToast();
                mToast = Toast.makeText(this, "Choose another position", Toast.LENGTH_SHORT);
                mToast.show();
            } else {
                if (LogUtil.islogOn()) {
                    Log.d(TAG, "playedPos: " + tappedCounter);
                }
                broadcastPosition(tappedCounter);
                counter.setImageResource(mySymbol);

                setInitialPositions(counter, tappedCounter);
                gameState[tappedCounter] = 0;  //me = 0, opp = 1
                animateCounters(counter, tappedCounter);

                if (!checkWinner()) {
                    if(soundEffects) {
                        SoundActivity.playSound(this, turnSound, turnSoundLoaded, turnSoundId);
                    }
                    cancelToast();
                    mToast = Toast.makeText(this, "Time to Bid", Toast.LENGTH_SHORT);
                    mToast.show();
                }

                tvBid1.setClickable(true);
                updatedBid1 = false;
                updatedBid2 = false;
                tvBid1.animate().alpha(0).setDuration(200);
                tvBid2.animate().alpha(0).setDuration(200);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvBid1 = findViewById(R.id.tv_bid1);
                        tvBid2 = findViewById(R.id.tv_bid2);
                        tvBid1.setText("00");
                        tvBid1.animate().alpha(1f).setDuration(200);
                        tvBid2.setText("00");
                        tvBid2.animate().alpha(1f).setDuration(200);
                    }
                }, 200);
            }

            gameActive = false;
        } else {
            cancelToast();
            mToast = Toast.makeText(this, "Bid Pending", Toast.LENGTH_SHORT);
            mToast.show();
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
        if (loseSound != null) {
            loseSound.setOnLoadCompleteListener(null);
            loseSound.release();
        }

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

    private void peerDropIn(int pos){

        counter =(ImageView)findViewById(R.id.activity_multiplayer).findViewWithTag(String.valueOf(pos));
        int tappedCounter = pos;
        if (LogUtil.islogOn()) {
            Log.d(TAG,"playedPos: " + tappedCounter);
        }
        counter.setImageResource(oppSymbol);

        setInitialPositions(counter, tappedCounter);
        gameState[tappedCounter] = 1;  //me = 0, opp = 1
        animateCounters(counter, tappedCounter);

        if (!checkWinner()) {
            if(soundEffects){
                SoundActivity.playSound(this, turnSound, turnSoundLoaded, turnSoundId);
            }
            cancelToast();
            mToast = Toast.makeText(this, "Time to Bid", Toast.LENGTH_SHORT);
            mToast.show();
        }

        tvBid1.setClickable(true);
        updatedBid1 = false;
        updatedBid2 = false;
        tvBid1.animate().alpha(0).setDuration(200);
        tvBid2.animate().alpha(0).setDuration(200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvBid1 = findViewById(R.id.tv_bid1);
                tvBid2 = findViewById(R.id.tv_bid2);
                tvBid1.setText("00");
                tvBid1.animate().alpha(1f).setDuration(200);
                tvBid2.setText("00");
                tvBid2.animate().alpha(1f).setDuration(200);
            }
        }, 200);

        gameActive = false;
    }

    private boolean checkWinner() {
        int i = 0;
        boolean gameIsOver = true;
        for (int[] winningPosition : winningPositions) {

            if (gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                    gameState[winningPosition[0]] != 2) {

                // Someone has won!

                gameActive = false;

                String winner = opponentNickname;

                if (gameState[winningPosition[0]] == 0) {

                    winner = "You";

                }
                cancelToast();
                mToast = Toast.makeText(this, winner + " won", Toast.LENGTH_SHORT);
                mToast.show();

                final int winnerPlayer = gameState[winningPosition[0]];

                handler.postDelayed(new Runnable() {
                    public void run() {
                        gameOverMessage(winnerPlayer);
                    }
                }, 500);
                declareWinner(winningPosition, winner, i);
                return true;

            } else {

                i++;

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
        deductCoins();

        if (i == 1) {
            if(soundEffects){
                SoundActivity.playSound(this, winSound, winSoundLoaded, winSoundId);
            }
            winnerMessage.setText(opponentNickname+ " won");
        } else if (i == 2) {
            winnerMessage.setText("It's a draw");
            if(soundEffects){
                SoundActivity.playSound(this, drawSound, drawSoundLoaded, drawSoundId);
            }
        } else {
            winnerMessage.setText("You won");
            if(soundEffects){
                SoundActivity.playSound(this, winSound, winSoundLoaded, winSoundId);
            }
        }

        layout.animate().alpha(1).setDuration(300);

        MyPreferences myPreferences = new MyPreferences();
        myPreferences.saveGameStats(this,i);
        scoreUpdationAlgorithm(i);
    }

    private int scoreUpdationAlgorithm(int status) { //returns increase in score.

        MyPreferences myPreferences = new MyPreferences();
        int currScore = Integer.parseInt(myPreferences.getLeaderboardScore(this));

        int newScore = currScore;
        int changeInScore = 0;
        if(status==1&&opponentScore>currScore){
            changeInScore = -10;
        }
        else if(status==1&&opponentScore<=currScore){
            changeInScore = -30;
        }

        else if(status==2){
            changeInScore = 10;
        }

        else if(opponentScore>=currScore){
            changeInScore = 50;
        }

        else if(opponentScore<currScore){
            changeInScore = 30;
        }

        newScore+=changeInScore;
        if(newScore<0){
            newScore = 0;
        }
        if (LogUtil.islogOn()) {
            Log.d("leaderboardScore: ",newScore+"");
        }
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .submitScore(getString(R.string.leaderboard_id), newScore);
        myPreferences.saveLeaderboardScore(this,newScore+"");

        return changeInScore;
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

//        mergeImages(line);
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
            counter.setScaleX(0);
            counter.setScaleY(0);
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

    public void playAgain(View view) {
//        releaseSound();
        LinearLayout layout = findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.GONE);
        mPlayAgain = true;
        broadcastMessage(2);

        if(oppPlayAgain) {
            displayOptions(true);
            resetGameVariables();
        }
        else{
            String msg = "Requesting opponent...";
            cancelToast();
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            mToast.show();
        }
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startBiddingGame(false);
//                Log.d(TAG,"insideHandler: ");
//            }
//        }, 500);
//        finish();
//        startActivity(getIntent());

    }







    //----------------------------------------------------------------------------------------------------

    // Check the sample to ensure all placeholder ids are are updated with real-world values.
    // This is strictly for the purpose of the samples; you don't need this in a production
    // application.
    private void checkPlaceholderIds() {
        StringBuilder problems = new StringBuilder();

        if (getPackageName().startsWith("com.google.")) {
            problems.append("- Package name start with com.google.*\n");
        }

        for (Integer id : new Integer[]{R.string.app_id}) {

            String value = getString(id);

            if (value.startsWith("YOUR_")) {
                // needs replacing
                problems.append("- Placeholders(YOUR_*) in ids.xml need updating\n");
                break;
            }
        }

        if (problems.length() > 0) {
            problems.insert(0, "The following problems were found:\n\n");

            problems.append("\nThese problems may prevent the app from working properly.");
            problems.append("\n\nSee the TODO window in Android Studio for more information");
            (new AlertDialog.Builder(this)).setMessage(problems.toString())
                    .setNeutralButton(android.R.string.ok, null).create().show();
        }
    }

    @Override
    protected void onResume() {
        lazyAds.onResume();
        super.onResume();
        if (LogUtil.islogOn()) {
            Log.d(TAG, "onResume()");
        }

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        if (mRealTimeMultiplayerClient == null ) {
            signInSilently();
        }
    }

    @Override
    protected void onPause() {
        lazyAds.onPause();
        super.onPause();
        if (LogUtil.islogOn()) {
            Log.d(TAG,"onPause: ");
        }
        // unregister our listeners.  They will be re-registered via onResume->signInSilently->onConnected.
        if (mInvitationsClient != null) {
            mInvitationsClient.unregisterInvitationCallback(mInvitationCallback);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_single_player:
            case R.id.button_single_player_2:
                // play a single-player game
                startBiddingGame(false);
                break;
            case R.id.button_sign_in:
                // start the sign-in flow
                if (LogUtil.islogOn()) {
                    Log.d(TAG, "Sign-in button clicked");
                }
                if(!isNetworkAvailable()){
                    showNetworkError();
                }
                else {
                    startSignInIntent();
                }
                break;
            case R.id.btn_sign_out:
                // user wants to sign out
                // sign out.
                if (LogUtil.islogOn()) {
                    Log.d(TAG, "Sign-out button clicked");
                }
                signOut();
                switchToScreen(R.id.screen_sign_in);
                break;
            case R.id.btn_invite_players:

                if(!isNetworkAvailable()){
                    showNetworkError();
                }
                else {
                    switchToScreen(R.id.screen_wait);

                    // show list of invitable players
                    mRealTimeMultiplayerClient.getSelectOpponentsIntent(1, 1).addOnSuccessListener(
                            new OnSuccessListener<Intent>() {
                                @Override
                                public void onSuccess(Intent intent) {
                                    startActivityForResult(intent, RC_SELECT_PLAYERS);
                                }
                            }
                    ).addOnFailureListener(createFailureListener("There was a problem selecting opponents."));
                }
                break;
            case R.id.btn_see_invitations:
                if(!isNetworkAvailable()){
                    showNetworkError();
                }
                else {
                    switchToScreen(R.id.screen_wait);

                    // show list of pending invitations
                    mInvitationsClient.getInvitationInboxIntent().addOnSuccessListener(
                            new OnSuccessListener<Intent>() {
                                @Override
                                public void onSuccess(Intent intent) {
                                    startActivityForResult(intent, RC_INVITATION_INBOX);
                                }
                            }
                    ).addOnFailureListener(createFailureListener("There was a problem getting the inbox."));
                }
                break;
            case R.id.button_accept_popup_invitation:
                if(!isNetworkAvailable()){
                    showNetworkError();
                }
                else {
                    // user wants to accept the invitation shown on the invitation popup
                    // (the one we got through the OnInvitationReceivedListener).
                    acceptInviteToRoom(mIncomingInvitationId);
                    mIncomingInvitationId = null;
                }
                break;
            case R.id.btn_quick_game:
                // user wants to play against a random opponent right now
                if(!isNetworkAvailable()){
                    showNetworkError();
                }
                else {
                    startQuickGame();
                }
                break;

            case R.id.btn_leaderboard:
                showLeaderboard();
                break;

            case R.id.tv_bid_title1:
                showProfileDialog(1);
                break;
            case R.id.tv_bid_title2:
                showProfileDialog(2);
                break;
//            case R.id.button_click_me:
//                // (gameplay) user clicked the "click me" button
//                scoreOnePoint();
//                break;
        }
    }

    void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria)
                .build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
    }

    /**
     * Start a sign in activity.  To properly handle the result, call tryHandleSignInResult from
     * your Activity's onActivityResult function
     */
    public void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    /**
     * Try to sign in without displaying dialogs to the user.
     * <p>
     * If the user has already signed in previously, it will not show dialog.
     */
    public void signInSilently() {
        if (LogUtil.islogOn()) {
            Log.d(TAG, "signInSilently()");
        }

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "signInSilently(): success");
                            }
                            onConnected(task.getResult());
                        } else {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "signInSilently(): failure", task.getException());
                            }
                            onDisconnected();
                        }
                    }
                });
    }

    public void signOut() {
        if (LogUtil.islogOn()) {
            Log.d(TAG, "signOut()");
        }

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            if (LogUtil.islogOn()) {
                                Log.d(TAG, "signOut(): success");
                            }
                        } else {
                            handleException(task.getException(), "signOut() failed!");
                        }

                        onDisconnected();
                    }
                });
    }

    /**
     * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
     *
     * @param exception The exception to evaluate.  Will try to display a more descriptive reason for the exception.
     * @param details   Will display alongside the exception if you wish to provide more details for why the exception
     *                  happened
     */
    private void handleException(Exception exception, String details) {
        int status = 0;

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        String errorString = null;
        switch (status) {
            case GamesCallbackStatusCodes.OK:
                break;
            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                errorString = getString(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                errorString = getString(R.string.match_error_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                errorString = getString(R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                errorString = getString(R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                errorString = getString(R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                errorString = getString(R.string.match_error_locally_modified);
                break;
            default:
                errorString = getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status));
                break;
        }

        if (errorString == null) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);

        new AlertDialog.Builder(BoardPlayMultiplayerActivity.this)
                .setTitle("Error")
                .setMessage(message + "\n" + errorString)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                if(LogUtil.islogOn()) {
                    Log.d(BoardPlayMultiplayerActivity.class.getSimpleName(), apiException.getStatusCode() + "");
                }
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                onDisconnected();
                /*
                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();*/
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // we got the result from the "select players" UI -- ready to create the room
            handleSelectPlayersResult(resultCode, intent);

        } else if (requestCode == RC_INVITATION_INBOX) {
            // we got the result from the "select invitation" UI (invitation inbox). We're
            // ready to accept the selected invitation:
            handleInvitationInboxResult(resultCode, intent);

        } else if (requestCode == RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            if (resultCode == Activity.RESULT_OK) {
                // ready to start playing
                if (LogUtil.islogOn()) {
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                }
                startBiddingGame(true);
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player indicated that they want to leave the room
                leaveRoom();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance). In our game,
                // this means leaving the room too. In more elaborate games, this could mean
                // something else (like minimizing the waiting room UI).
                leaveRoom();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.

    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            switchToMainScreen();
            return;
        }
        if (LogUtil.islogOn()) {
            Log.d(TAG, "Select players UI succeeded.");
        }
        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        if (LogUtil.islogOn()) {
            Log.d(TAG, "Invitee count: " + invitees.size());
        }
        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            if (LogUtil.islogOn()) {
                Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
            }
        }

        // create the room
        if (LogUtil.islogOn()) {
            Log.d(TAG, "Creating room...");
        }
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .addPlayersToInvite(invitees)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria).build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
        if (LogUtil.islogOn()) {
            Log.d(TAG, "Room created, waiting for it to be ready...");
        }
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            switchToMainScreen();
            return;
        }
        if (LogUtil.islogOn()) {
            Log.d(TAG, "Invitation inbox UI succeeded.");
        }
        Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        if (invitation != null) {
            acceptInviteToRoom(invitation.getInvitationId());
        }
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invitationId) {
        // accept the invitation
        if (LogUtil.islogOn()) {
            Log.d(TAG, "Accepting invitation: " + invitationId);
        }
        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setInvitationIdToAccept(invitationId)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .build();

        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();

        mRealTimeMultiplayerClient.join(mRoomConfig)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (LogUtil.islogOn()) {
                            Log.d(TAG, "Room Joined Successfully!");
                        }
                    }
                });
    }

    // Activity is going to the background. We have to leave the current room.
    @Override
    public void onStop() {
        if (LogUtil.islogOn()) {
            Log.d(TAG, "**** got onStop");
        }

        try {
            if (mCurScreen== R.id.screen_gameplay) {
                broadcastMessage(MESSAGE_BRB);
            }
        }
        catch (Exception e){
            if (LogUtil.islogOn()) {
                Log.d(TAG,"onStop Exception: "+e);
            }
        }
        // if we're in a room, leave it.

        if(mCurScreen!=R.id.screen_gameplay) {
            leaveRoom();
            switchToMainScreen();
        }

        // stop trying to keep the screen on
        stopKeepingScreenOn();



        super.onStop();
    }


    // Handle back key to make sure we cleanly leave a game if we are in the middle of one
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCurScreen == R.id.screen_gameplay) {
            if (LogUtil.islogOn()) {
                Log.d("coinsCalc: ", "backPressed.");
            }

            if(winLine.getVisibility()==View.VISIBLE){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to leave the game?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                leaveRoom();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
//                        hideStatusBar();
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
//                hideStatusBar();
                    }
                });
                builder.show();
            }
            else if(halfGamePlayed()) {
                deductCoins();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Resign and finish the game?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                MyPreferences myPreferences = new MyPreferences();
                                myPreferences.saveGameStats(getBaseContext(),1);
                                scoreUpdationAlgorithm(1);

                                leaveRoom();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
//                        hideStatusBar();
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
//                hideStatusBar();
                    }
                });
                builder.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Abort the game?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                leaveRoom();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
//                        hideStatusBar();
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
//                hideStatusBar();
                    }
                });
                builder.show();
            }

            return true;
        }

        return super.onKeyDown(keyCode, e);
    }

    // Leave the room.
    void leaveRoom() {
        if (LogUtil.islogOn()) {
            Log.d(TAG, "Leaving room.");
        }
        mSecondsLeft = 0;
        stopKeepingScreenOn();
        if (mRoomId != null) {
            mRealTimeMultiplayerClient.leave(mRoomConfig, mRoomId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mRoomId = null;
                            mRoomConfig = null;
                        }
                    });
            switchToScreen(R.id.screen_wait);
        } else {
            switchToMainScreen();
        }
    }

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        mRealTimeMultiplayerClient.getWaitingRoomIntent(room, MIN_PLAYERS)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        // show waiting room UI
                        startActivityForResult(intent, RC_WAITING_ROOM);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem getting the waiting room!"));
    }

    private InvitationCallback mInvitationCallback = new InvitationCallback() {
        // Called when we get an invitation to play a game. We react by showing that to the user.
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) {
            // We got an invitation to play a game! So, store it in
            // mIncomingInvitationId
            // and show the popup on the screen.
            mIncomingInvitationId = invitation.getInvitationId();
            ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                    invitation.getInviter().getDisplayName() + " " +
                            getString(R.string.is_inviting_you));
            switchToScreen(mCurScreen); // This will show the invitation popup
        }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) {

            if (mIncomingInvitationId.equals(invitationId) && mIncomingInvitationId != null) {
                mIncomingInvitationId = null;
                switchToScreen(mCurScreen); // This will hide the invitation popup
            }
        }
    };

    /*
     * CALLBACKS SECTION. This section shows how we implement the several games
     * API callbacks.
     */

    private String mPlayerId;

    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    GoogleSignInAccount mSignedInAccount = null;

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        if (LogUtil.islogOn()) {
            Log.d(TAG, "onConnected(): connected to Google APIs");
        }
        if (mSignedInAccount != googleSignInAccount) {

            mSignedInAccount = googleSignInAccount;

            // update the clients
            mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, googleSignInAccount);
            mInvitationsClient = Games.getInvitationsClient(BoardPlayMultiplayerActivity.this, googleSignInAccount);

            // get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
            playersClient.getCurrentPlayer().addOnSuccessListener(
                    new OnSuccessListener<Player>() {
                        @Override
                        public void onSuccess(Player player) {
                            mPlayerId = player.getPlayerId();
                        }
                    }
            );
        }

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        mInvitationsClient.registerInvitationCallback(mInvitationCallback);

        // get the invitation from the connection hint
        // Retrieve the TurnBasedMatch from the connectionHint
        GamesClient gamesClient = Games.getGamesClient(BoardPlayMultiplayerActivity.this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            Invitation invitation =
                                    hint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

                            if (invitation != null && invitation.getInvitationId() != null) {
                                // retrieve and cache the invitation ID
                                if (LogUtil.islogOn()) {
                                    Log.d(TAG, "onConnected: connection hint has a room invite!");
                                }
                                acceptInviteToRoom(invitation.getInvitationId());
                            }
                        }
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem getting the activation hint!"));

        switchToMainScreen();
    }

    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, string);
            }
        };
    }

    public void onDisconnected() {
        if (LogUtil.islogOn()) {
            Log.d(TAG, "onDisconnected()");
        }

        mRealTimeMultiplayerClient = null;
        mInvitationsClient = null;

        switchToMainScreen();
    }

    private RoomStatusUpdateCallback mRoomStatusUpdateCallback = new RoomStatusUpdateCallback() {
        // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
        // is connected yet).
        @Override
        public void onConnectedToRoom(Room room) {
            if (LogUtil.islogOn()) {
                Log.d(TAG, "onConnectedToRoom.");
            }

            //get participants and my ID:
            mParticipants = room.getParticipants();
            mMyId = room.getParticipantId(mPlayerId);

            // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
            if (mRoomId == null) {
                mRoomId = room.getRoomId();
            }

            // print out the list of participants (for debug purposes)
            if (LogUtil.islogOn()) {
                Log.d(TAG, "Room ID: " + mRoomId);
                Log.d(TAG, "My ID " + mMyId);
                Log.d(TAG, "<< CONNECTED TO ROOM>>");
            }
        }

        // Called when we get disconnected from the room. We return to the main screen.
        @Override
        public void onDisconnectedFromRoom(Room room) {
            mRoomId = null;
            mRoomConfig = null;
            showGameError();
        }


        // We treat most of the room update callbacks in the same way: we update our list of
        // participants and update the display. In a real game we would also have to check if that
        // change requires some action like removing the corresponding player avatar from the screen,
        // etc.
        @Override
        public void onPeerDeclined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerInvitedToRoom(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onP2PDisconnected(@NonNull String participant) {
            if (LogUtil.islogOn()) {
                Log.d(TAG,"P2P DISCONNECTED");
            }
        }

        @Override
        public void onP2PConnected(@NonNull String participant) {
        }

        @Override
        public void onPeerJoined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerLeft(Room room, @NonNull List<String> peersWhoLeft) {
            if (LogUtil.islogOn()) {
                Log.d(TAG,"PEER LEFT RESET ALL");
            }

            String s;
            if(winLine.getVisibility()==View.VISIBLE){
                s = "Opponent left the room";
            }
            else if(halfGamePlayed()) {
                s = "Opponent left the room\n" + " You Won";
                deductCoins();
                MyPreferences myPreferences = new MyPreferences();
                myPreferences.saveGameStats(getBaseContext(),0);
                scoreUpdationAlgorithm(0);
            }
            else{
                s = "Opponent left too soon.";
            }
            customSystemToast(s, Toast.LENGTH_SHORT);

            if(alertDialog!=null && alertDialog.isShowing())
                alertDialog.dismiss();

            if(msgDialog!=null && msgDialog.isShowing())
                msgDialog.dismiss();
            updateRoom(room);
        }

        @Override
        public void onRoomAutoMatching(Room room) {
            updateRoom(room);
        }

        @Override
        public void onRoomConnecting(Room room) {
            updateRoom(room);
        }

        @Override
        public void onPeersConnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(Room room, @NonNull List<String> peers) {
            if (LogUtil.islogOn()) {
                Log.d(TAG,"on PEERs DISCONNECTED");
            }
            updateRoom(room);
        }
    };

    private boolean halfGamePlayed() {

        if(gameState==null){
            return false;
        }

        int turnsPlayed = 0;
        for(int i=0;i<8;i++){
            if(gameState[i]!=2){
                turnsPlayed++;
            }
        }

        return turnsPlayed > 1;

    }

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {

        if(!isNetworkAvailable()){
            Toast.makeText(this, R.string.no_network_error, Toast.LENGTH_SHORT).show();
        }
     /*   new AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).create().show();
     */
        switchToMainScreen();
    }

    void showNetworkError() {

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.no_network_error))
                .setNeutralButton(android.R.string.ok, null).create().show();

        switchToMainScreen();
    }

    private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback() {

        // Called when room has been created
        @Override
        public void onRoomCreated(int statusCode, Room room) {
            if (LogUtil.islogOn()) {
                Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
            }
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
                showGameError();
                return;
            }

            // save room ID so we can leave cleanly before the game starts.
            mRoomId = room.getRoomId();

            // show the waiting room UI
            showWaitingRoom(room);
        }

        // Called when room is fully connected.
        @Override
        public void onRoomConnected(int statusCode, Room room) {
            if (LogUtil.islogOn()) {
                Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
            }
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                showGameError();
                return;
            }
            updateRoom(room);
        }

        @Override
        public void onJoinedRoom(int statusCode, Room room) {
            if (LogUtil.islogOn()) {
                Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
            }
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                showGameError();
                return;
            }

            // show the waiting room UI
            showWaitingRoom(room);
        }

        // Called when we've successfully left the room (this happens a result of voluntarily leaving
        // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
        @Override
        public void onLeftRoom(int statusCode, @NonNull String roomId) {
            // we have left the room; return to main screen.
            if (LogUtil.islogOn()) {
                Log.d(TAG, "onLeftRoom, code " + statusCode);
            }
            switchToMainScreen();
        }
    };

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        if (mParticipants != null) {
//            updatePeerScoresDisplay();
        }
    }

    /*
     * GAME LOGIC SECTION. Methods that implement the game's rules.
     */

    // Current state of the game:
    int mSecondsLeft = -1; // how long until the game ends (seconds)
    final static int GAME_DURATION = 40; // game duration, seconds.
    int mScore = 0; // user's current score

    // Reset game variables in preparation for a new game.
    void resetGameVars() {
        mSecondsLeft = GAME_DURATION;
        mScore = 0;
        mParticipantScore.clear();
        mFinishedParticipants.clear();
    }
/*
    // Start the gameplay phase of the game.
    void startGame(boolean multiplayer) {
        mMultiplayer = multiplayer;
        updateScoreDisplay();
        broadcastScore(false);
        switchToScreen(R.id.screen_gameplay);

//        findViewById(R.id.button_click_me).setVisibility(View.VISIBLE);

        // run the gameTick() method every second to update the game.
        /*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSecondsLeft <= 0) {
                    return;
                }
                //                gameTick();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
*/
    // Game tick -- update countdown, check if game ended.
//    void gameTick() {
//        if (mSecondsLeft > 0) {
//            --mSecondsLeft;
//        }
//
//        // update countdown
//        ((TextView) findViewById(R.id.countdown)).setText("0:" +
//                (mSecondsLeft < 10 ? "0" : "") + String.valueOf(mSecondsLeft));
//
//        if (mSecondsLeft <= 0) {
//            // finish game
//            findViewById(R.id.button_click_me).setVisibility(View.GONE);
//            broadcastScore(true);
//        }
//    }

    /* indicates the player scored one point
    void scoreOnePoint() {
        if (mSecondsLeft <= 0) {
            return; // too late!
        }
        ++mScore;
        updateScoreDisplay();
//        updatePeerScoresDisplay();

        // broadcast our new score to our peers
        broadcastScore(false);
    }
    */

    /*
     * COMMUNICATIONS SECTION. Methods that implement the game's network
     * protocol.
     */

    // Score of other participants. We update this as we receive their scores
    // from the network.
    Map<String, Integer> mParticipantScore = new HashMap<>();

    // Participants who sent us their final score.
    Set<String> mFinishedParticipants = new HashSet<>();

    // Called when we receive a real-time message from the network.
    // Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
    // indicating
    // whether it's a final or interim score. The second byte is the score.
    // There is also the
    // 'S' message, which indicates that the game should start.
//    OnRealTimeMessageReceivedListener mOnRealTimeMessageReceivedListener = new OnRealTimeMessageReceivedListener() {
//        @Override
//        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
//            byte[] buf = realTimeMessage.getMessageData();
//            String sender = realTimeMessage.getSenderParticipantId();
//            Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);
//
//            if (buf[0] == 'F' || buf[0] == 'U') {
//                // score update.
//                int existingScore = mParticipantScore.containsKey(sender) ?
//                        mParticipantScore.get(sender) : 0;
//                int thisScore = (int) buf[1];
//                if (thisScore > existingScore) {
//                    // this check is necessary because packets may arrive out of
//                    // order, so we
//                    // should only ever consider the highest score we received, as
//                    // we know in our
//                    // game there is no way to lose points. If there was a way to
//                    // lose points,
//                    // we'd have to add a "serial number" to the packet.
//                    mParticipantScore.put(sender, thisScore);
//                }
//
//                // update the scores on the screen
////                updatePeerScoresDisplay();
//
//                // if it's a final score, mark this participant as having finished
//                // the game
//                if ((char) buf[0] == 'F') {
//                    mFinishedParticipants.add(realTimeMessage.getSenderParticipantId());
//                }
//            }
//        }
//    };

    /*
    // Broadcast my score to everybody else.
    void broadcastScore(boolean finalScore) {
        if (!mMultiplayer) {
            // playing single-player mode
            return;
        }

        // First byte in message indicates whether it's a final score or not
        mMsgBuf[0] = (byte) (finalScore ? 'F' : 'U');

        // Second byte is the score.
        mMsgBuf[1] = (byte) mScore;

        // Send to every other participant.
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            if (finalScore) {
                // final score notification must be sent via reliable message
                mRealTimeMultiplayerClient.sendReliableMessage(mMsgBuf,
                        mRoomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                            @Override
                            public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                                if (LogUtil.islogOn()) {
                                    Log.d(TAG, "RealTime message sent");
                                    Log.d(TAG, "  statusCode: " + statusCode);
                                    Log.d(TAG, "  tokenId: " + tokenId);
                                    Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                                }
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Integer>() {
                            @Override
                            public void onSuccess(Integer tokenId) {
                                if (LogUtil.islogOn()) {
                                    Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                                }
                            }
                        });
            } else {
                // it's an interim score notification, so we can use unreliable
                mRealTimeMultiplayerClient.sendUnreliableMessage(mMsgBuf, mRoomId,
                        p.getParticipantId());
            }
        }
    }
    */
    /*
     * UI SECTION. Methods that implement the game's UI.
     */

    // This array lists everything that's clickable, so we can install click
    // event handlers.
    final static int[] CLICKABLES = {
            R.id.button_accept_popup_invitation, R.id.btn_invite_players,
            R.id.btn_quick_game, R.id.btn_see_invitations, R.id.button_sign_in,
            R.id.btn_sign_out,
//            R.id.button_click_me,
            R.id.button_single_player,
            R.id.button_single_player_2,
    };

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.screen_gameplay, R.id.screen_main, R.id.screen_sign_in,
            R.id.screen_wait
    };
    int mCurScreen = -1;

    void switchToScreen(int screenId) {
        if (LogUtil.islogOn()) {
            Log.d(TAG,"SWITCH TO MAIN SCREEN");
        }
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurScreen = screenId;

        // should we show the invitation popup?
        boolean showInvPopup;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else if (mMultiplayer) {
            // if in multiplayer, only show invitation on main screen
            showInvPopup = (mCurScreen == R.id.screen_main);
        } else {
            // single-player: show on main screen and gameplay screen
            showInvPopup = (mCurScreen == R.id.screen_main || mCurScreen == R.id.screen_gameplay);
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    void switchToMainScreen() {
        if (mRealTimeMultiplayerClient != null) {
            if (LogUtil.islogOn()) {
                Log.d(TAG,"onScreen1 : ");
            }
            switchToScreen(R.id.screen_main);
        } else {
            if (LogUtil.islogOn()) {
                Log.d(TAG,"onScreen2 : ");
            }
            switchToScreen(R.id.screen_sign_in);
        }
    }

    // updates the label that shows my score
    void updateScoreDisplay() {
//        ((TextView) findViewById(R.id.my_score)).setText(formatScore(mScore));
    }

    // formats a score as a three-digit number
    String formatScore(int i) {
        if (i < 0) {
            i = 0;
        }
        String s = String.valueOf(i);
        return s.length() == 1 ? "00" + s : s.length() == 2 ? "0" + s : s;
    }

    // updates the screen with the scores from our peers
//    void updatePeerScoresDisplay() {
//        ((TextView) findViewById(R.id.score0)).setText(
//                getString(R.string.score_label, formatScore(mScore)));
//        int[] arr = {
//                R.id.score1, R.id.score2, R.id.score3
//        };
//        int i = 0;
//
//        if (mRoomId != null) {
//            for (Participant p : mParticipants) {
//                String pid = p.getParticipantId();
//                if (pid.equals(mMyId)) {
//                    continue;
//                }
//                if (p.getStatus() != Participant.STATUS_JOINED) {
//                    continue;
//                }
//                int score = mParticipantScore.containsKey(pid) ? mParticipantScore.get(pid) : 0;
//                ((TextView) findViewById(arr[i])).setText(formatScore(score) + " - " +
//                        p.getDisplayName());
//                ++i;
//            }
//        }
//
//        for (; i < arr.length; ++i) {
//            ((TextView) findViewById(arr[i])).setText("");
//        }
//    }

    /*
     * MISC SECTION. Miscellaneous methods.
     */


    // Sets the flag to keep this screen on. It's recommended to do that during
    // the
    // handshake when setting up a game, because if the screen turns off, the
    // game will be
    // cancelled.
    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }






    // REWARD VIDEO

    LazyAds lazyAds;

    private final int REWARDED_COINS = 2;
    private final int MAX_REWARD_COINS = 10;
    private final int MIN_REWARD_COINS = 0;

    int totalRewardedCoins = 0;
    boolean isRewarded;

    private ImageButton mShowVideoButton;



    private void loadRewardViews(){
        isRewarded =false ;
        totalRewardedCoins=0;

        // Initialize the Mobile Ads SDK.



        lazyAds = LazyAds.getInstance(getApplicationContext());
        lazyAds.initializeInterface(this);


        // Create the "show" button, which shows a rewarded video if one is loaded.
        mShowVideoButton = findViewById(R.id.video);
        mShowVideoButton.setEnabled(lazyAds.isButtonEnabled());
        mShowVideoButton.setVisibility(lazyAds.isButtonEnabled()?View.VISIBLE:View.INVISIBLE);

        mShowVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lazyAds.showRewardedVideo();
            }
        });


    }


    @Override
    public void addCoins() {
        totalRewardedCoins += REWARDED_COINS;
        totalRewardedCoins = Math.min(totalRewardedCoins, MAX_REWARD_COINS);

        MyPreferences myPreferences = new MyPreferences();
        myPreferences.saveRewardedCoins(this,totalRewardedCoins);
    }

    private void deductCoins(){
        totalRewardedCoins -= REWARDED_COINS;
        totalRewardedCoins = Math.max(totalRewardedCoins, MIN_REWARD_COINS);

        MyPreferences myPreferences = new MyPreferences();
        myPreferences.saveRewardedCoins(this,totalRewardedCoins);
    }

    @Override
    public void enableButton() {
        mShowVideoButton.setEnabled(true);
        mShowVideoButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void disableButton() {
        mShowVideoButton.setEnabled(false);
        mShowVideoButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        removeFromMemory();
        super.onDestroy();
    }

    private void removeFromMemory() {

        releaseSound();

        if(lazyAds!=null) {
            lazyAds.removeInterface();
        }
        if(moveTimer!=null) {
            moveTimer.cancel();
            moveTimer = null;
        }
        if(tvBid1!=null) {
            tvBid1.setOnClickListener(null);
        }
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }

    }


    @Override
    public void onRewardedAndVideoAdClosed() {
        String s = "Rewarded! : " + REWARDED_COINS + " COINS";
        customSystemToast(s, Toast.LENGTH_SHORT);
    }



    //----------------------------NETWORK CHECK----------------------------------------
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        }
        catch (Exception e) {
            return true;
        }
    }

    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            if (LogUtil.islogOn()) {
                Log.d(TAG, "No network available!");
            }
        }
        return false;
    }

    class InputTextFilter extends NumberKeyListener {

        /**
         * The numbers accepted by the input text's {@link android.view.LayoutInflater.Filter}
         */
        private final char[] DIGIT_CHARACTERS = new char[] {
                // Latin digits are the common case
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                // Arabic-Indic
                '\u0660', '\u0661', '\u0662', '\u0663', '\u0664', '\u0665', '\u0666', '\u0667', '\u0668'
                , '\u0669',
                // Extended Arabic-Indic
                '\u06f0', '\u06f1', '\u06f2', '\u06f3', '\u06f4', '\u06f5', '\u06f6', '\u06f7', '\u06f8'
                , '\u06f9',
                // Hindi and Marathi (Devanagari script)
                '\u0966', '\u0967', '\u0968', '\u0969', '\u096a', '\u096b', '\u096c', '\u096d', '\u096e'
                , '\u096f',
                // Bengali
                '\u09e6', '\u09e7', '\u09e8', '\u09e9', '\u09ea', '\u09eb', '\u09ec', '\u09ed', '\u09ee'
                , '\u09ef',
                // Kannada
                '\u0ce6', '\u0ce7', '\u0ce8', '\u0ce9', '\u0cea', '\u0ceb', '\u0cec', '\u0ced', '\u0cee'
                , '\u0cef'
        };

        // XXX This doesn't allow for range limits when controlled by a
        // soft input method!
        public int getInputType() {
            return InputType.TYPE_CLASS_TEXT;
        }

        @Override
        protected char[] getAcceptedChars() {
            return DIGIT_CHARACTERS;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Log.v("filter", "source:" + source.toString());

            CharSequence filtered = String.valueOf(source.subSequence(start, end));

            Log.v("filter", "filtered:" + filtered.toString());
            if (TextUtils.isEmpty(filtered)) {
                return "";
            }
            String result = String.valueOf(dest.subSequence(0, dstart)) + filtered
                    + dest.subSequence(dend, dest.length());
            String str = String.valueOf(result).toLowerCase();
            try{
                int value = Integer.parseInt(str);

                if(1 <= value && value <= 12) {
                    return source;
                }
            } catch(NumberFormatException e) {
                //continue with the checking
            }

            for (String val : np.getDisplayedValues()) {
                String valLowerCase = val.toLowerCase();
                if (valLowerCase.startsWith(str)) {
                    final int selstart = result.length();
                    final int selend = val.length();
                    mInputText.post(new Runnable() {
                        @Override
                        public void run() {
                            mInputText.setSelection(selstart, selend);
                        }
                    });
                    return val.subSequence(dstart, val.length());
                }
            }
            return "";

        }
    }


}
