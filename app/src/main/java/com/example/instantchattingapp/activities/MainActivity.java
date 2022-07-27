package com.example.instantchattingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instantchattingapp.R;
import com.example.instantchattingapp.databinding.ActivityMainBinding;
import com.example.instantchattingapp.utilities.Constants;
import com.example.instantchattingapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListener();
//        UserAdapter adapter = new UserAdapter(arr,MainActivity.this);
//        RecyclerView view=findViewById(R.id.root);
//        view.setAdapter(adapter);
//        view.setLayoutManager(new LinearLayoutManager(this));
    }
    private void setListener(){
        binding.powerButton.setOnClickListener(view -> signOut());
        binding.fabNewChat.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UserActivity.class)));
    }
    // uploading user name and image to the main page fetched from the PreferenceManager
    private void loadUserDetails(){
        binding.textView.setText(preferenceManager.getString(Constants.KEY_NAME));
        //set image using bitmap to the profile
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    // used to update the Token fetched from the preference manager
    private void updateToken(String token){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnFailureListener(e ->showToast("unable to update token..."));
    }
    // used to get the Token saved on the firebaseFirestore
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    // to delete the fcm token for the given user
    private void signOut() {
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN,FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused ->{
                    showToast("signing out...");
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                    finish();
                }).addOnFailureListener(e -> showToast("Unable to sign out..."));
    }
}