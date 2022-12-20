package com.thesis.myapplication.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.thesis.myapplication.R;
import com.thesis.myapplication.databinding.ActivityAboutApplicationBinding;

public class AboutApplication extends AppCompatActivity {

    private ActivityAboutApplicationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutApplicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.backButton.setOnClickListener(view -> onBackPressed());
        binding.appInfoButton.setOnClickListener(view -> appInfo());
        binding.privacyButton.setOnClickListener(view -> privacyPolicy());
    }

    private void appInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutApplication.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(AboutApplication.this).inflate(
                R.layout.app_info, findViewById(R.id.noInt_dialogContainer)
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
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    private void privacyPolicy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutApplication.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(AboutApplication.this).inflate(
                R.layout.privacy_policy, findViewById(R.id.noInt_dialogContainer)
        );
        builder.setCancelable(true);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }
}