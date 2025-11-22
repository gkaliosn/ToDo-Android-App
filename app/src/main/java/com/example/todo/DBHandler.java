package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "tasksDB.db";
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COMPLETED = "completed";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_SPECIFIC = "specific";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHandler(Context context, String name,
                     SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER, " +
                COLUMN_DATE + " INTEGER, " +
                COLUMN_PRIORITY + " INTEGER, " +
                COLUMN_SPECIFIC + " INTEGER " +
                ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public int addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DATE, task.getDueDate());
        values.put(COLUMN_COMPLETED,0);
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_SPECIFIC, task.getSpecific());

        long newID = db.insert(TABLE_TASKS, null, values);
        db.close();
        return (int) newID;
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query="DELETE FROM " + TABLE_TASKS +
                " WHERE " + COLUMN_ID + " = "+ task.getId();
        db.execSQL(query);
        db.close();
    }
    public void editTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DATE, task.getDueDate());
        values.put(COLUMN_COMPLETED, 0);
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_SPECIFIC, task.getSpecific());

        db.update(TABLE_TASKS, values, COLUMN_ID + "=" + task.getId(), null);
        db.close();
    }


    public void completeTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, 1);
        values.put(COLUMN_DATE, Task.calendarToInt(Calendar.getInstance()));   // Ενημερωνουμε date για completion_Dates
        db.update(TABLE_TASKS, values, COLUMN_ID + "=" + task.getId(), null);
        db.close();
    }

    public ArrayList<Task> currentTasks(int currentDate) {

        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * " +
                "FROM " + TABLE_TASKS +
                " WHERE " + COLUMN_DATE + " >= " + currentDate + " AND " + COLUMN_COMPLETED + "=0 AND " + COLUMN_SPECIFIC + "=0";

        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                Task task = new Task(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SPECIFIC)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                );

                tasks.add(task);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return tasks;
    }

    public ArrayList<Task> completedTasks(int currentDate) {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * " +
                "FROM " + TABLE_TASKS +
                " WHERE " + COLUMN_DATE + " = " + currentDate +" AND " + COLUMN_COMPLETED + " =1";

        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                Task task = new Task(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SPECIFIC)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                );
                tasks.add(task);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return tasks;
    }

    public ArrayList<Task> currentSpecificTasks(int currentDate) {

        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * " +
                "FROM " + TABLE_TASKS +
                " WHERE " + COLUMN_DATE + " = " + currentDate + " AND " + COLUMN_COMPLETED + "=0 AND " + COLUMN_SPECIFIC + "=1";

        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                Task task = new Task(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SPECIFIC)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                );
                tasks.add(task);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return tasks;
    }
    public ArrayList<Task> expiredTasks(){
        ArrayList<Task> tasks = new ArrayList<>();
        long currentDay = Task.calendarToInt(Calendar.getInstance());
        SQLiteDatabase db= this.getReadableDatabase();

        String query = "SELECT * " +
                "FROM " + TABLE_TASKS +
                " WHERE " + COLUMN_DATE + " <= " + currentDay + " AND " + COLUMN_COMPLETED + "=0";

        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                Task task = new Task(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SPECIFIC)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                );
                tasks.add(task);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return tasks;
    }

    //Δεν νομιζω οτι χρειαζεται το βλεπουμε
    public ArrayList<Task> findTask(String date) {
        String query = "SELECT * FROM " + TABLE_TASKS +
                " WHERE " + COLUMN_DATE + " <= " + date;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Task task = new Task(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SPECIFIC)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                );
                tasks.add(task);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return tasks;
    }

    public ArrayList<Task> getOverdueTasks() {
        ArrayList<Task> overdueTasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            int today = Task.calendarToInt(Calendar.getInstance());

            String query = "SELECT * FROM " + TABLE_TASKS +
                    " WHERE " + COLUMN_DATE + " < ? AND " + COLUMN_COMPLETED + " = ?";

            String[] selectionArgs = { String.valueOf(today), "0" };

            cursor = db.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    Task task = new Task(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SPECIFIC)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    );
                    overdueTasks.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return overdueTasks;
    }

}
