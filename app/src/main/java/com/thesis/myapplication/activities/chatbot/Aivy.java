package com.thesis.myapplication.activities.chatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thesis.myapplication.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Aivy extends AppCompatActivity {

    private RecyclerView chatsRV;
    private EditText userMsgEdt;
    private FrameLayout sendMsgFAB;
    private ImageView backBtn;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private ArrayList<ChatsModal> chatsModalArrayList;
    private ChatRVAdapter chatRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aivy);

        chatsRV = findViewById(R.id.rvChat);
        userMsgEdt = findViewById(R.id.textInputBox);
        sendMsgFAB = findViewById(R.id.layoutSend);
        backBtn = findViewById(R.id.backButton);
        chatsModalArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModalArrayList, this);
        chatsRV.setAdapter(chatRVAdapter);
        chatsRV.setVisibility(View.VISIBLE);

        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userMsgEdt.getText().toString().isEmpty()) {
                    Toast.makeText(Aivy.this, "Type something to talk to Aivy!", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getResponse(String message) {
        chatsModalArrayList.add(new ChatsModal(message, USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=171116&key=zg5j53SiOrfanhdv&uid=[uid]&msg=" + message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModal> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if (response.isSuccessful()) {
                    MsgModal modal = response.body();
                    chatsModalArrayList.add(new ChatsModal(modal.getCnt(), BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
                chatsModalArrayList.add(new ChatsModal("Please re-phrase your message!", BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
            }
        });

    }
}
