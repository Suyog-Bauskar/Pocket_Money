package com.suyogbauskar.pocketmoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    private EditText firstnameField;
    private EditText lastnameField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText emailField;
    private Button registerButton;
    private TextView loginButton;
    public boolean isDuplicate;
    private String registrationID;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstnameField = findViewById(R.id.SignupFirstnameField);
        lastnameField = findViewById(R.id.SignupLastnameField);
        usernameField = findViewById(R.id.SignupUsernameField);
        passwordField = findViewById(R.id.SignupPasswordField);
        emailField = findViewById(R.id.SignupEmailField);
        registerButton = findViewById(R.id.SignupRegisterButton);
        loginButton = findViewById(R.id.SignupLoginButton);

        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        firstnameField.requestFocus();

        if (MainActivity.theme != null) {
            if (MainActivity.theme.equals("System")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else if (MainActivity.theme.equals("Light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (MainActivity.theme.equals("Dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }

        registrationID = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SignupRegisterButton:

                isDuplicate = false;
                String firstname = firstnameField.getText().toString();
                String lastname = lastnameField.getText().toString();
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                String email = emailField.getText().toString();

                if (firstname.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Firstname", Toast.LENGTH_LONG).show();
                } else if (lastname.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Lastname", Toast.LENGTH_LONG).show();
                } else if (username.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_LONG).show();
                } else if (password.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_LONG).show();
                } else if (email.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_LONG).show();
                } else {

                    db.collection("Pocket Money")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (username.equals(document.getString("username"))) {
                                                isDuplicate = true;
                                                Toast.makeText(getApplicationContext(), "Username already exists!", Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                            if (email.equals(document.getString("email"))) {
                                                isDuplicate = true;
                                                Toast.makeText(getApplicationContext(), "Email is already registered", Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                            if (MainActivity.uniqueID.equals(document.getString("registrationID"))) {
                                                isDuplicate = true;
                                                new AlertDialog.Builder(Signup.this)
                                                        .setTitle("New Account")
                                                        .setMessage("Sorry you can only create one id from one device")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .setIcon(R.drawable.error)
                                                        .show();
                                                break;
                                            }
                                        }
                                        if (!isDuplicate) {

                                            Date date = new Date();
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                            String dateToStr = dateFormat.format(date);
                                            String timeToStr = timeFormat.format(date);

                                            Map<String, Object> user = new HashMap<>();
                                            user.put("firstname", firstname);
                                            user.put("lastname", lastname);
                                            user.put("username", username);
                                            user.put("password", password);
                                            user.put("email", email);
                                            user.put("balance", "0");
                                            user.put("registrationID", registrationID);
                                            user.put("isAppPasscodeSet", "False");
                                            user.put("isNewAccount", "True");
                                            user.put("appPasscode", "0");
                                            user.put("transactionHistorySharedWith", "False");
                                            user.put("theme", "System");
                                            user.put("accountCreationDate", dateToStr);
                                            user.put("accountCreationTime", timeToStr);

                                            db.collection("Pocket Money").add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getApplicationContext(), "You are now registered", Toast.LENGTH_LONG).show();
                                                            usernameField.setText("");
                                                            passwordField.setText("");
                                                            startActivity(new Intent(Signup.this, MainActivity.class));
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "Failed! Try again", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }

                break;

            case R.id.SignupLoginButton:
                startActivity(new Intent(Signup.this, MainActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}