package com.thesis.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.thesis.myapplication.databinding.ActivitySignUpBinding;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.signupCancelButton.setOnClickListener(view -> onBackPressed());
        binding.signupButton.setOnClickListener(view -> {
            if (isValidSignUpDetails()) {
                signup();
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signup() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.signupName.getText().toString());
        user.put(Constants.KEY_USERNAME, binding.signupUsername.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.signupEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.signupPassword.getText().toString());
        database.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.signupName.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_USERNAME, binding.signupUsername.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.signupEmail.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_PASSWORD, binding.signupPassword.getText().toString().trim());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private Boolean isValidSignUpDetails() {
        if (binding.signupName.getText().toString().trim().isEmpty()) {
            showToast("Please input your full name!");
            return false;
        } else if (binding.signupUsername.getText().toString().trim().isEmpty()) {
            showToast("Please input your username!");
            return false;
        } else if (binding.signupEmail.getText().toString().trim().isEmpty()) {
            showToast("Please input your email!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.signupEmail.getText().toString()).matches()) {
            showToast("Please input a valid email!");
            return false;
        } else if (binding.signupPassword.getText().toString().trim().isEmpty()) {
            showToast("Please input your password!");
            return false;
        } else if (binding.signupConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Please confirm your password");
            return false;
        } else if (!binding.signupPassword.getText().toString().equals(binding.signupConfirmPassword.getText().toString())) {
            showToast("The two passwords didn't matched!");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.signupButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.signupButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}