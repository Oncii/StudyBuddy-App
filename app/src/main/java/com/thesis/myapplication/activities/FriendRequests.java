package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thesis.myapplication.databinding.ActivityFriendProfileBinding;
import com.thesis.myapplication.databinding.ActivityFriendRequestsBinding;

public class FriendRequests extends AppCompatActivity {

    private ActivityFriendRequestsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view ->
                onBackPressed());
    }
}