package com.mindsortlabs.biddingtictactoe;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;


public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView imageView= (ImageView)findViewById(R.id.imageView);
        imageView.animate().alpha(1f).setDuration(1500);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                //Intent i = new Intent(SplashScreen.this, StartActivity.class);
                //startActivity(i);
                Intent myIntent = new Intent(SplashScreen.this, StartActivity.class);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(SplashScreen.this, R.anim.fade_in, R.anim.fade_out);
                startActivity(myIntent, options.toBundle());
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
