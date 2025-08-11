package hua.dit.mob_dev_project.Activity;

/* imports */
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import hua.dit.mob_dev_project.DAO.TaskDAO;
import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.EmbeddedTask;
import hua.dit.mob_dev_project.R;
import hua.dit.mob_dev_project.TaskAdapter;

public class ViewAllTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_tasks);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(taskAdapter);

        /* loads tasks into the RecyclerView */
        loadTasks();
    }

    private void loadTasks() {
        new Thread(() -> {
            AppDB DB = AppDB.getInstance(getApplicationContext());
            TaskDAO DAO = DB.taskDAO();

            /* fetches tasks with their statuses */
            List<EmbeddedTask> embeddedTasks = DAO.getEmbeddedTasks();

            /* updates the RecyclerView via main thread */
            runOnUiThread(() -> {
                if (taskAdapter == null) {
                    taskAdapter = new TaskAdapter(this, embeddedTasks);
                    recyclerView.setAdapter(taskAdapter);
                } else {
                    /* updates data */
                    taskAdapter.updateTasks(embeddedTasks);
                }
            });
        }).start();
    }

    /* handles the result from EditTaskActivity */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            /* reloads tasks to reflect changes after editing */
            loadTasks();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks(); //reloads tasks from the database
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
