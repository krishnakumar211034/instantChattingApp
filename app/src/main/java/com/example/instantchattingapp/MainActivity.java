package com.example.instantchattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instantchattingapp.activities.Login_Activity;
import com.example.instantchattingapp.activities.UserActivity;
import com.example.instantchattingapp.adapters.UserAdapter;
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
    private PreferenceManager preferenceManager;
    private TextView userName;
    private ImageView userProfile,signOut;
    private FloatingActionButton fabNewChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceManager=new PreferenceManager(getApplicationContext());

        userName=findViewById(R.id.textView);
        userProfile=findViewById(R.id.image_profile);
        signOut=findViewById(R.id.power_button);
        fabNewChat = findViewById(R.id.fabNewChat);

        loadUserDetails();
        getToken();
//        UserAdapter adapter = new UserAdapter(arr,MainActivity.this);
//        RecyclerView view=findViewById(R.id.root);
//        view.setAdapter(adapter);
//        view.setLayoutManager(new LinearLayoutManager(this));
        fabNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                DocumentReference documentReference=firebaseFirestore.collection(Constants.USERS)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
                documentReference.update(Constants.KEY_FCM_TOKEN, FieldValue.delete());
                HashMap<String,Object> updates = new HashMap<>();
                updates.put(Constants.KEY_FCM_TOKEN,FieldValue.delete());
                documentReference.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                preferenceManager.clear();
                                startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Signing out...", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
    public void loadUserDetails(){
        userName.setText(preferenceManager.getString(Constants.KEY_NAME));
        //set image using bitmap to the profile
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        userProfile.setImageBitmap(bitmap);
    }
    private void updateToken(String token){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.collection(Constants.USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "unable to update token...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
}