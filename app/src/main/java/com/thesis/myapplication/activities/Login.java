package com.thesis.myapplication.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.thesis.myapplication.databinding.ActivityLoginBinding;
import com.thesis.myapplication.utilities.Constants;
import com.thesis.myapplication.utilities.PreferenceManager;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;


    //Popup for Forget Password
    Dialog forgot_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        //For Forget Password Popup
        forgot_dialog = new Dialog(this);
        setContentView(binding.getRoot());
        setListeners();

    }

    private void setListeners() {
        binding.loginToSignupButton.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), SignUp.class)));
    }

    public void loginUser(View view) {
        if (!isConnected(this)) {
            noInt();
        } else if (!validateEmail() | !validatePassword()) {
            return;
        } else {
            checkIfEmailExists(view);
        }
    }

    public void checkIfEmailExists(View view) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference userRef = database.collection(Constants.KEY_COLLECTION_USERS);
        Query query = userRef.whereEqualTo(Constants.KEY_EMAIL, binding.loginEmail.getText().toString().trim());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String email = documentSnapshot.getString(Constants.KEY_EMAIL);

                        if (email.equals(binding.loginEmail.getText().toString().trim())) {
                            logIn();
                            return;
                        }

                    }
                } if(task.getResult().size() == 0){
                    binding.loginEmail.setError("No such account exists");
                    return;
                }
            }
        });
    }

    private void logIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.loginEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.loginPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_USERNAME, documentSnapshot.getString(Constants.KEY_USERNAME));
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_USER_BIO, documentSnapshot.getString(Constants.KEY_USER_BIO));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Something went wrong, please check your login details");
                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.loginButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.loginButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean validateEmail() {
        String val = binding.loginEmail.getText().toString().trim();

        if (val.isEmpty()) {
            binding.loginEmail.setError("This field is required");
            return false;
        } else {
            binding.loginEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = binding.loginPassword.getText().toString().trim();

        if (val.isEmpty()) {
            binding.loginPassword.setError("This field is required");
            return false;
        } else {
            binding.loginPassword.setError(null);
            return true;
        }
    }

    private boolean isConnected(Login login) {

        ConnectivityManager connectivityManager = (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void noInt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(Login.this).inflate(
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

    //Not Yet Finished
    public void ShowPopup(View view){
        Button for_cancel;

        forgot_dialog.setContentView(R.layout.activity_forgot_pass);


        for_cancel = forgot_dialog.findViewById(R.id.forgot_cancel);

        for_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgot_dialog.dismiss();
            }
        });
        forgot_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forgot_dialog.show();
        forgot_dialog.setCanceledOnTouchOutside(false);
    }
}