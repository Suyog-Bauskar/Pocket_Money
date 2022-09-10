package com.suyogbauskar.pocketmoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPassword extends AppCompatActivity {

    private EditText changeUsername;
    private EditText changePassword;
    private Button updateButton;

    private String username;
    private String password;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        changeUsername = findViewById(R.id.changeUsername);
        changePassword = findViewById(R.id.changePassword);
        updateButton = findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = changeUsername.getText().toString();
                password = changePassword.getText().toString();

                if (username.trim().isEmpty() && password.trim().isEmpty()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    if (!username.trim().isEmpty()) {
                        db.collection("Pocket Money").document(MainActivity.forgotScreenID).update("username", username);
                    }
                    if (!password.trim().isEmpty()) {
                        db.collection("Pocket Money").document(MainActivity.forgotScreenID).update("password", password);
                    }
                    Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });

        if (MainActivity.theme.equals("System")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (MainActivity.theme.equals("Light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (MainActivity.theme.equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}