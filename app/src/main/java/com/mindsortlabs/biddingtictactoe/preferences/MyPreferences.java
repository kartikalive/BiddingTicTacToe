package com.mindsortlabs.biddingtictactoe.preferences;


import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {

    String MyPrefName = "MyPref";
    String userTotalCoinsString =  "myTotalCoins";
    String cpuTotalCoinsString = "cpuTotalCoins";
    String levelString = "levels";
    String extraRewardCoinsString ="rewardedCoins";

    String userStatsPref = "userStatsPref";
    String userNickname = "userNickname";
    String userRank = "userRank";
    String userWin = "userWin";
    String userDraw = "userDraw";
    String userLoss = "userLoss";

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

    public void saveNicknamePreference(Context context, String nickname){
        SharedPreferences pref = context.getSharedPreferences(userStatsPref, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(userNickname, nickname);
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
        // win - 1, draw = 2, loss = 3
        if(status==1){
            int currentWinStats = Integer.parseInt(getUserWin(context));
            currentWinStats++;
            editor.putString(userWin,String.valueOf(currentWinStats));
        }

        if(status==2){
            int currentDrawStats = Integer.parseInt(getUserDraw(context));
            currentDrawStats++;
            editor.putString(userDraw,String.valueOf(currentDrawStats));
        }

        if(status==3){
            int currentLossStats = Integer.parseInt(getUserLoss(context));
            currentLossStats++;
            editor.putString(userWin,String.valueOf(currentLossStats));
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



}
