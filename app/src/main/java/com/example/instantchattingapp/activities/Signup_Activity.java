package com.example.instantchattingapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instantchattingapp.R;
import com.example.instantchattingapp.classes.Personal_Data;
import com.example.instantchattingapp.databinding.ActivitySignUpBinding;
import com.example.instantchattingapp.utilities.Constants;
import com.example.instantchattingapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Signup_Activity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private String encodedImage;
    PreferenceManager preferenceManager;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        // activating the click Listeners
        setListeners();
    }
    // method to generate Toast with any Message
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    // method used to keep all the clickable objects to standby mode
    private void setListeners() {
        // link for login option  onBackPressedButton() is used to invoke
        binding.imageAddButton.setOnClickListener(view -> onBackPressed());
        // this button is used to initiate signUp method if given credentials are satisfied
        binding.SignUpButton.setOnClickListener(view -> { if(validateCredentials()) signUp(); });
        // this intent is used to pick the image from the gallery
        binding.imageAddButton.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
        });
    }
    // method that validates the credentials of the users entered by them
    public boolean validateCredentials() {
        if(encodedImage==null){
            showToast("upload the image...");
            return false;
        }
        else if(TextUtils.isEmpty(binding.signupNameInput.getText().toString().trim()))
        {
            binding.signupNameInput.setError("Required Field.");
            return false;
        }
        else if(TextUtils.isEmpty(binding.signUpEmailInput.getText().toString().trim()))
        {
            binding.signUpEmailInput.setError("Required Field.");
            return false;
        }
        else if(TextUtils.isEmpty(binding.signUpPasswordInput.getText().toString().trim()))
        {
            binding.signUpPasswordInput.setError("Required Field.");
            return false;
        }
        else if(TextUtils.isEmpty(binding.confirmPasswordEdit.getText().toString().trim()))
        {
            binding.confirmPasswordEdit.setError("Required Field.");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.signUpEmailInput.getText().toString()).matches())
        {
            binding.signUpEmailInput.setError("enter valid email.");
            return false;
        }
        else if((binding.confirmPasswordEdit.getText().toString().trim()).compareTo(binding.signUpPasswordInput.getText().toString().trim())!=0)
        {
            binding.signUpPasswordInput.setError("password not matching.");
            return false;
        }
        else return true;
    }
    // loading method here sets the visibility of the button and progressBar
    private void loading(Boolean isloading){
        if(isloading){
            binding.SignUpButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.SignUpButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    // it uploads the data collected from the users to FirebaseFirestore
    private void signUp() {
        loading(true);
        //creating a new database and adding data to that database;
        firebaseFirestore=FirebaseFirestore.getInstance();
        HashMap<String,Object> map = new HashMap<>();
        map.put(Constants.KEY_NAME,binding.signupNameInput.getText().toString());
        map.put(Constants.KEY_EMAIL,binding.signUpEmailInput.getText().toString());
        map.put(Constants.KEY_PASSWORD,binding.signUpPasswordInput.getText().toString());
        map.put(Constants.KEY_IMAGE,encodedImage);
        CollectionReference dbReference=firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS);
        dbReference.add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                loading(false);
                // adding data to permanent memory after the login
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                preferenceManager.putString(Constants.KEY_NAME,binding.signupNameInput.getText().toString() );
                preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
            }
        });

    }
    // converting image in the Bitmap format to String format to upload it to the firebasefirestore
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 172;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte bytes[]=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    // about this i dont have proper knowledge currently
    private final ActivityResultLauncher<Intent> pickImage
            =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData()!=null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.signUpLogo.setImageBitmap(bitmap);
                            binding.imageAddButton.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });



}
