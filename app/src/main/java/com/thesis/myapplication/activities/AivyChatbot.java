package com.thesis.myapplication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thesis.myapplication.databinding.ActivityAivyChatbotBinding;


public class AivyChatbot extends AppCompatActivity {

    private ActivityAivyChatbotBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAivyChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
    }

    private void setListener() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
    }

}