package com.example.calorietracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScrenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scren);

        Timer timer=new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                    startActivity(new Intent(SplashScrenActivity.this,LoginActivity.class));
                    finish();
            }
        },2500);

    }
}