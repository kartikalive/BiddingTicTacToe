package com.mindsortlabs.biddingtictactoe.ads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/**
 * Using Google's Ad-mob Reward Video to load Ads in the app
 * Implemented as a singleton class having one instance from app start to end
 * Create an instance as soon as possible for ad loading in starting activity
 */
public class LazyAds implements RewardedVideoAdListener {

    /**
     *  AdMob ID for ads unique for each app. Use testing id's for development purposes
     */
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";

    /**
     * Single Instance used in app lifetime
     */
    private static LazyAds instance;

    /**
     * To check if player has successfully watched the reward video
     */
    private static boolean isRewarded;

    private static RewardedVideoAd mRewardedVideoAd = null;

    /**
     * Interface used for calling callback methods in activities according
     * RewardedVideoAd lifecycle methods
     */
    private Implementable imp = null;
    private static Context context = null ;
    /**
     * Signifies the state of button shown by enabling or disabling button
     * according to if RewardVideoAd is loaded or not
     */
    private boolean isButtonEnabled = false;

    private LazyAds() {
    }


    public static LazyAds getInstance(Context c) {

        if (instance == null) {
            instance = new LazyAds();
            context = c ;
            Log.d(LazyAds.class.getSimpleName(),"mReward NULL");
            // Initialize the Mobile Ads SDK.
            MobileAds.initialize(context, APP_ID);
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            mRewardedVideoAd.setRewardedVideoAdListener(instance);
        }
        isRewarded = false;

        loadRewardedVideoAd();
        return instance;
    }

    private static void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }

    public void initializeInterface(Implementable i) {
        imp = i;
    }
    public void removeInterface() {
        imp = null;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Preload the next video ad.
        if (isRewarded) {
            if (imp != null) {
                imp.onRewardedAndVideoAdClosed();
            }
            isRewarded = false;
        }
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if (imp != null) {
            imp.enableButton();
        }
        isButtonEnabled = true;
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewarded(RewardItem reward) {
        isRewarded = true;
        if (imp != null) {
            imp.addCoins();
        }
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    public void onPause() {
        mRewardedVideoAd.pause(context);
    }

    public void onResume() {
        mRewardedVideoAd.resume(context);
    }

    public void showRewardedVideo() {
        if (imp != null) {
            imp.disableButton();
        }
        isButtonEnabled = false;
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    public boolean isButtonEnabled() {
        return isButtonEnabled;
    }

    public void onDestroy() {
        if(mRewardedVideoAd!=null) {
            mRewardedVideoAd.destroy(context);
            mRewardedVideoAd.setRewardedVideoAdListener(null);
        }
        mRewardedVideoAd  = null;
        imp = null;
        instance = null;
        context = null;
    }

    /**
     * Interface working as callback in activities in which Ads are implemented
     */
    public interface Implementable {
        /**
         * Enables Ad Showing Button
         */
        void enableButton();

        /**
         * Disables Ad Showing Button
         */
        void disableButton();

        /**
         * On Successfully Ad(Video) is completed rewarded material(here coins) is added to user
         * Called when Ad video is completed
         */
        void addCoins();

        /**
         * On Successfully Ad(Video) completed and closed
         * Called after addCoins when gameScreen resumes from Ad
         * To show custom toast or inform user that reward successful
         */
        void onRewardedAndVideoAdClosed();
    }

}