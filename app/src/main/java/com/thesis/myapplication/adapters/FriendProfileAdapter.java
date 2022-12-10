package com.thesis.myapplication.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.thesis.myapplication.activities.FriendProfile;
import com.thesis.myapplication.databinding.ActivityFriendProfileBinding;
import com.thesis.myapplication.listeners.FriendListener;
import com.thesis.myapplication.models.User;

public class FriendProfileAdapter extends FriendProfile {

    private final User friends;
    private final FriendListener friendListener;

    public FriendProfileAdapter(User friends, FriendListener friendListener) {
        this.friends = friends;
        this.friendListener = friendListener;
        setFriendData(friends);
    }

    ActivityFriendProfileBinding binding;

    void setFriendData(User friends){
        binding.friendBio.setText(friends.bio);
        binding.friendFullName.setText(friends.name);
        binding.friendUsername.setText(friends.username);
        binding.friendEmail.setText(friends.email);
        binding.friendProfilePic.setImageBitmap(getUserImage(friends.image));
        binding.getRoot().setOnClickListener(view -> friendListener.onFriendClicked(friends));
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
