package hua.dit.mob_dev_project.Activity;

/* imports */
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import hua.dit.mob_dev_project.DAO.TaskDAO;
import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.Task;
import hua.dit.mob_dev_project.R;

public class DeleteActivity extends AppCompatActivity {

    private static final String TAG = "DeleteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        EditText taskIdInput = findViewById(R.id.task_id_input);
        Button deleteTaskBtn = findViewById(R.id.delete_task_button);

        /* initializes Room DB */
        AppDB DB = Room.databaseBuilder(getApplicationContext(), AppDB.class, "task_database").build();
        TaskDAO DAO = DB.taskDAO();

        /* Delete Task Button Click Listener */
        deleteTaskBtn.setOnClickListener(view -> {
            String taskIdStr = taskIdInput.getText().toString();

            /* validates input */
            if (taskIdStr.isEmpty()) {
                Toast.makeText(this, "Please enter a Task id", Toast.LENGTH_SHORT).show();
                return;
            }

            int taskId = Integer.parseInt(taskIdStr);
            Log.d(TAG, "Attempting to delete task with id: " + taskId);
            /* the deletion is done via a background thread */
            new Thread(() -> {

                Task existingTask = DAO.getTaskById(taskId);
                if (existingTask == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Task with id " + taskId + " does not exist.", Toast.LENGTH_SHORT).show());
                    return;
                }

                int rowsAffected = DAO.deleteTaskById(taskId);
                Log.d(TAG, "Rows affected: " + rowsAffected);
                /* informs the user about the result */
                runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(this, rowsAffected + " task(s) deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); //closes the activity
                    } else {
                        Toast.makeText(this, "No task found with id: " + taskId, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
