package com.suyogbauskar.pocketmoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Table extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<DatabaseData> userData = new ArrayList<>();
    int rowNo = 1;
    boolean isFirstRow = true;
    private String inputText;
    private TableLayout stk;
    private Long amount;

    String creditOrDebit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        Toolbar toolbar = findViewById(R.id.toolbarTransaction);
        toolbar.setTitle("History");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        stk = findViewById(R.id.table_main);

        if (Filters.isCreditCheckboxChecked && !Filters.isDebitCheckboxChecked) {
            creditOrDebit = "Credit";
        } else if (!Filters.isCreditCheckboxChecked && Filters.isDebitCheckboxChecked) {
            creditOrDebit = "Debit";
        }

        DocumentReference docRef = db.collection("Pocket Money").document(MainActivity.ID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        amount = Long.parseLong(document.getString("balance"));
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("transactions")) {
                                List transactions = (List) document.get("transactions");
                                for (Object transaction: transactions) {
                                    Map values = (Map)transaction;
                                    try {
                                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(values.get("dateToStr").toString());
                                        long amount = Long.parseLong(values.get("amount").toString());
                                        String description = values.get("description").toString();
                                        String time = values.get("timeToStr").toString();
                                        String transactionType = values.get("transactionType").toString();

                                        userData.add(new DatabaseData(amount, date, time, description, transactionType));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        //Add here

                        rowNo = 1;
                        isFirstRow = true;

                        drawTableHeader();

                        if (Filters.isDebitCheckboxChecked && Filters.isCreditCheckboxChecked && (Filters.month == 0) && (Filters.year == 0)) {

                            stk.removeViews(1, Math.max(0, stk.getChildCount() - 1));
                            rowNo = 1;
                            isFirstRow = true;

                            for (int i = userData.size() - 1; i >= 0; i--) {
                                String date = userData.get(i).getDay() + "/" + userData.get(i).getMonth() + "/" + userData.get(i).getYear();
                                createTableRow(i, date);
                            }

                        } else if (Filters.isDebitCheckboxChecked || Filters.isCreditCheckboxChecked){

                            stk.removeViews(1, Math.max(0, stk.getChildCount() - 1));
                            rowNo = 1;
                            isFirstRow = true;

                            for (int i = userData.size() - 1; i >= 0; i--) {

                                String amount = "" + userData.get(i).getAmount();
                                String date = userData.get(i).getDay() + "/" + userData.get(i).getMonth() + "/" + userData.get(i).getYear();
                                String time = userData.get(i).getTime();
                                String type = userData.get(i).getTransactionType();
                                String description = userData.get(i).getDescription();

                                if (Filters.month == 0 && Filters.year == 0 && type.equals(creditOrDebit)) {
                                    createTableRow(i, date);
                                } else if (Filters.month == 0 && userData.get(i).getYear() == Filters.year && type.equals(creditOrDebit)) {
                                    createTableRow(i, date);
                                } else if (userData.get(i).getMonth() == Filters.month && Filters.year == 0 && type.equals(creditOrDebit)) {
                                    createTableRow(i, date);
                                } else if (userData.get(i).getMonth() == Filters.month && userData.get(i).getYear() == Filters.year && type.equals(creditOrDebit)) {
                                    createTableRow(i, date);
                                } else if (Filters.isCreditCheckboxChecked && Filters.isDebitCheckboxChecked) {
                                    if (Filters.year == userData.get(i).getYear() && Filters.month == 0) {
                                        createTableRow(i, date);
                                    } else if (Filters.year == 0 && Filters.month == userData.get(i).getMonth()) {
                                        createTableRow(i, date);
                                    } else if (Filters.year == userData.get(i).getYear() && Filters.month == userData.get(i).getMonth()){
                                        createTableRow(i, date);
                                    }
                                }
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "No such document!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
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

    public void drawTableHeader() {
        TableRow tbrow0 = new TableRow(getApplicationContext());

        TextView tv = new TextView(getApplicationContext());
        TextView tv0 = new TextView(getApplicationContext());
        TextView tv1 = new TextView(getApplicationContext());
        TextView tv2 = new TextView(getApplicationContext());
        TextView tv3 = new TextView(getApplicationContext());
        TextView tv4 = new TextView(getApplicationContext());

        tv.setText("No.");
        tv0.setText("Amount");
        tv1.setText("Date");
        tv2.setText("Time");
        tv3.setText("Type");
        tv4.setText("Description");

        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv0.setTypeface(Typeface.DEFAULT_BOLD);
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        tv2.setTypeface(Typeface.DEFAULT_BOLD);
        tv3.setTypeface(Typeface.DEFAULT_BOLD);
        tv4.setTypeface(Typeface.DEFAULT_BOLD);

        tv.setTextSize(18);
        tv0.setTextSize(18);
        tv1.setTextSize(18);
        tv2.setTextSize(18);
        tv3.setTextSize(18);
        tv4.setTextSize(18);

        tv.setPadding(30,30,15,30);
        tv0.setPadding(30,30,15,30);
        tv1.setPadding(30,30,15,30);
        tv2.setPadding(30,30,15,30);
        tv3.setPadding(30,30,15,30);
        tv4.setPadding(30,30,15,30);

        tv.setGravity(Gravity.CENTER);
        tv0.setGravity(Gravity.CENTER);
        tv1.setGravity(Gravity.CENTER);
        tv2.setGravity(Gravity.CENTER);
        tv3.setGravity(Gravity.CENTER);
        tv4.setGravity(Gravity.START);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                tv.setTextColor(Color.WHITE);
                tv0.setTextColor(Color.WHITE);
                tv1.setTextColor(Color.WHITE);
                tv2.setTextColor(Color.WHITE);
                tv3.setTextColor(Color.WHITE);
                tv4.setTextColor(Color.WHITE);

                tv.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv0.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv1.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv2.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv3.setBackgroundColor(getResources().getColor(R.color.transparent));
                tv4.setBackgroundColor(getResources().getColor(R.color.transparent));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                tv.setTextColor(Color.BLACK);
                tv0.setTextColor(Color.BLACK);
                tv1.setTextColor(Color.BLACK);
                tv2.setTextColor(Color.BLACK);
                tv3.setTextColor(Color.BLACK);
                tv4.setTextColor(Color.BLACK);

                tv.setBackgroundColor(getResources().getColor(R.color.table_header));
                tv0.setBackgroundColor(getResources().getColor(R.color.table_header));
                tv1.setBackgroundColor(getResources().getColor(R.color.table_header));
                tv2.setBackgroundColor(getResources().getColor(R.color.table_header));
                tv3.setBackgroundColor(getResources().getColor(R.color.table_header));
                tv4.setBackgroundColor(getResources().getColor(R.color.table_header));
                break;
        }

        tbrow0.addView(tv);
        tbrow0.addView(tv0);
        tbrow0.addView(tv1);
        tbrow0.addView(tv2);
        tbrow0.addView(tv3);
        tbrow0.addView(tv4);

        stk.addView(tbrow0);
    }

    public void createTableRow(int i, String date) {
        TableRow tbrow = new TableRow(getApplicationContext());

        tbrow.setTag(i);

        TextView t0v = new TextView(getApplicationContext());
        TextView t1v = new TextView(getApplicationContext());
        TextView t2v = new TextView(getApplicationContext());
        TextView t3v = new TextView(getApplicationContext());
        TextView t4v = new TextView(getApplicationContext());
        TextView t5v = new TextView(getApplicationContext());

        t0v.setText("" + rowNo);
        rowNo++;
        t1v.setText("" + userData.get(i).getAmount());
        t2v.setText(date);
        t3v.setText(userData.get(i).getTime());
        t4v.setText(userData.get(i).getTransactionType());
        t5v.setText(userData.get(i).getDescription());

        t0v.setTextSize(16);
        t1v.setTextSize(16);
        t2v.setTextSize(16);
        t3v.setTextSize(16);
        t4v.setTextSize(16);
        t5v.setTextSize(16);

        t0v.setPadding(30, 30, 15, 30);
        t1v.setPadding(30, 30, 15, 30);
        t2v.setPadding(30, 30, 15, 30);
        t3v.setPadding(30, 30, 15, 30);
        t4v.setPadding(30, 30, 15, 30);
        t5v.setPadding(30, 30, 15, 30);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                t0v.setTextColor(Color.WHITE);
                t1v.setTextColor(Color.WHITE);
                t2v.setTextColor(Color.WHITE);
                t3v.setTextColor(Color.WHITE);
                t4v.setTextColor(Color.WHITE);
                t5v.setTextColor(Color.WHITE);

                if (isFirstRow) {
                    t0v.setBackgroundColor(getResources().getColor(R.color.table_row_white_transparent));
                    t1v.setBackgroundColor(getResources().getColor(R.color.table_row_white_transparent));
                    t2v.setBackgroundColor(getResources().getColor(R.color.table_row_white_transparent));
                    t3v.setBackgroundColor(getResources().getColor(R.color.table_row_white_transparent));
                    t4v.setBackgroundColor(getResources().getColor(R.color.table_row_white_transparent));
                    t5v.setBackgroundColor(getResources().getColor(R.color.table_row_white_transparent));
                    isFirstRow = false;
                } else {
                    isFirstRow = true;
                }
                break;
            case Configuration.UI_MODE_NIGHT_NO:

                t0v.setBackgroundResource(R.drawable.borders);
                t1v.setBackgroundResource(R.drawable.borders);
                t2v.setBackgroundResource(R.drawable.borders);
                t3v.setBackgroundResource(R.drawable.borders);
                t4v.setBackgroundResource(R.drawable.borders);
                t5v.setBackgroundResource(R.drawable.borders);

                t0v.setTextColor(Color.BLACK);
                t1v.setTextColor(Color.BLACK);
                t2v.setTextColor(Color.BLACK);
                t3v.setTextColor(Color.BLACK);
                t4v.setTextColor(Color.BLACK);
                t5v.setTextColor(Color.BLACK);

                if (isFirstRow) {
                    t0v.setBackgroundColor(getResources().getColor(R.color.white));
                    t1v.setBackgroundColor(getResources().getColor(R.color.white));
                    t2v.setBackgroundColor(getResources().getColor(R.color.white));
                    t3v.setBackgroundColor(getResources().getColor(R.color.white));
                    t4v.setBackgroundColor(getResources().getColor(R.color.white));
                    t5v.setBackgroundColor(getResources().getColor(R.color.white));
                    isFirstRow = false;
                } else {
                    t0v.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    t1v.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    t2v.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    t3v.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    t4v.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    t5v.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    isFirstRow = true;
                }
                break;
        }

        t0v.setGravity(Gravity.CENTER);
        t1v.setGravity(Gravity.CENTER);
        t2v.setGravity(Gravity.CENTER);
        t3v.setGravity(Gravity.CENTER);
        t4v.setGravity(Gravity.CENTER);
        t5v.setGravity(Gravity.START);

        tbrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new AlertDialog.Builder(Table.this)
                        .setTitle("Delete record")
                        .setMessage("Are you sure you want to delete this record permanently?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                long amountToDelete = userData.get((Integer) tbrow.getTag()).getAmount();
                                String day = userData.get((Integer) tbrow.getTag()).getDay() + "";
                                String month = userData.get((Integer) tbrow.getTag()).getMonth() + "";
                                String year = userData.get((Integer) tbrow.getTag()).getYear() + "";
                                if (day.length() == 1) {
                                    day = "0" + day;
                                }
                                if (month.length() == 1) {
                                    month = "0" + month;
                                }
                                String dateToDelete = day + "/" + month + "/" + year;
                                String timeToDelete = userData.get((Integer) tbrow.getTag()).getTime();
                                String typeToDelete = " " + userData.get((Integer) tbrow.getTag()).getTransactionType();
                                String descriptionToDelete = userData.get((Integer) tbrow.getTag()).getDescription();

                                if (userData.get((Integer) tbrow.getTag()).getTransactionType().equals("Credit")) {
                                    amount -= amountToDelete;
                                } else if (userData.get((Integer) tbrow.getTag()).getTransactionType().equals("Debit")) {
                                    amount += amountToDelete;
                                }

                                db.collection("Pocket Money").document(MainActivity.ID).update("transactions", FieldValue.arrayRemove(new Data(amountToDelete, dateToDelete, timeToDelete, typeToDelete, descriptionToDelete)));
                                db.collection("Pocket Money").document(MainActivity.ID).update("balance", amount + "");
                                Toast.makeText(getApplicationContext(), "Record deleted", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), Table.class));
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(R.drawable.warning)
                        .show();

                return true;
            }
        });

        tbrow.addView(t0v);
        tbrow.addView(t1v);
        tbrow.addView(t2v);
        tbrow.addView(t3v);
        tbrow.addView(t4v);
        tbrow.addView(t5v);

        stk.addView(tbrow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.table_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.search:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Search");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setNeutralButton("Show All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Filters.yearPosition = 0;
                        Filters.month = 0;
                        Filters.isCreditCheckboxChecked = true;
                        Filters.isDebitCheckboxChecked = true;
                        startActivity(new Intent(getApplicationContext(), Table.class));
                    }
                });
                builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputText = input.getText().toString();
                        if (input.getText().toString().trim().isEmpty()) {
                            dialog.cancel();
                            return;
                        }

                        stk.removeViews(1, Math.max(0, stk.getChildCount() - 1));
                        rowNo = 1;
                        isFirstRow = true;

                        for (int i = userData.size() - 1; i >= 0; i--) {
                            String amount = "" + userData.get(i).getAmount();
                            String date = userData.get(i).getDay() + "/" + userData.get(i).getMonth() + "/" + userData.get(i).getYear();
                            String time = userData.get(i).getTime();
                            String type = userData.get(i).getTransactionType();
                            String description = userData.get(i).getDescription();

                            if (amount.equals(inputText) || date.equals(inputText) || time.equals(inputText) || type.equals(inputText) || description.equals(inputText)) {

                                createTableRow(i, date);

                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return true;
            case R.id.filter:
                startActivity(new Intent(getApplicationContext(), Filters.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home.class));
    }
}