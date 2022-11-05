package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.thesis.myapplication.R;

public class Splashscreen extends AppCompatActivity {

    //Duration for the Splashscreen
    private static int SPLASH_SCREEN = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this, Login.class);

            }
        }, SPLASH_SCREEN);
    }
}