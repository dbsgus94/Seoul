package com.e.codingmon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(WelcomeActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();

                //startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                /*
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                overridePendingTransition(R.anim.anim_left_out, R.anim.anim_right_in);
                finish();
                */
            }
        }, SPLASH_TIME_OUT);
    }
}

