package com.thesis.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.thesis.myapplication.R;
import com.thesis.myapplication.adapters.UserAdapter;
import com.thesis.myapplication.databinding.ActivityFriendsBinding;
import com.thesis.myapplication.listeners.UserListener;
import com.thesis.myapplication.models.User;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class Friends extends AppCompatActivity implements UserListener {

    private ActivityFriendsBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager((getApplicationContext()));
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            if (queryDocumentSnapshot.getString(Constants.KEY_IMAGE) != null) {
                                user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            } else {
                                ImageView user_p;
                                user_p = findViewById(R.id.profilePic);
                                user_p.setImageResource(R.drawable.sb_logo_only);
                            }
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UserAdapter userAdapter = new UserAdapter(users, this);
                            binding.rvContainer.setAdapter(userAdapter);
                            binding.rvContainer.setVisibility(View.VISIBLE);
                        } else {
                            errorMessage();
                        }
                    } else {
                        errorMessage();
                    }
                });
    }

    private void errorMessage() {
        binding.errorText.setText((String.format("%s", "NO FRIENDS YET")));
        binding.errorText.setVisibility(View.VISIBLE);

    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),Chat.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}