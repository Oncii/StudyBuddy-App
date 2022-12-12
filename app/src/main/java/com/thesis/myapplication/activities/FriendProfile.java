package com.thesis.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityFriendProfileBinding;
import com.thesis.myapplication.models.User;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

public class FriendProfile extends AppCompatActivity {

    private ActivityFriendProfileBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        setListeners();
    }

    private void setListeners(){
        binding.backButton.setOnClickListener(view ->
                onBackPressed());
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.friendFullName.setText(receiverUser.name);
        binding.friendUsername.setText(receiverUser.username);
        binding.friendEmail.setText(receiverUser.email);
        binding.friendProfilePic.setImageBitmap(getUserImage(receiverUser.image));
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}