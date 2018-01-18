package com.mindsortlabs.biddingtictactoe;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by kartik1 on 17-01-2018.
 */

public class CustomApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        MultiDex.install(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
