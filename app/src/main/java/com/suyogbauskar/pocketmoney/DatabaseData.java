package com.suyogbauskar.pocketmoney;

import java.util.Date;

public class DatabaseData {
    private long amount;
    private Date date;
    private String time;
    private String description;
    private String transactionType;
    private int day;
    private int month;
    private int year;

    public DatabaseData(long amount, Date date, String time, String description, String transactionType) {
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.description = description;
        this.transactionType = transactionType;
        day = Integer.parseInt(android.text.format.DateFormat.format("dd", date).toString());
        month = Integer.parseInt(android.text.format.DateFormat.format("MM", date).toString());
        year = Integer.parseInt(android.text.format.DateFormat.format("yyyy", date).toString());
    }

    public long getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
