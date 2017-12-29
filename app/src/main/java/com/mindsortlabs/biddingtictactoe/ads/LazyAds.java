package com.mindsortlabs.biddingtictactoe.ads;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class LazyAds implements RewardedVideoAdListener {

    private static LazyAds instance;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";

    private static boolean isRewarded;
    private Implementable imp = null;
    boolean isButtonEnabled=false;

    private static RewardedVideoAd mRewardedVideoAd = null;

    private LazyAds(){}

    public static LazyAds getInstance(Context context){
        if(mRewardedVideoAd!=null&&mRewardedVideoAd.isLoaded()){
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        }
        if(instance == null){
            instance = new LazyAds();
            MobileAds.initialize(context, APP_ID);
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            mRewardedVideoAd.setRewardedVideoAdListener(instance);
        }
        isRewarded =false;

        loadRewardedVideoAd();
        return instance;
    }

    public void initializeInterface(Implementable i){
        imp = i;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Preload the next video ad.
        if(isRewarded){
            if(imp!=null) {
                imp.onRewardedAndVideoAdClosed();
            }
            isRewarded=false;
        }
        //Log.d("REWARDED ","OK " + userAndCpuTotalCoins);
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if(imp!=null) {
            imp.enableButton();
        }
        isButtonEnabled=true;
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewarded(RewardItem reward) {
        isRewarded=true;
        if(imp!=null) {
            imp.addCoins();
        }
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    public void onPause(Context context) {
        mRewardedVideoAd.pause(context);
    }

    public void onResume(Context context) {
        mRewardedVideoAd.resume(context);
    }

    private static void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }

    public void showRewardedVideo() {
        if(imp!=null) {
            imp.disableButton();
        }
        isButtonEnabled=false;
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    public boolean isButtonEnabled() {
        return isButtonEnabled;
    }

    public void onDestroy(Context context) {
        mRewardedVideoAd.destroy(context);
    }


    public interface Implementable{
        void enableButton();
        void disableButton();
        void addCoins();
        void onRewardedAndVideoAdClosed();
    }

}
