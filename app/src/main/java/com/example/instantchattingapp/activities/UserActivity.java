package com.example.instantchattingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.instantchattingapp.R;
import com.example.instantchattingapp.adapters.UserAdapter;
import com.example.instantchattingapp.databinding.ActivityUserBinding;
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
    ActivityUserBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
        getUserDetails();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(view -> onBackPressed() );
    }

    // sets the visibility of progress bar
    private void loading(boolean isLoading){
        if(isLoading) binding.progressBar.setVisibility(View.VISIBLE);
        else binding.progressBar.setVisibility(View.INVISIBLE);
    }
    private void showErrorMessage() {
        binding.errorTextMessage.setText(String.format("%s","No User available"));
        binding.errorTextMessage.setVisibility(View.VISIBLE);
    }
     private void getUserDetails(){
        loading(true);
        FirebaseFirestore firebaseFirestore =FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading(false);
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if(task.isSuccessful() && task.getResult()!=null) {
                            ArrayList<User> arr = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if(currentUserId.equals(queryDocumentSnapshot.getId())) continue;
                                User user = new User();
                                user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                                user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                user.setToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                                user.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                                user.setId(queryDocumentSnapshot.getId());
                                arr.add(user);
                            }
                            if (arr.size() > 0) {
                                UserAdapter userAdapter = new UserAdapter(arr);
                                binding.userRecyclerView.setAdapter(userAdapter);
                                binding.userRecyclerView.setVisibility(View.VISIBLE);
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