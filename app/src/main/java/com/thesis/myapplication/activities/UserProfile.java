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
import com.thesis.myapplication.databinding.ActivityUserProfileBinding;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

public class UserProfile extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
        loadUserDetails();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
        binding.editButton.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), EditProfile.class)));
    }

    private void loadUserDetails() {
        binding.userName.setText(preferenceManager.getString(Constants.KEY_USERNAME));
        binding.fullName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.userEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.userBio.setText(preferenceManager.getString(Constants.KEY_USER_BIO));
        binding.userPassword.setText(preferenceManager.getString(Constants.KEY_PASSWORD));

        if(preferenceManager.getString(Constants.KEY_IMAGE) != null){
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.userProfilePic.setImageBitmap(bitmap);
            binding.userProfilePic.setCornerRadius(30);
        }else{
            RoundedImageView user_p;
            user_p = findViewById(R.id.userProfilePic);
            user_p.setImageResource(R.drawable.sb_logo_only);
        }

    }
}