package com.example.todo;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OverdueTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOverdue;
    private TaskAdapter adapter;
    private ArrayList<Task> overdueTasks;
    private DBHandler dbHandler;
    private ImageButton buttonBack;
    private ImageView undoX;

    private Task recentlyDeletedTask = null;
    private int recentlyDeletedTaskPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overdue_tasks);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.overdue_tasks_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewOverdue = findViewById(R.id.recyclerViewOverdue);
        buttonBack = findViewById(R.id.buttonBack);
        undoX = findViewById(R.id.undo_x);
        undoX.setVisibility(View.GONE);
        undoX.setOnClickListener(v -> undoDelete());

        dbHandler = new DBHandler(this);
        overdueTasks = dbHandler.getOverdueTasks();

        adapter = new TaskAdapter(
                overdueTasks,
                task -> {}, // Δεν διαγράφουμε από εδώ, γίνεται μέσω swipe
                task -> {
                    Intent intent = new Intent(OverdueTasksActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("title", task.getTitle());
                    intent.putExtra("description", task.getDescription());
                    intent.putExtra("priority", task.getPriority());
                    intent.putExtra("date", task.getDueDate());
                    startActivity(intent);
                },
                task -> {
                    Intent intent = new Intent(OverdueTasksActivity.this, EditTaskActivity.class);
                    intent.putExtra("task_id", task.getId());
                    intent.putExtra("title", task.getTitle());
                    intent.putExtra("description", task.getDescription());
                    intent.putExtra("priority", task.getPriority());
                    intent.putExtra("due_date", task.getDueDate());
                    intent.putExtra("specific", task.getSpecific());
                    startActivity(intent);
                }
        );

        recyclerViewOverdue.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOverdue.setAdapter(adapter);

        setupSwipeGestures();

        buttonBack.setOnClickListener(v -> finish());
    }

    private void setupSwipeGestures() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                recentlyDeletedTask = overdueTasks.get(position);
                recentlyDeletedTaskPosition = position;

                overdueTasks.remove(position);
                adapter.notifyItemRemoved(position);
                dbHandler.deleteTask(recentlyDeletedTask);
                showUndoAnimation();

                Toast.makeText(OverdueTasksActivity.this, "Διαγράφηκε: " + recentlyDeletedTask.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
                    paint.setColor(Color.parseColor("#6D1D24")); // Κόκκινο
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
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerViewOverdue);
    }

    private void undoDelete() {
        if (recentlyDeletedTask != null && recentlyDeletedTaskPosition >= 0) {
            int newID = dbHandler.addTask(recentlyDeletedTask);
            recentlyDeletedTask.setId(newID);
            overdueTasks.add(recentlyDeletedTaskPosition, recentlyDeletedTask);
            adapter.notifyItemInserted(recentlyDeletedTaskPosition);

            recentlyDeletedTask = null;
            recentlyDeletedTaskPosition = -1;

            undoX.animate().alpha(0f).setDuration(300).withEndAction(() -> undoX.setVisibility(View.GONE)).start();
            Toast.makeText(this, "Η διαγραφή αναιρέθηκε", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUndoAnimation() {
        undoX.setAlpha(1f);
        undoX.setVisibility(View.VISIBLE);

        if (undoX.getDrawable() instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) undoX.getDrawable()).start();
        }

        undoX.animate().alpha(0f).setDuration(3000).withEndAction(() -> undoX.setVisibility(View.GONE)).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overdueTasks.clear();
        overdueTasks.addAll(dbHandler.getOverdueTasks());
        adapter.notifyDataSetChanged();
    }
}
