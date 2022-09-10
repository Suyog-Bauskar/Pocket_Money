package com.suyogbauskar.pocketmoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Filters extends AppCompatActivity implements View.OnClickListener {

    private Spinner monthsSpinner;
    private Spinner yearsSpinner;
    public static int month = 0;
    public static int year = 0;
    public static int yearPosition = 0;
    public static boolean isCreditCheckboxChecked = true;
    public static boolean isDebitCheckboxChecked = true;

    private Button doneButton;
    private Button resetFiltersButton;
    private CheckBox creditCheckbox;
    private CheckBox debitCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        Toolbar toolbar = findViewById(R.id.toolbarFilters);
        toolbar.setTitle("Filter");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        doneButton = findViewById(R.id.doneFiltersButton);
        resetFiltersButton = findViewById(R.id.resetFiltersButton);
        creditCheckbox = findViewById(R.id.checkbox_credit);
        debitCheckbox = findViewById(R.id.checkbox_debit);
        monthsSpinner = findViewById(R.id.months_spinner);
        yearsSpinner = findViewById(R.id.years_spinner);

        doneButton.setOnClickListener(this);
        resetFiltersButton.setOnClickListener(this);

        ArrayAdapter<CharSequence> monthsAdapter = ArrayAdapter.createFromResource(this, R.array.months_array, R.layout.spinner_item);
        monthsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            monthsAdapter = ArrayAdapter.createFromResource(this, R.array.months_array, R.layout.dark_spinner_item);
            monthsAdapter.setDropDownViewResource(R.layout.dark_spinner_dropdown_item);
        }

        monthsSpinner.setAdapter(monthsAdapter);
        monthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                month = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> yearsAdapter = ArrayAdapter.createFromResource(this, R.array.years_array, R.layout.spinner_item);
        yearsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            yearsAdapter = ArrayAdapter.createFromResource(this, R.array.years_array, R.layout.dark_spinner_item);
            yearsAdapter.setDropDownViewResource(R.layout.dark_spinner_dropdown_item);
        }

        yearsSpinner.setAdapter(yearsAdapter);
        yearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    yearPosition = 0;
                    year = 0;
                } else if (i == 1) {
                    yearPosition = i;
                    year = 2021;
                } else if (i == 2) {
                    yearPosition = i;
                    year = 2022;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        monthsSpinner.setSelection(month);
        yearsSpinner.setSelection(yearPosition);
        creditCheckbox.setChecked(isCreditCheckboxChecked);
        debitCheckbox.setChecked(isDebitCheckboxChecked);

        if (MainActivity.theme.equals("System")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (MainActivity.theme.equals("Light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (MainActivity.theme.equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkbox_credit:
                if (checked) {
                    isCreditCheckboxChecked = true;
                } else {
                    isCreditCheckboxChecked = false;
                }
                break;
            case R.id.checkbox_debit:
                if (checked) {
                    isDebitCheckboxChecked = true;
                } else {
                    isDebitCheckboxChecked = false;
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.doneFiltersButton:
                startActivity(new Intent(getApplicationContext(), Table.class));
                break;

            case R.id.resetFiltersButton:
                month = 0;
                year = 0;
                isCreditCheckboxChecked = true;
                isDebitCheckboxChecked = true;
                creditCheckbox.setChecked(true);
                debitCheckbox.setChecked(true);
                monthsSpinner.setSelection(0);
                yearsSpinner.setSelection(0);

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:
                startActivity(new Intent(getApplicationContext(), Home.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Table.class));
    }
}