package com.thesis.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivitySignUpBinding;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.signupCancelButton.setOnClickListener(view -> onBackPressed());
    }

    public void signupUser(View view) {
        if (!isConnected(this)) {
            noInt();
        } else if (!validateName() | !validateUsername() | !validateEmail() | !validatePassword() | !validateConfPassword()) {
            return;
        } else {
            checkIfEmailExists(view);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signup() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.signupName.getText().toString());
        user.put(Constants.KEY_USERNAME, binding.signupUsername.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.signupEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.signupPassword.getText().toString());
        database.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.signupName.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_USERNAME, binding.signupUsername.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.signupEmail.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_PASSWORD, binding.signupPassword.getText().toString().trim());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private Boolean validateName() {
        String val = binding.signupName.getText().toString().trim();

        if (val.isEmpty()) {
            binding.signupName.setError("This field is required");
            return false;
        } else {
            binding.signupName.setError(null);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = binding.signupUsername.getText().toString().trim();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            binding.signupUsername.setError("This field is required");
            return false;
        } else if (val.length() >= 15) {
            binding.signupUsername.setError("Username too long!");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            binding.signupUsername.setError("Whites spaces not allowed!");
            return false;
        } else {
            binding.signupUsername.setError(null);
            return true;
        }
    }

    public void checkIfEmailExists(View view) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference userRef = database.collection(Constants.KEY_COLLECTION_USERS);
        Query query = userRef.whereEqualTo(Constants.KEY_EMAIL, binding.signupEmail.getText().toString().trim());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String email = documentSnapshot.getString(Constants.KEY_EMAIL);

                        if (email.equals(binding.signupEmail.getText().toString().trim())) {
                            binding.signupEmail.setError("Email already in use");
                            return;
                        }

                    }
                } if(task.getResult().size() == 0){
                    signup();
                    return;
                }
            }
        });
    }

    private Boolean validateEmail() {
        String val = binding.signupEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            binding.signupEmail.setError("This field is required");
            return false;
        } else if (!val.matches(emailPattern)) {
            binding.signupEmail.setError("Invalid Email Address!");
            return false;
        } else {
            binding.signupEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = binding.signupPassword.getText().toString().trim();
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
            binding.signupPassword.setError("This field is required");
            return false;
        } else if (!val.matches(passwordVal)) {
            binding.signupPassword.setError("Password must contain at least 1 upper case & 1 special character!");
            return false;
        } else {
            binding.signupPassword.setError(null);
            return true;
        }
    }

    private Boolean validateConfPassword() {
        String val = binding.signupConfirmPassword.getText().toString().trim();
        String pass = binding.signupPassword.getText().toString().trim();

        if (val.isEmpty()) {
            binding.signupConfirmPassword.setError("This field is required");
            return false;
        } else if (!val.matches(pass)) {
            binding.signupConfirmPassword.setError("Password didn't matched!");
            return false;
        } else {
            binding.signupConfirmPassword.setError(null);
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.signupButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.signupButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isConnected(SignUp signUp) {
        ConnectivityManager connectivityManager = (ConnectivityManager) signUp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void noInt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SignUp.this).inflate(
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
}