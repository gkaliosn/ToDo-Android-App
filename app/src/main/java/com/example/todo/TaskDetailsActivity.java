package com.example.todo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.task_details_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView titleHeader = findViewById(R.id.taskTitleHeader);
        TextView dueDateView = findViewById(R.id.textViewDueDate);
        TextView priorityView = findViewById(R.id.textViewPriority);
        TextView descriptionLabel = findViewById(R.id.textViewDescriptionLabel);
        TextView descriptionValue = findViewById(R.id.textViewDescriptionValue);
        ImageView priorityIndicator = findViewById(R.id.priorityIndicator);
        ImageButton buttonBack = findViewById(R.id.buttonBack);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        int priority = intent.getIntExtra("priority", -1);
        String date = intent.getStringExtra("date");

        titleHeader.setText(title);

        buttonBack.setOnClickListener(v -> finish());
        // Date
        SpannableString dueSpan = new SpannableString("Due date: " + date);
        dueSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        dueDateView.setText(dueSpan);

        // Priority
        String priorityText;
        switch (priority) {
            case 0:
                priorityText = "Low";
                priorityIndicator.setImageResource(R.drawable.circle_green);
                break;
            case 1:
                priorityText = "Medium";
                priorityIndicator.setImageResource(R.drawable.circle_yellow);
                break;
            case 2:
                priorityText = "High";
                priorityIndicator.setImageResource(R.drawable.circle_red);
                break;
            default:
                priorityText = "—";
                priorityIndicator.setImageDrawable(null);
        }
        SpannableString prioritySpan = new SpannableString("Priority: " + priorityText);
        prioritySpan.setSpan(new StyleSpan(Typeface.BOLD), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        priorityView.setText(prioritySpan);

        // Description
        SpannableString descLabel = new SpannableString("Description:");
        descLabel.setSpan(new StyleSpan(Typeface.BOLD), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descriptionLabel.setText(descLabel);
        descriptionValue.setText((description == null || description.isEmpty()) ? "—" : description);

    }
}
