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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private TextInputEditText titleEditText, descriptionEditText;
    private Button datePickerButton;
    private RadioButton lowPriority, mediumPriority, highPriority;
    private ImageButton buttonBack;
    private Calendar selectedDate = Calendar.getInstance();
    private int taskId = -1;
    private Switch specificSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_task_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHandler = new DBHandler(this);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        datePickerButton = findViewById(R.id.datePickerButton);
        lowPriority = findViewById(R.id.lowPriority);
        mediumPriority = findViewById(R.id.mediumPriority);
        highPriority = findViewById(R.id.highPriority);
        buttonBack = findViewById(R.id.buttonBack);
        specificSwitch = findViewById(R.id.specificSwitch);

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra("task_id", -1);
            String title = getIntent().getStringExtra("title");
            String description = getIntent().getStringExtra("description");
            int priority = getIntent().getIntExtra("priority", 0); // default = low
            int dueDateInt = getIntent().getIntExtra("due_date", -1);
            int specific = getIntent().getIntExtra("specific", 0);

            if (title != null) titleEditText.setText(title);
            if (description != null) descriptionEditText.setText(description);

            // Προσαρμοσμένο priority matching
            switch (priority) {
                case 2:
                    highPriority.setChecked(true);
                    break;
                case 1:
                    mediumPriority.setChecked(true);
                    break;
                default:
                    lowPriority.setChecked(true);
                    break;
            }

            if (dueDateInt != -1) {
                selectedDate = Task.intToCalendar(dueDateInt);
            }
            if (specific==1)
            {
                specificSwitch.setChecked(true);
            }
        }

        updateDateButtonText();

        buttonBack.setOnClickListener(v -> finish());
        datePickerButton.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.saveButton).setOnClickListener(v -> saveTask());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
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

        // Προσαρμογή priority mapping
        int priority = highPriority.isChecked() ? 2 :
                mediumPriority.isChecked() ? 1 : 0;

        int dueDateInt = Task.calendarToInt(selectedDate);
        int specific = specificSwitch.isChecked() ? 1 : 0;

        Task task = (taskId != -1)
                ? new Task(title, description, priority, dueDateInt, specific, taskId) //need the button
                : new Task(title, description, priority,dueDateInt, specific); //need the button

        dbHandler.editTask(task);
        Toast.makeText(this, "Η εργασία αποθηκεύτηκε", Toast.LENGTH_SHORT).show();
        finish();
    }
}
