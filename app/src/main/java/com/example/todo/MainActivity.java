package com.example.todo;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView dateTextView;
    private CalendarView calendarView;
    private Button addButton;
    private String selectedDateString;
    private boolean hasCheckedForOverdueTasks = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Προαιρετικό: για πλήρη χρήση της οθόνης
        setContentView(R.layout.activity_main); // Σιγουρέψου πως το layout είναι σωστά ορισμένο

        // Σύνδεση στοιχείων από το XML
        dateTextView = findViewById(R.id.dateTextView);
        calendarView = findViewById(R.id.calendarView);
        addButton = findViewById(R.id.add_button);

        // Εμφάνιση τρέχουσας ημερομηνίας
        updateDateTextView(calendarView.getDate());

        // Όταν επιλέγεται ημερομηνία από το ημερολόγιο
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            long selectedMillis = cal.getTimeInMillis();

            updateDateTextView(selectedMillis);
            selectedDateString = formatDateToServer(cal.getTime());

            // Άνοιγμα της DayTasksActivity και μεταφορά της ημερομηνίας
            Intent intent = new Intent(MainActivity.this, DayTasksActivity.class);
            intent.putExtra("selectedDate", selectedDateString);
            startActivity(intent);
        });

        // Άνοιγμα της AddTaskActivity όταν πατηθεί το κουμπί
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        // Αρχικοποίηση selectedDateString με σημερινή ημερομηνία
        selectedDateString = formatDateToServer(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBHandler dbHandler = new DBHandler(this);

        if (!hasCheckedForOverdueTasks) {
            ArrayList<Task> overdueTasks = dbHandler.getOverdueTasks();

            if (!overdueTasks.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, OverdueTasksActivity.class);
                startActivity(intent);
            }

            hasCheckedForOverdueTasks = true;
        }

        // Όταν επιστρέφουμε από άλλη δραστηριότητα, επαναφορά ημερομηνίας
        if (selectedDateString != null) {
            try {
                SimpleDateFormat sdfServer = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdfServer.parse(selectedDateString);
                if (date != null) {
                    updateDateTextView(date.getTime());
                    calendarView.setDate(date.getTime(), true, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Ενημερώνει το TextView με την επιλεγμένη ημερομηνία (μορφή ελληνική)
    private void updateDateTextView(long millis) {
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("el"));
        String formattedDate = sdfDisplay.format(new Date(millis));
        dateTextView.setText(formattedDate);
    }

    // Μορφοποιεί την ημερομηνία σε "yyyy-MM-dd" για χρήση μεταξύ δραστηριοτήτων
    private String formatDateToServer(Date date) {
        SimpleDateFormat sdfServer = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdfServer.format(date);
    }
}
