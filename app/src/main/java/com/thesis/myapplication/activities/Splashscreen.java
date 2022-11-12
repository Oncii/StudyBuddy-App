package com.thesis.myapplication.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityLoginBinding;
import com.thesis.myapplication.databinding.ActivitySplashscreenBinding;

public class Splashscreen extends AppCompatActivity {

    //Duration for the Splashscreen
    private static int SPLASH_SCREEN = 1800;

    private ActivitySplashscreenBinding binding;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(!isConnected(this)){
            noInt();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_SCREEN);
        }

        mLayout = findViewById(R.id.noInt_dialogContainer);

    }

    private boolean isConnected(Splashscreen splashscreen) {

        ConnectivityManager connectivityManager = (ConnectivityManager) splashscreen.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConnection != null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    private void noInt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Splashscreen.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(Splashscreen.this).inflate(
                R.layout.no_internet_connection, findViewById(R.id.noInt_dialogContainer)
        );
        builder.setCancelable(false);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.noInt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                System.exit(0);
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

    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected(this)){
            noInt();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_SCREEN);
        }
    }
}