package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityLoginBinding;
import com.thesis.myapplication.databinding.ActivitySplashscreenBinding;

public class Splashscreen extends AppCompatActivity {

    //Duration for the Splashscreen
    private static int SPLASH_SCREEN = 1800;

    private ActivitySplashscreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this, Login.class);
                startActivity(intent);
            }
        }, SPLASH_SCREEN);
    }
}