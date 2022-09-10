package com.suyogbauskar.pocketmoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button firstnameButton;
    private Button lastnameButton;
    private Button emailButton;
    private Button usernameButton;
    private Button passwordButton;
    private Button deleteButton;
    private CheckBox appPasscodeCheckBox;
    private CheckBox shareTransactionHistoryCheckBox;
    private Spinner themeSpinner;
    private Button aboutButton;

    private String firstname;
    private String lastname;
    private String oldEmailText;
    private String newEmailText;
    private String confirmNewEmailText;
    private String oldUsernameText;
    private String newUsernameText;
    private String confirmNewUsernameText;
    private String oldPasswordText;
    private String newPasswordText;
    private String confirmNewPasswordText;
    private String usernameText;
    private String passwordText;

    private boolean isEmailChangePending;
    private boolean isUsernameChangePending;
    private boolean isPasswordChangePending;
    private boolean isProcessRemaining;
    private boolean isInvalidCredentials;

    LinearLayout.LayoutParams params;
    LinearLayout.LayoutParams aboutParams;
    AlertDialog.Builder alert;
    LinearLayout layout;
    private String setPasscodeText;
    private String confirmSetPasscodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) (15 * getResources().getDisplayMetrics().density + 0.5f), (int) (15 * getResources().getDisplayMetrics().density + 0.5f), (int) (15 * getResources().getDisplayMetrics().density + 0.5f), 0);

        aboutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        aboutParams.setMargins((int) (15 * getResources().getDisplayMetrics().density + 0.5f), (int) (15 * getResources().getDisplayMetrics().density + 0.5f), (int) (15 * getResources().getDisplayMetrics().density + 0.5f), (int) (15 * getResources().getDisplayMetrics().density + 0.5f));

        firstnameButton = findViewById(R.id.firstnameButton);
        lastnameButton = findViewById(R.id.lastnameButton);
        emailButton = findViewById(R.id.emailButton);
        usernameButton = findViewById(R.id.usernameButton);
        passwordButton = findViewById(R.id.passwordButton);
        deleteButton = findViewById(R.id.deleteAccountButton);
        appPasscodeCheckBox = findViewById(R.id.appPasscodeCheckBox);
        shareTransactionHistoryCheckBox = findViewById(R.id.shareDataCheckBox);
        themeSpinner = findViewById(R.id.theme_spinner);
        aboutButton = findViewById(R.id.aboutButton);

        appPasscodeCheckBox.setTag("False");
        appPasscodeCheckBox.setChecked(false);
        shareTransactionHistoryCheckBox.setTag("False");
        shareTransactionHistoryCheckBox.setChecked(false);

        firstnameButton.setOnClickListener(this);
        lastnameButton.setOnClickListener(this);
        emailButton.setOnClickListener(this);
        usernameButton.setOnClickListener(this);
        passwordButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        appPasscodeCheckBox.setOnClickListener(this);
        shareTransactionHistoryCheckBox.setOnClickListener(this);
        aboutButton.setOnClickListener(this);

        DocumentReference docRef = db.collection("Pocket Money").document(MainActivity.ID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getString("isAppPasscodeSet").equals("True")) {
                            appPasscodeCheckBox.setChecked(true);
                            appPasscodeCheckBox.setTag("True");
                        }
                        if (!document.getString("transactionHistorySharedWith").equals("False")) {
                            shareTransactionHistoryCheckBox.setChecked(true);
                            shareTransactionHistoryCheckBox.setTag("True");
                        }
                    }
                }
            }
        });

        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(this, R.array.theme_array, R.layout.theme_spinner_item);
        themeAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            themeAdapter = ArrayAdapter.createFromResource(this, R.array.theme_array, R.layout.dark_theme_spinner_item);
            themeAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        }

        themeSpinner.setAdapter(themeAdapter);

        if (MainActivity.theme.equals("System")) {
            themeSpinner.setSelection(0);
        } else if (MainActivity.theme.equals("Light")) {
            themeSpinner.setSelection(1);
        } else if (MainActivity.theme.equals("Dark")) {
            themeSpinner.setSelection(2);
        }

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    MainActivity.theme = "System";
                    db.collection("Pocket Money").document(MainActivity.ID).update("theme", "System");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else if (i == 1) {
                    MainActivity.theme = "Light";
                    db.collection("Pocket Money").document(MainActivity.ID).update("theme", "Light");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (i == 2) {
                    MainActivity.theme = "Dark";
                    db.collection("Pocket Money").document(MainActivity.ID).update("theme", "Dark");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

        isEmailChangePending = true;
        isUsernameChangePending = true;
        isPasswordChangePending = true;
        isProcessRemaining = false;
        isInvalidCredentials = true;

        AlertDialog.Builder alert;
        LinearLayout layout;

        switch (view.getId()) {
            case R.id.firstnameButton:

                alert = new AlertDialog.Builder(this);
                alert.setTitle("Change Firstname");

                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText newFirstname = new EditText(this);
                newFirstname.setHint("New firstname");
                newFirstname.setLayoutParams(params);
                layout.addView(newFirstname);

                alert.setView(layout);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        firstname = newFirstname.getText().toString();
                        if (!firstname.trim().isEmpty()) {
                            db.collection("Pocket Money").document(MainActivity.ID).update("firstname", firstname);
                            Toast.makeText(getApplicationContext(), "Firstname Saved Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter valid firstname", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        }
                        if (isProcessRemaining) {
                            isProcessRemaining = false;
                            firstnameButton.performClick();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                break;

            case R.id.lastnameButton:

                alert = new AlertDialog.Builder(this);
                alert.setTitle("Change Lastname");

                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText newLastname = new EditText(this);
                newLastname.setHint("New lastname");
                newLastname.setLayoutParams(params);
                layout.addView(newLastname);

                alert.setView(layout);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        lastname = newLastname.getText().toString();
                        if (!lastname.trim().isEmpty()) {
                            db.collection("Pocket Money").document(MainActivity.ID).update("lastname", lastname);
                            Toast.makeText(getApplicationContext(), "Lastname Saved Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter valid lastname", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        }
                        if (isProcessRemaining) {
                            isProcessRemaining = false;
                            lastnameButton.performClick();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                break;

            case R.id.emailButton:

                alert = new AlertDialog.Builder(this);
                alert.setTitle("Change Email");

                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText oldEmail = new EditText(this);
                oldEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                oldEmail.setHint("Old email");
                oldEmail.setLayoutParams(params);
                layout.addView(oldEmail);

                final EditText newEmail = new EditText(this);
                newEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                newEmail.setHint("New email");
                newEmail.setLayoutParams(params);
                layout.addView(newEmail);

                final EditText confirmNewEmail = new EditText(this);
                confirmNewEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                confirmNewEmail.setHint("Confirm new email");
                confirmNewEmail.setLayoutParams(params);
                layout.addView(confirmNewEmail);

                alert.setView(layout);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        oldEmailText = oldEmail.getText().toString();
                        newEmailText = newEmail.getText().toString();
                        confirmNewEmailText = confirmNewEmail.getText().toString();

                        if (oldEmailText.trim().isEmpty() || newEmailText.trim().isEmpty() || confirmNewEmailText.trim().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else if (!newEmailText.equals(confirmNewEmailText)) {
                            Toast.makeText(getApplicationContext(), "Confirm new email not matching", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else if (oldEmailText.equals(newEmailText)) {
                            Toast.makeText(getApplicationContext(), "New email must be different", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else {

                            db.collection("Pocket Money")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (oldEmailText.equals(document.getString("email"))) {
                                                        isEmailChangePending = false;
                                                        db.collection("Pocket Money").document(MainActivity.ID).update("email", newEmailText);
                                                        Toast.makeText(getApplicationContext(), "Email changed successfully", Toast.LENGTH_LONG).show();
                                                        break;
                                                    }
                                                }
                                                if (isEmailChangePending) {
                                                    Toast.makeText(getApplicationContext(), "Invalid old email", Toast.LENGTH_LONG).show();
                                                    emailButton.performClick();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }
                        if (isProcessRemaining) {
                            isProcessRemaining = false;
                            emailButton.performClick();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                break;

            case R.id.usernameButton:

                alert = new AlertDialog.Builder(this);
                alert.setTitle("Change Username");

                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText oldUsername = new EditText(this);
                oldUsername.setHint("Old username");
                oldUsername.setLayoutParams(params);
                layout.addView(oldUsername);

                final EditText newUsername = new EditText(this);
                newUsername.setHint("New username");
                newUsername.setLayoutParams(params);
                layout.addView(newUsername);

                final EditText confirmNewUsername = new EditText(this);
                confirmNewUsername.setHint("Confirm new username");
                confirmNewUsername.setLayoutParams(params);
                layout.addView(confirmNewUsername);

                alert.setView(layout);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        oldUsernameText = oldUsername.getText().toString();
                        newUsernameText = newUsername.getText().toString();
                        confirmNewUsernameText = confirmNewUsername.getText().toString();

                        if (oldUsernameText.trim().isEmpty() || newUsernameText.trim().isEmpty() || confirmNewUsernameText.trim().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else if (!newUsernameText.equals(confirmNewUsernameText)) {
                            Toast.makeText(getApplicationContext(), "Confirm new username not matching", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else if (oldUsernameText.equals(newUsernameText)) {
                            Toast.makeText(getApplicationContext(), "New username must be different", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else {

                            db.collection("Pocket Money")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (oldUsernameText.equals(document.getString("username"))) {
                                                        isUsernameChangePending = false;
                                                        db.collection("Pocket Money").document(MainActivity.ID).update("username", newUsernameText);
                                                        Toast.makeText(getApplicationContext(), "Username changed successfully", Toast.LENGTH_LONG).show();
                                                        break;
                                                    }
                                                }
                                                if (isUsernameChangePending) {
                                                    Toast.makeText(getApplicationContext(), "Invalid old username", Toast.LENGTH_LONG).show();
                                                    usernameButton.performClick();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }
                        if (isProcessRemaining) {
                            isProcessRemaining = false;
                            usernameButton.performClick();
                        }
                    }

                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                break;

            case R.id.passwordButton:

                alert = new AlertDialog.Builder(this);
                alert.setTitle("Change Password");

                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText oldPassword = new EditText(this);
                oldPassword.setHint("Old password");
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                oldPassword.setLayoutParams(params);
                layout.addView(oldPassword);

                final EditText newPassword = new EditText(this);
                newPassword.setHint("New password");
                newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPassword.setLayoutParams(params);
                layout.addView(newPassword);

                final EditText confirmNewPassword = new EditText(this);
                confirmNewPassword.setHint("Confirm new password");
                confirmNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmNewPassword.setLayoutParams(params);
                layout.addView(confirmNewPassword);

                alert.setView(layout);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        oldPasswordText = oldPassword.getText().toString();
                        newPasswordText = newPassword.getText().toString();
                        confirmNewPasswordText = confirmNewPassword.getText().toString();

                        if (oldPasswordText.trim().isEmpty() || newPasswordText.trim().isEmpty() || confirmNewPasswordText.trim().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else if (!newPasswordText.equals(confirmNewPasswordText)) {
                            Toast.makeText(getApplicationContext(), "Confirm new password not matching", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else if (oldPasswordText.equals(newPasswordText)) {
                            Toast.makeText(getApplicationContext(), "New password must be different", Toast.LENGTH_LONG).show();
                            isProcessRemaining = true;
                        } else {

                            db.collection("Pocket Money")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (oldPasswordText.equals(document.getString("password"))) {
                                                        isPasswordChangePending = false;
                                                        db.collection("Pocket Money").document(MainActivity.ID).update("password", newPasswordText);
                                                        Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_LONG).show();
                                                        break;
                                                    }
                                                }
                                                if (isPasswordChangePending) {
                                                    Toast.makeText(getApplicationContext(), "Invalid old password", Toast.LENGTH_LONG).show();
                                                    passwordButton.performClick();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }
                        if (isProcessRemaining) {
                            isProcessRemaining = false;
                            passwordButton.performClick();
                        }
                    }

                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                break;

            case R.id.deleteAccountButton:

                alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete Account");

                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText username = new EditText(this);
                username.setHint("Enter username");
                username.setLayoutParams(params);
                layout.addView(username);

                final EditText password = new EditText(this);
                password.setHint("Enter password");
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setLayoutParams(params);
                layout.addView(password);

                alert.setView(layout);

                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        usernameText = username.getText().toString();
                        passwordText = password.getText().toString();

                        if (usernameText.trim().isEmpty() || passwordText.trim().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_LONG).show();
                            deleteButton.performClick();
                        } else {
                            db.collection("Pocket Money")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (usernameText.equals(document.getString("username")) && passwordText.equals(document.getString("password"))) {
                                                        isInvalidCredentials = false;
                                                        new AlertDialog.Builder(Settings.this)
                                                                .setTitle("Confirm deletion")
                                                                .setMessage("Are you sure you want to delete this account permanently?")
                                                                .setNegativeButton("No", null)
                                                                .setIcon(R.drawable.warning)
                                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        db.collection("Pocket Money").document(MainActivity.ID)
                                                                                .delete()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        Toast.makeText(getApplicationContext(), "Account deleted successfully", Toast.LENGTH_LONG).show();
                                                                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(getApplicationContext(), "Error deleting account!", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                .show();

                                                        break;
                                                    }
                                                }
                                                if (isInvalidCredentials) {
                                                    Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
                                                    deleteButton.performClick();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error! Data couldn't be load", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                break;

            case R.id.appPasscodeCheckBox:

                if (appPasscodeCheckBox.getTag().equals("False")) {
                    createSetPasscodeDialog();
                } else {
                    new AlertDialog.Builder(Settings.this)
                            .setTitle("Remove Passcode")
                            .setMessage("If you remove passcode then you will be asked for username and password whenever you login")
                            .setCancelable(false)
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("Pocket Money").document(MainActivity.ID).update("isAppPasscodeSet", "False");
                                    appPasscodeCheckBox.setChecked(false);
                                    appPasscodeCheckBox.setTag("False");
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (appPasscodeCheckBox.getTag().equals("True")) {
                                        appPasscodeCheckBox.setChecked(true);
                                        appPasscodeCheckBox.setTag("True");
                                    } else {
                                        appPasscodeCheckBox.setChecked(false);
                                        appPasscodeCheckBox.setTag("False");
                                    }
                                }
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                }

                break;

            case R.id.shareDataCheckBox:

                if (shareTransactionHistoryCheckBox.getTag().equals("False")) {
                    createTransactionHistoryDialog();
                } else {
                    new AlertDialog.Builder(Settings.this)
                            .setTitle("Remove sharing")
                            .setMessage("If you remove sharing that person cannot see your transaction history")
                            .setCancelable(false)
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("Pocket Money").document(MainActivity.ID).update("transactionHistorySharedWith", "False");
                                    shareTransactionHistoryCheckBox.setChecked(false);
                                    shareTransactionHistoryCheckBox.setTag("False");
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (shareTransactionHistoryCheckBox.getTag().equals("True")) {
                                        shareTransactionHistoryCheckBox.setChecked(true);
                                        shareTransactionHistoryCheckBox.setTag("True");
                                    } else {
                                        shareTransactionHistoryCheckBox.setChecked(false);
                                        shareTransactionHistoryCheckBox.setTag("False");
                                    }
                                }
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                }

                break;

            case R.id.aboutButton:

                alert = new AlertDialog.Builder(Settings.this);
                alert.setTitle("About");
                alert.setCancelable(true);

                layout = new LinearLayout(Settings.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView developedBy = new TextView(Settings.this);
                developedBy.setText("Developed : Suyog Bauskar");
                developedBy.setLayoutParams(aboutParams);
                developedBy.setTextSize(16);
                layout.addView(developedBy);

                final TextView version = new TextView(Settings.this);
                version.setText("Version : " + MainActivity.appVersion);
                version.setLayoutParams(aboutParams);
                version.setTextSize(16);
                layout.addView(version);

                alert.setView(layout);
                alert.show();

                break;
        }
    }

    public void createSetPasscodeDialog() {

        alert = new AlertDialog.Builder(Settings.this);
        alert.setTitle("Set Passcode");

        layout = new LinearLayout(Settings.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText setPasscodeField = new EditText(Settings.this);
        setPasscodeField.setHint("4 digit numeric passcode");
        setPasscodeField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        setPasscodeField.setLayoutParams(params);
        layout.addView(setPasscodeField);

        final EditText confirmSetPasscodeField = new EditText(Settings.this);
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
                    db.collection("Pocket Money").document(MainActivity.ID).update("isAppPasscodeSet", "True");
                    db.collection("Pocket Money").document(MainActivity.ID).update("appPasscode", setPasscodeText);
                    appPasscodeCheckBox.setChecked(true);
                    appPasscodeCheckBox.setTag("True");
                    Toast.makeText(getApplicationContext(), "Passcode set successfully", Toast.LENGTH_LONG).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (appPasscodeCheckBox.getTag().equals("True")) {
                    appPasscodeCheckBox.setChecked(true);
                    appPasscodeCheckBox.setTag("True");
                } else {
                    appPasscodeCheckBox.setChecked(false);
                    appPasscodeCheckBox.setTag("False");
                }
            }
        });

        alert.show();
    }

    public void createTransactionHistoryDialog() {
        alert = new AlertDialog.Builder(Settings.this);
        alert.setTitle("Share");

        layout = new LinearLayout(Settings.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView textView = new TextView(Settings.this);
        textView.setText("Enter the email with whom you want to share your transaction history");
        textView.setLayoutParams(params);
        layout.addView(textView);

        final EditText email = new EditText(Settings.this);
        email.setHint("Email");
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setLayoutParams(params);
        layout.addView(email);

        alert.setView(layout);

        alert.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String emailText = email.getText().toString();

                if (emailText.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_LONG).show();
                    createTransactionHistoryDialog();
                } else {
                    db.collection("Pocket Money").document(MainActivity.ID).update("transactionHistorySharedWith", emailText);
                    shareTransactionHistoryCheckBox.setChecked(true);
                    shareTransactionHistoryCheckBox.setTag("True");
                    Toast.makeText(getApplicationContext(), "Shared successfully", Toast.LENGTH_LONG).show();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (shareTransactionHistoryCheckBox.getTag().equals("True")) {
                    shareTransactionHistoryCheckBox.setChecked(true);
                    shareTransactionHistoryCheckBox.setTag("True");
                } else {
                    shareTransactionHistoryCheckBox.setChecked(false);
                    shareTransactionHistoryCheckBox.setTag("False");
                }
            }
        });
        alert.setIcon(R.drawable.share);
        alert.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home.class));
    }
}
