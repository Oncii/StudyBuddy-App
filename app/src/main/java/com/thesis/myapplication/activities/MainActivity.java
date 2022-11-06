package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;

import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityLoginBinding;
import com.thesis.myapplication.databinding.ActivityMainBinding;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();

    }

    private void setListeners(){
        binding.settingsButton.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), Settings.class)));
    }

    private void loadUserDetails() {
        binding.userName.setText(preferenceManager.getString(Constants.KEY_USERNAME));
    }
}