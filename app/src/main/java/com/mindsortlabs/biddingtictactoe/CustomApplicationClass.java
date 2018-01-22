package com.mindsortlabs.biddingtictactoe;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class CustomApplicationClass extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Required initialization logic here!
        MultiDex.install(this);
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }
}
