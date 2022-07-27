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

import com.example.instantchattingapp.R;
import com.example.instantchattingapp.databinding.ActivityLoginBinding;
import com.example.instantchattingapp.utilities.Constants;
import com.example.instantchattingapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login_Activity extends AppCompatActivity {
    // Object for data binding for this Activity
    private ActivityLoginBinding binding;
    // Preference Manager is an object that is used to store small amount of data in the mobile
    PreferenceManager preferenceManager;
    // Object to send data to firebase
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);  // MainActivity is loaded if user has already signed in already
            finish();
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }
    // set the click Listener for the clickable buttons
    private void setListeners() {
        //link for signup Layout
        binding.signUpOption.setOnClickListener(v -> startActivity(new Intent(Login_Activity.this,Signup_Activity.class)));
        binding.forgotPassword.setOnClickListener(v -> startActivity(new Intent(Login_Activity.this,Reset_Activity.class)));
        binding.LoginButton.setOnClickListener(v -> {
            if(validateCredentials()) Login();
        });
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    /* in this method, we fetch data from the firebasefirestore and match the data entered by the user and then loads mainActivity
    if the credentials are matching
     */
    private void Login() {
        loading(true);
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.loginEmailInput.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.loginPasswordInput.getText().toString())
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
                            showToast("unable to signIn...");
                        }
                    }
                });
        loading(false);

    }
    // method to validate the credentials entered by the users
    private boolean validateCredentials(){
        if(TextUtils.isEmpty(binding.loginEmailInput.getText().toString().trim()))
        {
            binding.loginEmailInput.setError("Required Field.");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.loginEmailInput.getText().toString()).matches())
        {
            binding.loginEmailInput.setError("enter valid email.");
            return false;
        }
        else if(TextUtils.isEmpty(binding.loginPasswordInput.getText().toString().trim()))
        {
            binding.loginPasswordInput.setError("Required Field.");
            return false;
        }
        else return true;
    }
    // related to the visibility actions of ProgressBar and SignUp Button
    private void loading(Boolean isloading){
        if(isloading){
            binding.LoginButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.LoginButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
