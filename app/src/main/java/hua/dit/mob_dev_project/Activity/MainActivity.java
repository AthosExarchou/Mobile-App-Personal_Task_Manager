/*
** it2022134 Exarchou Athos **
                            */
package hua.dit.mob_dev_project.Activity;

/* imports */
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.EmbeddedTask;
import hua.dit.mob_dev_project.FileGenerator;
import hua.dit.mob_dev_project.R;
import hua.dit.mob_dev_project.Worker.UpdateStatusWorker;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final Handler handler = new Handler(Looper.getMainLooper()); //handler for scheduling tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        schedulePeriodicTaskStatusUpdate();

        /* task creation listener */
        findViewById(R.id.add_task_button).setOnClickListener(view -> {
            Log.d(TAG, "Starting AddActivity...");
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(UpdateStatusWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        });

        /* task deletion listener */
        findViewById(R.id.delete_task_button).setOnClickListener(view -> {
            Log.d(TAG, "Starting DeleteActivity...");
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(UpdateStatusWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent = new Intent(MainActivity.this, DeleteActivity.class);
            startActivity(intent);
        });

        /* incomplete-tasks viewing listener */
        findViewById(R.id.view_tasks_button).setOnClickListener(view -> {
            Log.d(TAG, "Starting ViewIncompleteTasksActivity...");
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(UpdateStatusWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent = new Intent(MainActivity.this, ViewIncompleteTasksActivity.class);
            startActivity(intent);
        });

        /* all-tasks viewing listener */
        findViewById(R.id.view_all_task_button).setOnClickListener(view -> {
            Log.d(TAG, "Starting ViewAllTasksActivity...");
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(UpdateStatusWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent = new Intent(MainActivity.this, ViewAllTasksActivity.class);
            startActivity(intent);
        });

        /* tasks exporting listener */
        findViewById(R.id.export_button).setOnClickListener(view -> {
            Log.d(TAG, "Starting File Exportation...");
            FileGenerator fileGenerator = new FileGenerator(this);
            fileGenerator.generateAndExportTasks("IncompleteTasks.html");
        });
    }

    public void schedulePeriodicTaskStatusUpdate() {
        WorkManager workManager = WorkManager.getInstance(this);

        /* creates the periodic work request */
        PeriodicWorkRequest updateRequest = new PeriodicWorkRequest.Builder(
                UpdateStatusWorker.class,
                1, TimeUnit.HOURS //runs every 1 hour
        ).build();

        /* enqueues the periodic work with unique work name */
        workManager.enqueueUniquePeriodicWork(
                "StatusUpdate",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, //cancels and re-enqueues if already running
                updateRequest  //periodic work request
        );
    }

}
