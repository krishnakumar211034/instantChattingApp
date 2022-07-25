package com.example.instantchattingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.instantchattingapp.R;
import com.example.instantchattingapp.databinding.ActivityChatBinding;
import com.example.instantchattingapp.databinding.ActivityMainBinding;
import com.example.instantchattingapp.models.User;
import com.example.instantchattingapp.utilities.Constants;

public class chatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User reciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadingUserDetails();

    }
    private void loadingUserDetails(){
        reciever = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.userName.setText(reciever.getName());
    }
    private void setListener(){
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }
}