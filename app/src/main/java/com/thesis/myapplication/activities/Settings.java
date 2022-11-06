package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.thesis.myapplication.databinding.ActivityMainBinding;
import com.thesis.myapplication.databinding.ActivitySettingsBinding;

public class Settings extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.backButton.setOnClickListener(view -> onBackPressed());
        binding.userProfileButton.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), UserProfile.class)));
    }
}