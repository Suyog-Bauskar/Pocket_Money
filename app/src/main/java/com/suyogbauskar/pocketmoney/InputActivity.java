package com.suyogbauskar.pocketmoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InputActivity extends AppCompatActivity {

    private FloatingActionButton doneButton;
    private RadioButton transactionTypeRadioButton;
    private RadioGroup transactionTypeRadioGroup;
    private EditText amountField;
    private EditText descriptionField;
    private long amount;
    private String description;
    private long databaseAmount;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Input");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        doneButton = findViewById(R.id.doneButton);
        transactionTypeRadioGroup = findViewById(R.id.radioGroup);
        amountField = findViewById(R.id.amount);
        descriptionField = findViewById(R.id.description);

        doneButton.setOnClickListener(view -> {

            int selectedId = transactionTypeRadioGroup.getCheckedRadioButtonId();
            transactionTypeRadioButton = findViewById(selectedId);
            description = descriptionField.getText().toString();
            try {
                amount = Long.parseLong(amountField.getText().toString());

                if (amount <= 0) {
                    Toast.makeText(getApplicationContext(), "Invalid Amount!",Toast.LENGTH_LONG).show();
                } else if (selectedId == -1) {
                    Toast.makeText(getApplicationContext(), "Transaction Type not selected", Toast.LENGTH_LONG).show();
                } else if (description.length() >= 500) {
                    Toast.makeText(getApplicationContext(), "Description too long", Toast.LENGTH_LONG).show();
                } else {
                    description = description.trim();
                    DocumentReference docRef = db.collection("Pocket Money").document(MainActivity.ID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    databaseAmount = Long.parseLong(document.getString("balance"));
                                    if (selectedId == R.id.credit) {
                                        databaseAmount += amount;

                                        db.collection("Pocket Money").document(MainActivity.ID).update("balance", databaseAmount + "");
                                        db.collection("Pocket Money").document(MainActivity.ID).update("transactions", FieldValue.arrayUnion(new Data(amount, transactionTypeRadioButton.getText().toString(), description)));

                                        startActivity(new Intent(getApplicationContext(), Home.class));
                                    } else if (selectedId == R.id.debit) {
                                        if ((databaseAmount - amount) > 0) {
                                            databaseAmount -= amount;

                                            db.collection("Pocket Money").document(MainActivity.ID).update("balance", databaseAmount + "");
                                            db.collection("Pocket Money").document(MainActivity.ID).update("transactions", FieldValue.arrayUnion(new Data(amount, transactionTypeRadioButton.getText().toString(), description)));

                                            startActivity(new Intent(getApplicationContext(), Home.class));
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Insufficient balance", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error loading data!",Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Error!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Invalid Amount!",Toast.LENGTH_LONG).show();
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
        startActivity(new Intent(getApplicationContext(), Home.class));
    }

}