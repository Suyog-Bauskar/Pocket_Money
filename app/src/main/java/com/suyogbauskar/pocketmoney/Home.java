package com.suyogbauskar.pocketmoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.Locale;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private TextView titleTextView;
    private TextView balanceTextView;
    private FloatingActionButton plusButton;
    private Button showTransactionButton;
    private Button sharedTransactionButton;
    private LinearLayout linearLayout;

    public static String sharedID;
    public static String sharedFirstname;

    LinearLayout.LayoutParams params;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbarHome);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        titleTextView = findViewById(R.id.homeTitle);
        balanceTextView = findViewById(R.id.homeBalance);
        plusButton = findViewById(R.id.floatingPlusButton);
        showTransactionButton = findViewById(R.id.transactionButton);
        linearLayout = findViewById(R.id.linearLayout);
        sharedTransactionButton = findViewById(R.id.shareTransactionButton);

        sharedTransactionButton.setVisibility(View.GONE);
        sharedTransactionButton.setOnClickListener(null);

        plusButton.setOnClickListener(this);
        showTransactionButton.setOnClickListener(this);

        SharedFilters.month = 0;
        SharedFilters.yearPosition = 0;
        SharedFilters.isCreditCheckboxChecked = true;
        SharedFilters.isDebitCheckboxChecked = true;

        DocumentReference docRef = db.collection("Pocket Money").document(MainActivity.ID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentReference docRef = db.collection("Pocket Money").document(MainActivity.ID);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                String currency = format.format(Long.parseLong(document.getString("balance")));
                                String name = document.getString("firstname");
                                currency = currency.substring(0, currency.length() - 3);
                                balanceTextView.setText("Balance : " + currency);
                                titleTextView.setText("Welcome " + name);
                            } else {
                                Toast.makeText(getApplicationContext(), "No such document!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        db.collection("Pocket Money")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (MainActivity.thisEmail.equals(document.getString("transactionHistorySharedWith"))) {
                                    sharedID = document.getId();
                                    sharedFirstname = document.getString("firstname");

                                    sharedTransactionButton.setText(sharedFirstname + "'s transactions");
                                    sharedTransactionButton.setVisibility(View.VISIBLE);
                                    sharedTransactionButton.setOnClickListener(Home.this::onClick);

                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingPlusButton:
                startActivity(new Intent(getApplicationContext(), InputActivity.class));
                break;

            case R.id.transactionButton:
                startActivity(new Intent(getApplicationContext(), Table.class));
                break;

            case R.id.shareTransactionButton:
                startActivity(new Intent(getApplicationContext(), SharedTable.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                return true;

            case R.id.logout:
                showLogoutDialog();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }

    public void showLogoutDialog() {
        new AlertDialog.Builder(Home.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Pocket Money").document(MainActivity.ID).update("isLoggedIn", "False");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}