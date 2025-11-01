package com.gajendra.nadiya;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
     EditText emailEditText, passwordEditText;
     CardView loginButton;
     TextView authGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try{
            //login
            mAuth = FirebaseAuth.getInstance();

            emailEditText = findViewById(R.id.emailEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.loginButton);
            authGreeting = findViewById(R.id.authGreeting);

            authGreeting.setText(getGreetings((int)(Math.random() * 11)));
            loginButton.setOnClickListener(v -> loginUser());
        } catch (Exception e) {
            Toast.makeText(this, e+"", Toast.LENGTH_SHORT).show();
        }
    }
    private void loginUser() {

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AuthActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                        Toast.makeText(AuthActivity.this, "Error: " + errorCode, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    String getGreetings(int position){

        List<String> greetings = new ArrayList<>();

        greetings.add(this.getString(R.string.question1)+" ");
        greetings.add(this.getString(R.string.question2)+" ");
        greetings.add(this.getString(R.string.question3)+" ");
        greetings.add(this.getString(R.string.question4)+" ");
        greetings.add(this.getString(R.string.question5)+" ");
        greetings.add(this.getString(R.string.question6)+" ");
        greetings.add(this.getString(R.string.question7)+" ");
        greetings.add(this.getString(R.string.question8)+" ");
        greetings.add(this.getString(R.string.question9)+" ");
        greetings.add(this.getString(R.string.question10)+" ");
        greetings.add(this.getString(R.string.question11)+" ");
        
        //authGreeting.setText(greetings.get(position));

        return greetings.get(position);
    }
}