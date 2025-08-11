package hua.dit.mob_dev_project.Activity;

/* imports */
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import hua.dit.mob_dev_project.DAO.TaskDAO;
import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.Status;
import hua.dit.mob_dev_project.Entity.Task;
import hua.dit.mob_dev_project.R;
import hua.dit.mob_dev_project.Worker.UpdateStatusWorker;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(UpdateStatusWorker.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);

        /* initializes the Room DB */
        AppDB DB = Room.databaseBuilder(getApplicationContext(), AppDB.class, "task_database").build();
        TaskDAO DAO = DB.taskDAO();

        EditText taskNameEditText = findViewById(R.id.name);
        EditText taskDescriptionEditText = findViewById(R.id.description);
        EditText taksStartTimeEditText = findViewById(R.id.startTime_input);
        EditText durationEditText = findViewById(R.id.duration);
        EditText locationEditText = findViewById(R.id.location_input);
        Button saveTaskButton = findViewById(R.id.save_task_button);

        saveTaskButton.setOnClickListener(view -> {
            String taskName = taskNameEditText.getText().toString();
            String taskDescription = taskDescriptionEditText.getText().toString();
            String taskStartTime =  taksStartTimeEditText.getText().toString();
            String taskDuration = durationEditText.getText().toString();
            String taskLocation = locationEditText.getText().toString();

            /* validates required fields */
            if (taskName.isEmpty() || taskStartTime.isEmpty() || taskDuration.isEmpty()) {
                Toast.makeText(this, "Name, Start Time and Duration are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            /* validates Duration format */
            int duration;
            try {
                duration = Integer.parseInt(taskDuration);
                if (duration <= 0) {
                    Toast.makeText(this, "Duration must be a positive number!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid duration format!", Toast.LENGTH_SHORT).show();
                return;
            }

            /* validates Start Time format */
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                sdf.setLenient(false);
                sdf.parse(taskStartTime); //validates the format
            } catch (ParseException e) {
                Toast.makeText(this, "Start Time must be in HH:mm format", Toast.LENGTH_SHORT).show();
                return;
            }

            /* creates Task */
            Task task = new Task(taskName, taskDescription, taskStartTime, duration, taskLocation);

            /* inserts Task and Status into the Room DB */
            new Thread(() -> {
                /* inserts task and retrieves its id */
                long taskId = DAO.insertTask(task);

                if (taskId > 0) {
                    /* inserts default status for the new task */
                    Status status = new Status((int) taskId, "recorded");
                    DAO.insertStatus(status);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Task added successfully with status 'recorded'!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to add task. Please try again.", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
