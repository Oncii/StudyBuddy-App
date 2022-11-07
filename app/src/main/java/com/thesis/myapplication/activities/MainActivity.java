package com.thesis.myapplication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityMainBinding;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

import org.checkerframework.checker.units.qual.C;

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
        getToken();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setListeners() {
        binding.settingsButton.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), Settings.class)));
        binding.friendsButton.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), Friends.class)));
    }

    private void loadUserDetails() {
        binding.userName.setText(preferenceManager.getString(Constants.KEY_USERNAME));

        if(preferenceManager.getString(Constants.KEY_IMAGE) != null){
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.userProfile.setImageBitmap(bitmap);
            binding.userProfile.setCornerRadius(30);
        }else{
            RoundedImageView user_p;
            user_p = findViewById(R.id.userProfile);
            user_p.setImageResource(R.drawable.sb_logo_only);
        }
    }


    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Token update failed"));
    }
}