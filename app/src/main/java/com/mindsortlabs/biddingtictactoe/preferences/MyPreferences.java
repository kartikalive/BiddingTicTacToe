package com.mindsortlabs.biddingtictactoe.preferences;


import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {

    String MyPrefName = "MyPref";
    String userTotalCoinsString =  "myTotalCoins";
    String cpuTotalCoinsString = "cpuTotalCoins";
    String levelString = "levels";
    String extraRewardCoinsString ="rewardedCoins";

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

}
