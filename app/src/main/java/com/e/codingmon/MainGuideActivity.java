package com.e.codingmon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainGuideActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_guide);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcomeIntent = new Intent(MainGuideActivity.this, MainActivity.class);
                startActivity(welcomeIntent);
                finish();

                startActivity(new Intent(MainGuideActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

            }
        }, SPLASH_TIME_OUT);
    }
}
