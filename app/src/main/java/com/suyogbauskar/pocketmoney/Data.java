package com.suyogbauskar.pocketmoney;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Data {
    private Date date;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private String dateToStr;
    private String timeToStr;
    private long amount;
    private String transactionType;
    private String description;

    public Data(long amount, String transactionType, String description) {
        this.date = new Date();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateToStr = dateFormat.format(date);
        timeToStr = timeFormat.format(date);
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
    }

    public Data(long amount, String dateToStr, String timeToStr, String transactionType, String description) {
        this.dateToStr = dateToStr;
        this.timeToStr = timeToStr;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
    }

    public String getDateToStr() {
        return dateToStr;
    }

    public String getTimeToStr() {
        return timeToStr;
    }

    public String getAmount() {
        return amount + "";
    }

    public String getTransactionType() {
        return transactionType.substring(1);
    }

    public String getDescription() {
        return description;
    }
}
