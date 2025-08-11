package hua.dit.mob_dev_project.Activity;

/* imports */
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import hua.dit.mob_dev_project.DAO.TaskDAO;
import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.Task;
import hua.dit.mob_dev_project.R;

public class EditActivity extends AppCompatActivity {

    private Task task;
    private TaskDAO taskDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        /* initializes UI elements */
        EditText nameEditText = findViewById(R.id.edit_name);
        EditText descriptionEditText = findViewById(R.id.edit_description);
        EditText startTimeEditText = findViewById(R.id.startTimeInput);
        EditText durationEditText = findViewById(R.id.edit_duration);
        EditText locationEditText = findViewById(R.id.edit_location_input);
        Button saveButton = findViewById(R.id.save_changes_button);

        /* fetches task id from Intent */
        int taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Invalid task id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        /* initializes the DB and gets the task */
        AppDB DB = AppDB.getInstance(getApplicationContext());
        TaskDAO taskDAO = DB.taskDAO();

        /* fetches the task from the database */
        new Thread(() -> {
            Task task = taskDAO.getTaskById(taskId);
            if (task != null) {
                runOnUiThread(() -> {
                    /* inserts the existing task data into the corresponding fields */
                    nameEditText.setText(task.getShortName());
                    descriptionEditText.setText(task.getDescription());
                    startTimeEditText.setText(task.getStartTime());
                    durationEditText.setText(String.valueOf(task.getDuration()));
                    locationEditText.setText(task.getLocation());
                });
            }
        }).start();

        /*  Save Button Click Listener */
        saveButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String startTime =  startTimeEditText.getText().toString().trim();
            String durationStr = durationEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();

            /* validates input */
            if (name.isEmpty() || startTime.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(this, "Name, Start Time and Duration are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            /* validates Start Time format */
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                sdf.setLenient(false);
                sdf.parse(startTime); //validates the format
            } catch (ParseException e) {
                Toast.makeText(this, "Start Time must be in HH:mm format", Toast.LENGTH_SHORT).show();
                return;
            }

            /* validates Duration format */
            int duration;
            try {
                duration = Integer.parseInt(durationStr);
                if (duration <= 0) {
                    Toast.makeText(this, "Duration must be a positive number!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid duration format!", Toast.LENGTH_SHORT).show();
                return;
            }

            /* updates the task in the database */
            new Thread(() -> {
                Task EditedTask = taskDAO.getTaskById(taskId);

                if (EditedTask != null) {
                    EditedTask.setShortName(name);
                    EditedTask.setDescription(description);
                    EditedTask.setStartTime(startTime);
                    EditedTask.setDuration(duration);
                    EditedTask.setLocation(location);

                    taskDAO.updateTask(EditedTask);

                    /* notifies the previous activity about the changes */
                    runOnUiThread(() -> {
                        setResult(RESULT_OK);
                        Toast.makeText(this, "Task changes saved!", Toast.LENGTH_SHORT).show();
                        finish(); //closes the EditActivity
                    });
                }
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
