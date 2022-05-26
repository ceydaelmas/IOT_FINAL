package com.example.voice_assistant;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.voice_assistant.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    private ActionBar actionBar;

    private FirebaseAuth firebaseAuth;

    public static final Integer RecordAudioRequestCode = 1;

    EditText editText;
    private ImageButton speechButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editText = findViewById(R.id.editTextvoice);
        //imageView = findViewById(R.id.imageButton);
        actionBar = getSupportActionBar();
        actionBar.setTitle("SignUp");

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        speechButton = findViewById(R.id.imageButton);

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak up");
                startActivityForResult(intent,RecordAudioRequestCode);
            }
        });
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

    }
    public void buttonClicked(View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Task");

        String message = editText.getText().toString();
        myRef.setValue(message);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==RecordAudioRequestCode&&resultCode==RESULT_OK){
            ArrayList<String>taskText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editText.setText(taskText.get(0));

        }
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            String email = firebaseUser.getEmail();
            binding.emailTV.setText(email);

        }
    }
}