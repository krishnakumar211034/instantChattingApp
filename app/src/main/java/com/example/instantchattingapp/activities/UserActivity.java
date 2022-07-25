package com.example.instantchattingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.instantchattingapp.R;
import com.example.instantchattingapp.adapters.UserAdapter;
import com.example.instantchattingapp.listeners.UserListener;
import com.example.instantchattingapp.models.User;
import com.example.instantchattingapp.utilities.Constants;
import com.example.instantchattingapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity implements UserListener {
    private PreferenceManager preferenceManager;
    private TextView error_text_message;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ImageView backPressedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        preferenceManager = new PreferenceManager(getApplicationContext());

        progressBar = findViewById(R.id.progress_bar);
        error_text_message = findViewById(R.id.error_text_message);
        recyclerView = findViewById(R.id.userRecyclerView);
        backPressedImage = findViewById(R.id.imageBack);
        backPressedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getUserDetails();
    }
    private void loading(boolean isLoading){
        if(isLoading) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.INVISIBLE);
    }
    private void showErrorMessage(){
        error_text_message.setText(String.format("%s","No User available"));
        error_text_message.setVisibility(View.VISIBLE);
    }
     private void getUserDetails(){
        loading(true);
        FirebaseFirestore firebaseFirestore =FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading(false);
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if(task.isSuccessful() && task.getResult()!=null) {
                            ArrayList<User> arr = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                if (currentUserId.equals(queryDocumentSnapshot.getId())) {
//                                    continue;
//                                }
                                User user = new User();
                                user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                                user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                user.setToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                                user.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                                arr.add(user);
                            }
                            if (arr.size() > 0) {
                                UserAdapter userAdapter = new UserAdapter(arr, getApplicationContext(), UserActivity.this);
                                recyclerView.setAdapter(userAdapter);
                                recyclerView.setVisibility(View.VISIBLE);
                            } else showErrorMessage();
                        } else showErrorMessage();
                    }
                });
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),chatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}