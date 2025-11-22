package com.example.todo;

import java.util.Calendar;

public class Task {
    private int id;
    private String title;
    private String description;
    private int priority;
    private int dueDate; // as integer (yyyymmdd)
    private int specific;

    public Task(String title, String description, int priority, int dueDate, int specific) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.specific= specific;
    }

    public Task(String title, String description, int priority, int dueDate, int specific,int id) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.specific= specific;
        this.id = id;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
    public int getDueDate() {
        return dueDate;
    }
    public int getSpecific(){return specific;}

    //Setters

    public void setId(int id)
    {
        this.id=id;
    }

    public void setTitle(String title) { this.title= title; }
    public void setDescription(String description) { this.description = description; }

    public void setPriority(int priority) { this.priority = priority; }
    public void setDueDate(int dueDate) { this.dueDate = dueDate; }
    public void setSpecific(int specific) { this.specific = specific; }

    // Κενός constructor για αρχικοποίηση χωρίς παραμέτρους
    public Task() {

    }


    // Static helper methods
    public static int calendarToInt(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year * 10000 + month * 100 + day;
    }

    public static Calendar intToCalendar(int dateInt) {
        int year = dateInt / 10000;
        int month = (dateInt % 10000) / 100 - 1; // Calendar.MONTH is 0-based
        int day = dateInt % 100;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar;
    }
}
