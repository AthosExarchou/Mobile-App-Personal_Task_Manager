package hua.dit.mob_dev_project.Activity;

/* imports */
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import hua.dit.mob_dev_project.DAO.TaskDAO;
import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.EmbeddedTask;
import hua.dit.mob_dev_project.R;

public class ViewIncompleteTasksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_incomplete_tasks);

        TableLayout tableLayout = findViewById(R.id.task_table);
        AppDB DB = AppDB.getInstance(getApplicationContext());
        TaskDAO DAO = DB.taskDAO();

        loadIncompleteTasks(tableLayout, DAO);
    }

    private void loadIncompleteTasks(TableLayout tableLayout, TaskDAO DAO) {
        new Thread(() -> {
            /* fetches tasks with their statuses where the status is not "completed" */
            List<EmbeddedTask> embeddedTasks = DAO.getEmbeddedTasks();

            runOnUiThread(() -> {
                tableLayout.removeAllViews(); //clears existing rows

                /* adds header row */
                TableRow headerRow = new TableRow(this);
                headerRow.addView(createTextView("Task ID"));
                headerRow.addView(createTextView("Name"));
                headerRow.addView(createTextView("Status"));
                tableLayout.addView(headerRow);

                /* adds rows dynamically */
                for (EmbeddedTask embeddedTask : embeddedTasks) {
                    if (!embeddedTask.getStatusName().equalsIgnoreCase("completed")) {
                        TableRow row = new TableRow(this);
                        row.addView(createTextView(String.valueOf(embeddedTask.getTask().getId())));
                        row.addView(createTextView(embeddedTask.getTask().getShortName()));
                        row.addView(createTextView(embeddedTask.getStatusName()));
                        tableLayout.addView(row);
                    }
                }
            });
        }).start();
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 8, 16, 8);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);

        /* makes headers bold */
        if (text.equals("Task ID") || text.equals("Name") || text.equals("Status")) {
            textView.setTypeface(null, Typeface.BOLD);
        }
        return textView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
