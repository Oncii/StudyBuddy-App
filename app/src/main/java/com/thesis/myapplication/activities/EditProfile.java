package com.thesis.myapplication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityEditProfileBinding;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class EditProfile extends BaseActivity {

    private ActivityEditProfileBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        loadUserDetails();
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
        binding.editCancelButton.setOnClickListener(view -> onBackPressed());
        binding.profileFrame.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.updateButton.setOnClickListener(view -> {
            if (isValidUpdateDetails()) {
                updateUserInfo();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateUserInfo() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_USER_BIO, binding.editBio.getText().toString());
        user.put(Constants.KEY_NAME, binding.editName.getText().toString());
        user.put(Constants.KEY_USERNAME, binding.editUsername.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.editEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.editPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.editName.getText().toString());
                    preferenceManager.putString(Constants.KEY_USER_BIO, binding.editBio.getText().toString());
                    preferenceManager.putString(Constants.KEY_USERNAME, binding.editUsername.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.editEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_PASSWORD, binding.editPassword.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private String encodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.userProfilePic.setImageBitmap(bitmap);
                            encodedImage = encodedImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidUpdateDetails() {
        if (encodedImage == null) {
            showToast("Please select a profile picture!");
            return false;
        } else if (binding.editName.getText().toString().trim().isEmpty()) {
            showToast("Please input your full name!");
            return false;
        } else if (binding.editUsername.getText().toString().trim().isEmpty()) {
            showToast("Please input your username!");
            return false;
        } else if (binding.editEmail.getText().toString().trim().isEmpty()) {
            showToast("Please input your email!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editEmail.getText().toString()).matches()) {
            showToast("Please input a valid email!");
            return false;
        } else if (binding.editPassword.getText().toString().trim().isEmpty()) {
            showToast("Please input your password!");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.updateButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.updateButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void loadUserDetails() {
        binding.editUsername.setText(preferenceManager.getString(Constants.KEY_USERNAME));
        binding.editName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.editEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.editBio.setText(preferenceManager.getString(Constants.KEY_USER_BIO));
        binding.editPassword.setText(preferenceManager.getString(Constants.KEY_PASSWORD));
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