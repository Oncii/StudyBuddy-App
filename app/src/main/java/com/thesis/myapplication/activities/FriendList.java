package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thesis.myapplication.databinding.ActivityChatBinding;
import com.thesis.myapplication.databinding.ActivityFriendListBinding;

public class FriendList extends AppCompatActivity {

    private ActivityFriendListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFriendListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
    }
}