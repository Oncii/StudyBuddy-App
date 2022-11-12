package com.thesis.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityMainBinding;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

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

    private void checkIfUserHasProfilePic1() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference userRef = database.collection(Constants.KEY_COLLECTION_USERS);
        Query query = userRef.whereEqualTo(Constants.KEY_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                    }
                } if(preferenceManager.getString(Constants.KEY_IMAGE) == null) {
                    noImage();
                } else {
                    startActivity(new Intent(getApplicationContext(), Friends.class));
                }
            }
        });
    }

    private void checkIfUserHasProfilePic2() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference userRef = database.collection(Constants.KEY_COLLECTION_USERS);
        Query query = userRef.whereEqualTo(Constants.KEY_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                    }
                } if(preferenceManager.getString(Constants.KEY_IMAGE) == null) {
                    noImage();
                } else {
                    startActivity(new Intent(getApplicationContext(), Messages.class));
                }
            }
        });
    }

    private void setListeners() {
        binding.settingsButton.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), Settings.class)));
        binding.friendsButton.setOnClickListener(view -> checkIfUserHasProfilePic1());
        binding.messagesButton.setOnClickListener(view -> checkIfUserHasProfilePic2());
    }


    private void loadUserDetails() {
        binding.userName.setText(preferenceManager.getString(Constants.KEY_USERNAME));

        if (preferenceManager.getString(Constants.KEY_IMAGE) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.userProfile.setImageBitmap(bitmap);
            binding.userProfile.setCornerRadius(30);
        } else {
            RoundedImageView user_p;
            user_p = findViewById(R.id.userProfile);
            user_p.setImageResource(R.drawable.sb_logo_only);
        }
    }


    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Token update failed"));
    }

    private boolean isConnected(MainActivity mainActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void noInt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.no_internet_connection, findViewById(R.id.noInt_dialogContainer)
        );
        builder.setCancelable(false);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.noInt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                System.exit(1);
            }
        });
        view.findViewById(R.id.noInt_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnected(this)) {
            noInt();
        }
    }

    private void noImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.no_image_detected, findViewById(R.id.noImage_dialogContainer)
        );
        builder.setCancelable(false);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.noImage_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.noImage_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditProfile.class));
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }
}