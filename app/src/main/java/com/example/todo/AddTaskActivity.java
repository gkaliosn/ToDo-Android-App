package com.example.todo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    DBHandler dbHandler;
    private TextInputEditText titleEditText, descriptionEditText;
    private Button datePickerButton;
    private RadioButton lowPriority, mediumPriority, highPriority;
    private ImageButton buttonBack;
    private Calendar selectedDate = Calendar.getInstance();
    private Switch specificSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_task_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Αρχικοποίηση views
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        datePickerButton = findViewById(R.id.datePickerButton);
        lowPriority = findViewById(R.id.lowPriority);
        mediumPriority = findViewById(R.id.mediumPriority);
        highPriority = findViewById(R.id.highPriority);
        buttonBack = findViewById(R.id.buttonBack);
        specificSwitch = findViewById(R.id.specificSwitch);


        // Αν υπάρχει ημερομηνία από Intent, ορισμός της
        String dateStr = getIntent().getStringExtra("selectedDate");
        if (dateStr != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(dateStr);
                if (date != null) {
                    selectedDate.setTime(date);
                    updateDateButtonText();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Ενημέρωση κουμπιού ημερομηνίας με την τρέχουσα ημερομηνία
        updateDateButtonText();

        buttonBack.setOnClickListener(v -> finish());
        datePickerButton.setOnClickListener(v -> showDatePicker());

        findViewById(R.id.saveButton).setOnClickListener(v -> saveTask());

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateButtonText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("el"));
        datePickerButton.setText(sdf.format(selectedDate.getTime()));
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        if (title.isEmpty()) {
            titleEditText.setError("Ο τίτλος είναι υποχρεωτικός");
            return;
        }

        String description = descriptionEditText.getText().toString().trim();

        // Καθορισμός προτεραιότητας
        int priority;
        if (highPriority.isChecked()) {
            priority = 2;
        } else if (mediumPriority.isChecked()) {
            priority = 1;
        } else {
            priority = 0;
        }

        int specific = specificSwitch.isChecked() ? 1 : 0;

        Task newTask = new Task(
                title,
                description,
                priority,
                Task.calendarToInt(selectedDate),
                specific
        );

        dbHandler = new DBHandler(this);
        dbHandler.addTask(newTask);

        Toast.makeText(this, "Η εργασία αποθηκεύτηκε", Toast.LENGTH_SHORT).show();
        finish();
    }

}
