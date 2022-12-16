package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thesis.myapplication.databinding.ActivityToDoListBinding;

public class ToDoList extends AppCompatActivity {

    private ActivityToDoListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToDoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

        binding.rvContainer.setHasFixedSize(true);
        binding.rvContainer.setLayoutManager(new LinearLayoutManager(ToDoList.this));

    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
        binding.addNoteButton.setOnClickListener(view ->
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));
    }
}