package com.example.instantchattingapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.example.instantchattingapp.MainActivity;
import com.example.instantchattingapp.R;
import com.example.instantchattingapp.classes.Personal_Data;
import com.example.instantchattingapp.utilities.Constants;
import com.example.instantchattingapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Signup_Activity extends AppCompatActivity {
    private EditText sname,semail,spassword,sconfirmpassword;
    private ProgressBar progressBar;
    private ImageView profile;
    private Button submit,addImage;
    private TextView login_option;
    private String encodedImage;
    PreferenceManager preferenceManager;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sname=findViewById(R.id.signup_name_input);
        spassword=findViewById(R.id.signUp_password_input);
        semail=findViewById(R.id.signUp_email_input);
        sconfirmpassword=findViewById(R.id.confirm_password_edit);
        submit=findViewById(R.id.SignUp_button);
        login_option=findViewById(R.id.login_option);
        profile=findViewById(R.id.signUp_logo);
        addImage=findViewById(R.id.button);
        progressBar=findViewById(R.id.progress_bar);
        preferenceManager=new PreferenceManager(getApplicationContext());



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateCredentials())
                {
                    loading(true);
                    //creating a new database and adding data to that database;
                    Personal_Data pd=new Personal_Data(sname.getText().toString().trim(),semail.getText().toString().trim(),spassword.getText().toString().trim(),encodedImage);
                    firebaseFirestore=FirebaseFirestore.getInstance();
                    CollectionReference dbReference=firebaseFirestore.collection(Constants.USERS);

                    dbReference.add(pd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                            preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                            preferenceManager.putString(Constants.KEY_NAME,sname.getText().toString().trim());
                            preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Signup_Activity.this, "Failed to add Object"+e+"", Toast.LENGTH_SHORT).show();
                        }
                    });
                    loading(false);
                }
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth=172;
        int previewHeight=bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte bytes[]=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData()!=null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            profile.setImageBitmap(bitmap);
                            addImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    private void loading(Boolean isloading){
        if(isloading){
            submit.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            submit.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    public boolean validateCredentials() {
        if(TextUtils.isEmpty(sname.getText().toString().trim()))
        {
            sname.setError("Required Field.");
            return false;
        }
        else if(TextUtils.isEmpty(semail.getText().toString().trim()))
        {
            semail.setError("Required Field.");
            return false;
        }
        else if(TextUtils.isEmpty(spassword.getText().toString().trim()))
        {
            spassword.setError("Required Field.");
            return false;
        }
        else if(TextUtils.isEmpty(sconfirmpassword.getText().toString().trim()))
        {
            sconfirmpassword.setError("Required Field.");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(semail.getText().toString()).matches())
        {
            semail.setError("enter valid email.");
            return false;
        }
        else if((spassword.getText().toString().trim()).compareTo(sconfirmpassword.getText().toString().trim())!=0)
        {
            spassword.setError("password not matching.");
            return false;
        }
        else return true;
    }
}
