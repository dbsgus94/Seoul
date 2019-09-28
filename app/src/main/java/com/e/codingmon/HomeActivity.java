package com.e.codingmon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class HomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //참고한 사이트: https://stackoverflow.com/questions/4298893/android-how-do-i-create-a-function-that-will-be-executed-only-once
        SharedPreferences homesettings = getSharedPreferences("homesettings", 0);
        final boolean firstStart = homesettings.getBoolean("firstStart", true);
        final SharedPreferences.Editor homesettingsEditor = homesettings.edit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firstStart) {
                    Intent welcomeIntent = new Intent(HomeActivity.this, MainGuideActivity.class);
                    startActivity(welcomeIntent);
                    finish();

                    homesettingsEditor.putBoolean("firstStart", false);
                    homesettingsEditor.commit();
                } else {
                    Intent welcomeIntent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(welcomeIntent);
                    finish();
                }
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }
        }, SPLASH_TIME_OUT);

    }
}
