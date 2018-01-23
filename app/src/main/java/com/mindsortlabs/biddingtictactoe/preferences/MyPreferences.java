package com.mindsortlabs.biddingtictactoe.preferences;


import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {

    String MyPrefName = "MyPref";
    String userTotalCoinsString =  "myTotalCoins";
    String cpuTotalCoinsString = "cpuTotalCoins";
    String levelString = "levels";
    String extraRewardCoinsString ="rewardedCoins";
    String gamePlayedString = "gamePlayed";
    String tutorialStatusString = "tutorial";

    public static final int SHOW_ADS_AFTER_PLAYED_GAMES = 6;


    String userStatsPref = "userStatsPref";
    String userNickname = "userNickname";
    String userRank = "userRank";
    String userWin = "userWin";
    String userDraw = "userDraw";
    String userLoss = "userLoss";
    String leaderBoardScore = "leaderBoardScore";

    public void saveMyandCPUTotalBid(Context context, int userTotalCoins, int cpuTotalCoins)
    {

        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(userTotalCoinsString, userTotalCoins );
        editor.putInt(cpuTotalCoinsString,cpuTotalCoins);
        editor.commit();
    }

    public void saveLevel(Context context, int level) {
        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(levelString, level );
        editor.commit();
    }

    public  int getUserTotalCoins(Context context){
        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        return pref.getInt(userTotalCoinsString, 100); // getting Integer
    }

    public  int getcpuTotalCoins(Context context){
        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        return pref.getInt(cpuTotalCoinsString, 100); // getting Integer
    }

    public  int getlevel(Context context){
        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        return pref.getInt(levelString, 100); // getting Integer
    }

    public void saveRewardedCoins(Context context, int extrareward) {
        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(extraRewardCoinsString, extrareward );
        editor.commit();
    }

    public  int getRewardedCoins(Context context){
        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        return pref.getInt(extraRewardCoinsString, 0); // getting Integer
    }


    public void saveGamePlayed(Context context, int gamePlayed) {
        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(gamePlayedString, gamePlayed );
        editor.commit();
    }

    public  int getGamePlayed(Context context){
        SharedPreferences pref = context.getSharedPreferences(MyPrefName, 0); // 0 - for private mode
        return pref.getInt(gamePlayedString, 0); // getting Integer
    }

    public void saveNicknamePreference(Context context, String nickname){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(userNickname, nickname);
        editor.apply();
    }

    public String getLeaderboardScore(Context context){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        return pref.getString(leaderBoardScore, "1000"); // getting Integer
    }

    public void saveLeaderboardScore(Context context, String score){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(leaderBoardScore, score);
        editor.apply();
    }

    public void saveCurrentRank(Context context, String rank){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(userRank, rank);
        editor.apply();
    }

    public void saveGameStats(Context context, int status){

        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        //Status values:
        // win = 0, draw = 2, loss = 1
        if(status==0){
            int currentWinStats = Integer.parseInt(getUserWin(context));
            currentWinStats++;
            editor.putString(userWin,String.valueOf(currentWinStats));
        }

        if(status==2){
            int currentDrawStats = Integer.parseInt(getUserDraw(context));
            currentDrawStats++;
            editor.putString(userDraw,String.valueOf(currentDrawStats));
        }

        if(status==1){
            int currentLossStats = Integer.parseInt(getUserLoss(context));
            currentLossStats++;
            editor.putString(userLoss,String.valueOf(currentLossStats));
        }
        editor.apply();
    }

    public String getNickname(Context context){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        return pref.getString(userNickname, "Guest"); // getting Integer
    }

    public String getCurrentRank(Context context){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        return pref.getString(userRank, "NA"); // getting Integer
    }

    public String getUserWin(Context context){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        return pref.getString(userWin, "0"); // getting Integer
    }

    public String getUserDraw(Context context){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        return pref.getString(userDraw, "0"); // getting Integer
    }

    public String getUserLoss(Context context){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        return pref.getString(userLoss, "0"); // getting Integer
    }

    public int getTutorialStatus(Context context){
        //0 = not seen, 1 = seen.
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        return pref.getInt(tutorialStatusString, 0); // getting Integer
    }

    public void saveTutorialStatus(Context context, int status){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(tutorialStatusString, status);
        editor.apply();
    }
}
