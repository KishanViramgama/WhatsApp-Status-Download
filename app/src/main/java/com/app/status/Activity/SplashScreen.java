package com.app.status.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.app.status.R;
import com.app.status.Util.Method;


public class SplashScreen extends AppCompatActivity {

    private Method method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash_screen);

        method = new Method(SplashScreen.this);
        method.forceRTLIfSupported();
        method.changeStatusBarColor();

        // splash screen timer
        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                if (method.isAppWA() && method.isAppWB()) {
                    method.editor.putString(method.pref_link, "wball");
                    method.editor.commit();
                } else if (method.isAppWA()) {
                    method.editor.putString(method.pref_link, "w");
                    method.editor.commit();
                } else if (method.isAppWB()) {
                    method.editor.putString(method.pref_link, "wb");
                    method.editor.commit();
                }

                startActivity(new Intent(SplashScreen.this, WelcomeActivity.class));
                finishAffinity();

            }
        }, SPLASH_TIME_OUT);

    }

}
