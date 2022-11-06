package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thesis.myapplication.databinding.ActivityMainBinding;
import com.thesis.myapplication.databinding.ActivityUserProfileBinding;

public class UserProfile extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}