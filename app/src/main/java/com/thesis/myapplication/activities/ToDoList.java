package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thesis.myapplication.databinding.ActivityToDoListBinding;

public class ToDoList extends AppCompatActivity {

    private ActivityToDoListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToDoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}