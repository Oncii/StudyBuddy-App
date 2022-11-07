package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityChatBinding;
import com.thesis.myapplication.models.User;
import com.thesis.myapplication.utilities.Constants;

public class Chat extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
    }

    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.userNameText.setText(receiverUser.name);
    }

    private void setListeners(){
        binding.backButton.setOnClickListener(view -> onBackPressed());
    }
}