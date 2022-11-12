package com.thesis.myapplication.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityEditProfileBinding;
import com.thesis.myapplication.models.User;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    //Popup for Forget Password
    Dialog noProfile_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        loadUserDetails();
        noProfile_dialog = new Dialog(this);
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
        binding.editCancelButton.setOnClickListener(view -> onBackPressed());
        binding.profileFrame.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.updateButton.setOnClickListener(view -> updateUser());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    public void updateUser() {
        if (!isConnected(this)) {
            noInt();
        } else if (!validateUsername() | !validatePassword() | !validateProfile()) {
            return;
        } else {
            updateProfile();
        }
    }

    private void updateProfile() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_USER_BIO, binding.editBio.getText().toString());
        documentReference.update(Constants.KEY_USERNAME, binding.editUsername.getText().toString());
        documentReference.update(Constants.KEY_PASSWORD, binding.editPassword.getText().toString());
        documentReference.update(Constants.KEY_IMAGE, encodedImage);
        preferenceManager.clear();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        showToast("Update complete! Login with your new password!");
    }

    private Boolean validateProfile() {
        if (encodedImage == null) {
            noProfile();
            return false;
        } else {
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = binding.editPassword.getText().toString().trim();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#_$%^&.+=-])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            binding.editPassword.setError("This field is required");
            return false;
        } else if (!val.matches(passwordVal)) {
            binding.editPassword.setError("Password must contain at least 1 upper case & 1 special character!");
            return false;
        } else {
            binding.editPassword.setError(null);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = binding.editUsername.getText().toString().trim();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            binding.editUsername.setError("This field is required");
            return false;
        } else if (val.length() >= 15) {
            binding.editUsername.setError("Username too long!");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            binding.editUsername.setError("Whites spaces not allowed!");
            return false;
        } else {
            binding.editUsername.setError(null);
            return true;
        }
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

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.updateButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.updateButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isConnected(EditProfile editProfile) {
        ConnectivityManager connectivityManager = (ConnectivityManager) editProfile.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void noInt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(EditProfile.this).inflate(
                R.layout.no_internet_connection, findViewById(R.id.noInt_dialogContainer)
        );
        builder.setCancelable(false);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.noInt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.noInt_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    private void loadUserDetails() {
        binding.editUsername.setText(preferenceManager.getString(Constants.KEY_USERNAME));
        binding.editName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.editEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.editBio.setText(preferenceManager.getString(Constants.KEY_USER_BIO));
        binding.editPassword.setText(preferenceManager.getString(Constants.KEY_PASSWORD));
        if (preferenceManager.getString(Constants.KEY_IMAGE) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.userProfilePic.setImageBitmap(bitmap);
            binding.userProfilePic.setCornerRadius(30);
        } else {
            RoundedImageView user_p;
            user_p = findViewById(R.id.userProfilePic);
            user_p.setImageResource(R.drawable.sb_logo_only);
        }
    }

    public void noProfile() {
        Button popup_dismiss;

        noProfile_dialog.setContentView(R.layout.upload_profile_popup);
        popup_dismiss = noProfile_dialog.findViewById(R.id.uploadPicture);

        popup_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noProfile_dialog.dismiss();
            }
        });
        noProfile_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        noProfile_dialog.show();
        noProfile_dialog.setCanceledOnTouchOutside(false);
    }

}