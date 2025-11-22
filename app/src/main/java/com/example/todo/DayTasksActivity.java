package com.example.todo;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayTasksActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private TextView textViewDate;
    private RecyclerView recyclerViewActive, recyclerViewCompleted;
    private Button buttonAddTask;
    private ImageButton buttonBack;
    private String dateStr;

    private ArrayList<Task> activeTasks;
    private ArrayList<Task> completedTasks;

    private TaskAdapter activeTaskAdapter;
    private CompletedTaskAdapter completedTaskAdapter;

    private ImageView undoX;
    private Calendar selectedDateCalendar;

    private Task recentlyDeletedTask = null;
    private int recentlyDeletedTaskPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_tasks);

        dbHandler = new DBHandler(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.day_tasks_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewDate = findViewById(R.id.textViewDate);
        recyclerViewActive = findViewById(R.id.recyclerViewActive);
        recyclerViewCompleted = findViewById(R.id.recyclerViewCompleted);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        buttonBack = findViewById(R.id.buttonBack);
        undoX = findViewById(R.id.undo_x);

        undoX.setVisibility(View.GONE);
        undoX.setOnClickListener(v -> undoDelete());

        SimpleDateFormat sdfServer = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("el"));
        dateStr = getIntent().getStringExtra("selectedDate");
        selectedDateCalendar = Calendar.getInstance();
        try {
            Date date = sdfServer.parse(dateStr);
            if (date != null) {
                selectedDateCalendar.setTime(date);
                String formattedDate = sdfDisplay.format(date);
                textViewDate.setText(formattedDate);
            } else {
                textViewDate.setText("Ημερομηνία");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            textViewDate.setText("Ημερομηνία");
            selectedDateCalendar = Calendar.getInstance();
        }

        buttonBack.setOnClickListener(v -> finish());

        buttonAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(DayTasksActivity.this, AddTaskActivity.class);
            intent.putExtra("selectedDate", dateStr);
            startActivity(intent);
        });

        activeTasks = new ArrayList<>();
        completedTasks = new ArrayList<>();

        int selectedDate = Task.calendarToInt(selectedDateCalendar);
        int currentDate = Task.calendarToInt(Calendar.getInstance());

        if (currentDate <= selectedDate) {
            activeTasks = dbHandler.currentTasks(selectedDate);
            completedTasks = dbHandler.completedTasks(selectedDate);
        } else {
            completedTasks = dbHandler.completedTasks(selectedDate);
        }

        activeTaskAdapter = new TaskAdapter(activeTasks,
                task -> {},
                task -> {
                    Intent intent = new Intent(DayTasksActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("title", task.getTitle());
                    intent.putExtra("description", task.getDescription());
                    intent.putExtra("priority", task.getPriority());
                    intent.putExtra("date", dateStr);
                    startActivity(intent);
                },
                task -> {
                    Intent intent = new Intent(DayTasksActivity.this, EditTaskActivity.class);
                    intent.putExtra("task_id", task.getId());
                    intent.putExtra("title", task.getTitle());
                    intent.putExtra("description", task.getDescription());
                    intent.putExtra("priority", task.getPriority());
                    intent.putExtra("due_date", task.getDueDate());
                    intent.putExtra("specific", task.getSpecific());
                    startActivity(intent);
                }
        );

        completedTaskAdapter = new CompletedTaskAdapter(completedTasks);

        recyclerViewActive.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewActive.setAdapter(activeTaskAdapter);

        recyclerViewCompleted.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCompleted.setAdapter(completedTaskAdapter);

        setupSwipeGestures();
    }

    private void setupSwipeGestures() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    deleteTask(position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    completeTask(position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX < 0) {
                        paint.setColor(Color.parseColor("#6D1D24"));
                        RectF background = new RectF(
                                itemView.getRight() + dX,
                                itemView.getTop(),
                                itemView.getRight(),
                                itemView.getBottom()
                        );
                        c.drawRect(background, paint);

                        paint.setColor(Color.WHITE);
                        paint.setTextSize(40);
                        float textWidth = paint.measureText("Delete");
                        float textX = itemView.getRight() + dX / 2 - textWidth / 2;
                        float textY = itemView.getTop() + itemView.getHeight() / 2 + paint.getTextSize() / 3;

                        c.drawText("Delete", textX, textY, paint);
                    } else if (dX > 0) {
                        paint.setColor(Color.parseColor("#4CAF50"));
                        RectF background = new RectF(
                                itemView.getLeft(),
                                itemView.getTop(),
                                itemView.getLeft() + dX,
                                itemView.getBottom()
                        );
                        c.drawRect(background, paint);

                        paint.setColor(Color.WHITE);
                        paint.setTextSize(40);
                        float textWidth = paint.measureText("Completed");
                        float textX = itemView.getLeft() + dX / 2 - textWidth / 2;
                        float textY = itemView.getTop() + itemView.getHeight() / 2 + paint.getTextSize() / 3;

                        c.drawText("Completed", textX, textY, paint);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewActive);
    }

    private void deleteTask(int position) {
        recentlyDeletedTask = activeTasks.get(position);
        recentlyDeletedTaskPosition = position;

        activeTasks.remove(position);
        activeTaskAdapter.notifyItemRemoved(position);

        dbHandler.deleteTask(recentlyDeletedTask);
        showUndoAnimation();

        Toast.makeText(this, "Διαγράφηκε: " + recentlyDeletedTask.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void undoDelete() {
        if (recentlyDeletedTask != null && recentlyDeletedTaskPosition >= 0) {
            int newID=dbHandler.addTask(recentlyDeletedTask);

            recentlyDeletedTask.setId(newID);
            activeTasks.add(recentlyDeletedTaskPosition, recentlyDeletedTask);
            activeTaskAdapter.notifyItemInserted(recentlyDeletedTaskPosition);

            recentlyDeletedTask = null;
            recentlyDeletedTaskPosition = -1;

            undoX.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> undoX.setVisibility(View.GONE))
                    .start();
            Toast.makeText(this, "Η διαγραφή αναιρέθηκε", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUndoAnimation() {
        undoX.setAlpha(1f);
        undoX.setVisibility(View.VISIBLE);

        if (undoX.getDrawable() instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) undoX.getDrawable();
            avd.start();
        }

        undoX.animate()
                .alpha(0f)
                .setDuration(3000)
                .withEndAction(() -> undoX.setVisibility(View.GONE))
                .start();
    }

    private void completeTask(int position) {
        Task task = activeTasks.get(position);

        activeTasks.remove(position);
        activeTaskAdapter.notifyItemRemoved(position);

        completedTasks.add(task);
        completedTaskAdapter.notifyItemInserted(completedTasks.size() - 1);

        dbHandler.completeTask(task);
        Toast.makeText(this, "Ολοκληρώθηκε: " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("EEEE dd MMMM yyyy", new Locale("el", "GR"));
            Date date = input.parse(isoDate);
            return output.format(date);
        } catch (ParseException e) {
            return isoDate;
        }
    }@Override
    protected void onResume() {
        super.onResume();

        int selectedDateInt = Task.calendarToInt(selectedDateCalendar);
        int currentDateInt = Task.calendarToInt(Calendar.getInstance());

        activeTasks.clear();
        completedTasks.clear();

        if (currentDateInt <= selectedDateInt) {
            activeTasks.addAll(dbHandler.currentTasks(selectedDateInt));
            activeTasks.addAll(dbHandler.currentSpecificTasks(selectedDateInt));
            completedTasks.addAll(dbHandler.completedTasks(selectedDateInt));
        } else {
            completedTasks.addAll(dbHandler.completedTasks(selectedDateInt));
        }

        activeTaskAdapter.notifyDataSetChanged();
        completedTaskAdapter.notifyDataSetChanged();
    }

}
