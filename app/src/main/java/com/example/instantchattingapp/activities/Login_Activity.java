package com.example.instantchattingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instantchattingapp.MainActivity;
import com.example.instantchattingapp.R;
import com.example.instantchattingapp.classes.Personal_Data;
import com.example.instantchattingapp.utilities.Constants;
import com.example.instantchattingapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class Login_Activity extends AppCompatActivity {
    private EditText lemail,lpassword;
    private Button login;
    private ProgressBar progressBar;
    private TextView forgotPassword,signupOption;
    PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lemail=findViewById(R.id.login_email_input);
        lpassword=findViewById(R.id.login_password_input);
        login=findViewById(R.id.Login_button);
        forgotPassword=findViewById(R.id.forgot_password);
        signupOption=findViewById(R.id.signUp_option);
        progressBar=findViewById(R.id.progress_bar);
        preferenceManager=new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

        signupOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Activity.this,Signup_Activity.class));
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Activity.this,Reset_Activity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(true);
                firebaseFirestore= FirebaseFirestore.getInstance();
                firebaseFirestore.collection(Constants.USERS)
                        .whereEqualTo(Constants.KEY_EMAIL,lemail.getText().toString())
                        .whereEqualTo(Constants.KEY_PASSWORD,lpassword.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                                    DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                                    preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                                    preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                                    preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                else{
                                    loading(false);
                                    Toast.makeText(Login_Activity.this, ""+task.getResult().getDocuments().size()+"", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                loading(false);

            }
        });
    }
    private boolean validateCredentials(){
        if(TextUtils.isEmpty(lemail.getText().toString().trim()))
        {
            lemail.setError("Required Field.");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(lemail.getText().toString()).matches())
        {
            lemail.setError("enter valid email.");
            return false;
        }
        else if(TextUtils.isEmpty(lpassword.getText().toString().trim()))
        {
            lpassword.setError("Required Field.");
            return false;
        }
        else return true;
    }
    private void loading(Boolean isloading){
        if(isloading){
            login.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            login.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
