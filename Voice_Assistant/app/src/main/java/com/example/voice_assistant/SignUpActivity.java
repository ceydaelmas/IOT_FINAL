package com.example.voice_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.voice_assistant.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private String email ="", password = "";

    private FirebaseAuth firebaseAuth;

    private ActionBar actionBar;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        actionBar = getSupportActionBar();
        actionBar.setTitle("SignUp");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        firebaseAuth= FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Creating you account...");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void validateData() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordET.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("Invalide email format");
        }
        else if(TextUtils.isEmpty(password)){
             binding.passwordET.setError("Enter password");
        }
        else if(password.length()<6){
            binding.emailEt.setError("Password must at least 6 characters long");
        }
        else{
            firebaseSignUP();
        }
    }

    private void firebaseSignUP() {
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressDialog.dismiss();
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String email = firebaseUser.getEmail();
                                Toast.makeText(SignUpActivity.this,"Account created\n"+email,Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,ProfileActivity.class));
                                finish();
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}