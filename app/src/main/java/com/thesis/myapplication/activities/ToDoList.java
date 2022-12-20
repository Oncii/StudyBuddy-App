package com.thesis.myapplication.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.thesis.myapplication.adapters.ToDoAdapter;
import com.thesis.myapplication.databinding.ActivityToDoListBinding;
import com.thesis.myapplication.listeners.OnDialogCloseListener;
import com.thesis.myapplication.models.ToDoModel;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToDoList extends AppCompatActivity implements OnDialogCloseListener {

    private ActivityToDoListBinding binding;
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private List<ToDoModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToDoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        showData();

        firestore = FirebaseFirestore.getInstance();

        binding.rvContainer.setHasFixedSize(true);
        binding.rvContainer.setLayoutManager(new LinearLayoutManager(ToDoList.this));

        mList = new ArrayList<>();
        adapter = new ToDoAdapter(ToDoList.this, mList);
        binding.rvContainer.setAdapter(adapter);
        binding.rvContainer.setVisibility(View.VISIBLE);

    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view -> onBackPressed());

        binding.addNoteButton.setOnClickListener(view ->
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));

        /**
         binding.addNoteButton.setOnClickListener(view ->
         startActivity(new Intent(getApplicationContext(), NoteDetails.class)));
         */
    }

    private void showData() {
        firestore = FirebaseFirestore.getInstance();
        query = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);

                        mList.add(toDoModel);
                        adapter.notifyDataSetChanged();
                        binding.progressBar.setVisibility(View.GONE);
                    } else if (mList.size() < 1) {
                        errorMessage();
                    } else {
                        Collections.reverse(mList);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    private void errorMessage() {
        binding.errorText.setText((String.format("%s", "NO NOTES YET")));
        binding.errorText.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}