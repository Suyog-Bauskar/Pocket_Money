package com.suyogbauskar.pocketmoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView forgotPassword;
    private Button loginButton;
    private TextView createButton;
    private EditText usernameField;
    private EditText passwordField;
    private EditText inputField;
    private Button passcodeLoginButton;

    public static String appVersion = "1.0.0";
    public static String ID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String username;
    public String password;
    private boolean isLoggedIn = false;
    private boolean flag = false;
    private boolean canGoTOForgotAccountScreen;
    public static String uniqueID;
    public static String thisEmail;
    public static String thisPassword;
    private String isNewAccount = "False";
    private String setPasscodeText;
    private String confirmSetPasscodeText;
    private int invalidAttempts;
    public static String forgotScreenID;
    public static String theme;

    AlertDialog.Builder alert;
    LinearLayout layout;
    LinearLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen);
        uniqueID = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        theme = "System";

        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins((int) (15 * getResources().getDisplayMetrics().density + 0.5f), (int) (15 * getResources().getDisplayMetrics().density + 0.5f), (int) (15 * getResources().getDisplayMetrics().density + 0.5f), 0);
        invalidAttempts = 0;
        canGoTOForgotAccountScreen = false;

        loadTheme();

        db.collection("Pocket Money")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (uniqueID.equals(document.getString("uniqueID")) && document.getString("isAppPasscodeSet").equals("True") && document.getString("isLoggedIn").equals("True")) {
                                    ID = document.getId();
                                    thisEmail = document.getString("email");
                                    thisPassword = document.getString("password");
                                    flag = true;
                                    theme = document.getString("theme");

                                    setContentView(R.layout.passcode_page);

                                    inputField = findViewById(R.id.passcodeInputField);
                                    passcodeLoginButton = findViewById(R.id.passcodeLoginButton);

                                    String passcode = document.getString("appPasscode");

                                    passcodeLoginButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (inputField.getText().toString().equals(passcode)) {
                                                db.collection("Pocket Money").document(MainActivity.ID).update("isLoggedIn", "True");
                                                startActivity(new Intent(getApplicationContext(), Home.class));
                                            } else {
                                                if (invalidAttempts == 4) {
                                                    Toast.makeText(getApplicationContext(), "Too many attempts", Toast.LENGTH_LONG).show();
                                                    db.collection("Pocket Money").document(MainActivity.ID).update("isLoggedIn", "False");
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Invalid passcode", Toast.LENGTH_LONG).show();
                                                }
                                                invalidAttempts++;
                                            }
                                        }
                                    });

                                    inputField.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            if (inputField.getText().toString().equals(passcode)) {
                                                db.collection("Pocket Money").document(MainActivity.ID).update("isLoggedIn", "True");
                                                startActivity(new Intent(getApplicationContext(), Home.class));
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {

                                        }
                                    });

                                    break;
                                }

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        if (!flag) {

            setContentView(R.layout.activity_main);

            forgotPassword = findViewById(R.id.loginForgotButton);
            loginButton = findViewById(R.id.loginLoginButton);
            createButton = findViewById(R.id.loginCreateButton);
            usernameField = findViewById(R.id.loginUsernameField);
            passwordField = findViewById(R.id.loginPasswordField);

            forgotPassword.setOnClickListener(this);
            loginButton.setOnClickListener(this);
            createButton.setOnClickListener(this);
        }

        checkForRegistrationID();

        if (!canGoTOForgotAccountScreen) {
            checkForUniqueID();
        }
    }

    @Override
    public void onClick (View view){
        switch (view.getId()) {
            case R.id.loginForgotButton:

                if (canGoTOForgotAccountScreen) {
                    startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Forgot Account")
                            .setMessage("No recently logged in or created account found")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(R.drawable.error)
                            .show();
                }

                break;

            case R.id.loginCreateButton:
                startActivity(new Intent(MainActivity.this, Signup.class));
                break;

            case R.id.loginLoginButton:

                isLoggedIn = false;
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();

                if (username.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_LONG).show();
                } else if (password.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_LONG).show();
                } else {

                    db.collection("Pocket Money")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (username.equals(document.getString("username")) && password.equals(document.getString("password"))) {
                                                ID = document.getId();
                                                thisEmail = document.getString("email");
                                                thisPassword = document.getString("password");
                                                isLoggedIn = true;
                                                theme = document.getString("theme");
                                                isNewAccount = document.getString("isNewAccount");

                                                if (isNewAccount.equals("True")) {

                                                    new AlertDialog.Builder(MainActivity.this)
                                                            .setTitle("Set Passcode")
                                                            .setMessage("Do you want to set passcode to this app instead of signing in everytime? (You can change this later in settings)")
                                                            .setCancelable(false)
                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    createSetPasscodeDialog();
                                                                }
                                                            })
                                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    db.collection("Pocket Money").document(MainActivity.ID).update("isLoggedIn", "True");
                                                                    db.collection("Pocket Money").document(MainActivity.ID).update("uniqueID", uniqueID);
                                                                    db.collection("Pocket Money").document(MainActivity.ID).update("isNewAccount", "False");
                                                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                                                }
                                                            })
                                                            .show();
                                                } else {
                                                    db.collection("Pocket Money").document(MainActivity.ID).update("isLoggedIn", "True");
                                                    db.collection("Pocket Money").document(MainActivity.ID).update("uniqueID", uniqueID);
                                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                                }

                                                break;
                                            }
                                        }
                                        if (!isLoggedIn) {
                                            Toast.makeText(getApplicationContext(), "Invalid credentials!", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                break;
        }
    }

    public void createSetPasscodeDialog() {
        alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Set Passcode");

        layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText setPasscodeField = new EditText(MainActivity.this);
        setPasscodeField.setHint("4 digit numeric passcode");
        setPasscodeField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        setPasscodeField.setLayoutParams(params);
        layout.addView(setPasscodeField);

        final EditText confirmSetPasscodeField = new EditText(MainActivity.this);
        confirmSetPasscodeField.setHint("Confirm passcode");
        confirmSetPasscodeField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        confirmSetPasscodeField.setLayoutParams(params);
        layout.addView(confirmSetPasscodeField);

        alert.setView(layout);

        alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                setPasscodeText = setPasscodeField.getText().toString();
                confirmSetPasscodeText = confirmSetPasscodeField.getText().toString();

                if (setPasscodeText.trim().isEmpty() || confirmSetPasscodeText.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are compulsory", Toast.LENGTH_LONG).show();
                    createSetPasscodeDialog();
                } else if (setPasscodeText.length() != 4) {
                    Toast.makeText(getApplicationContext(), "Passcode must be 4 digit", Toast.LENGTH_LONG).show();
                    createSetPasscodeDialog();
                } else if (!setPasscodeText.equals(confirmSetPasscodeText)) {
                    Toast.makeText(getApplicationContext(), "Passcode didn't match", Toast.LENGTH_LONG).show();
                    createSetPasscodeDialog();
                } else {
                    db.collection("Pocket Money").document(MainActivity.ID).update("isLoggedIn", "True");
                    db.collection("Pocket Money").document(MainActivity.ID).update("uniqueID", uniqueID);
                    db.collection("Pocket Money").document(MainActivity.ID).update("isAppPasscodeSet", "True");
                    db.collection("Pocket Money").document(MainActivity.ID).update("appPasscode", setPasscodeText);
                    db.collection("Pocket Money").document(MainActivity.ID).update("isNewAccount", "False");
                    Toast.makeText(getApplicationContext(), "Passcode set successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Home.class));
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                db.collection("Pocket Money").document(MainActivity.ID).update("isLoggedIn", "True");
                db.collection("Pocket Money").document(MainActivity.ID).update("uniqueID", uniqueID);
                db.collection("Pocket Money").document(MainActivity.ID).update("isNewAccount", "False");
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });

        alert.show();
    }

    public void checkForRegistrationID() {
        db.collection("Pocket Money")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (uniqueID.equals(document.getString("registrationID"))) {
                                    canGoTOForgotAccountScreen = true;
                                    forgotScreenID = document.getId();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void checkForUniqueID() {
        db.collection("Pocket Money")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (uniqueID.equals(document.getString("uniqueID"))) {
                                    canGoTOForgotAccountScreen = true;
                                    forgotScreenID = document.getId();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void loadTheme() {
        db.collection("Pocket Money")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (uniqueID.equals(document.getString("uniqueID"))) {
                                    MainActivity.theme = document.getString("theme");

                                    if (MainActivity.theme.equals("System")) {
                                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                    } else if (MainActivity.theme.equals("Light")) {
                                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    } else if (MainActivity.theme.equals("Dark")) {
                                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });
    }
}