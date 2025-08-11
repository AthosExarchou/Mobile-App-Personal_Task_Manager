package hua.dit.mob_dev_project;

/* imports */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import hua.dit.mob_dev_project.Activity.EditActivity;
import hua.dit.mob_dev_project.Activity.ViewAllTasksActivity;
import hua.dit.mob_dev_project.DAO.TaskDAO;
import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.EmbeddedTask;
import hua.dit.mob_dev_project.Entity.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private static final String TAG = "TaskAdapter";
    private final List<EmbeddedTask> embeddedTasks;
    private final TaskDAO taskDAO;
    private final Context context;

    public TaskAdapter(Context context, List<EmbeddedTask> embeddedTasks) {
        this.context = context;
        this.embeddedTasks = new ArrayList<>(embeddedTasks);
        AppDB DB = AppDB.getInstance(context);
        this.taskDAO = DB.taskDAO();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        EmbeddedTask embeddedTask = embeddedTasks.get(position);
        Task task = embeddedTask.getTask();

        /* binds data to views */
        holder.taskId.setText("Task ID: " + task.getId());
        holder.taskName.setText("Name: " + task.getShortName());
        holder.taskStatus.setText("Status: " + embeddedTask.getStatusName());
        holder.taskDescription.setText("Description: " + task.getDescription());
        holder.taskStartTime.setText("Start Time: " + task.getStartTime());
        holder.taskDuration.setText("Duration: " + task.getDuration() + " hours");
        holder.taskLocation.setText("Location: " + task.getLocation());

        /* Checkbox */
        boolean isCompleted = embeddedTask.getStatusName().equalsIgnoreCase("completed");
        holder.taskCompleted.setChecked(isCompleted);
        holder.taskCompleted.setEnabled(!isCompleted);

        holder.taskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                new Thread(() -> {
                    taskDAO.updateTaskStatus(task.getId(), "completed");
                    embeddedTask.setStatusName("completed");
                    holder.itemView.post(() -> holder.taskCompleted.setEnabled(false));
                }).start();
            }
        });

        /* Edit Button Click Listener */
        holder.editTaskButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditActivity.class);
            intent.putExtra("task_id", task.getId());
            ((ViewAllTasksActivity) context).startActivityForResult(intent, 1);
        });

        /* Delete Button Click Listener */
        holder.deleteTaskButton.setOnClickListener(view -> {
            new Thread(() -> {
                Log.d(TAG, "Attempting to delete task with id: " + task.getId());
                int rowsDeleted = taskDAO.deleteTaskById(task.getId());
                Log.d(TAG, "Rows deleted: " + rowsDeleted);
                if (rowsDeleted > 0) {
                    embeddedTasks.remove(position);
                    holder.itemView.post(() -> notifyItemRemoved(position));
                } else {
                    Log.w(TAG, "No task found with id: " + task.getId());
                }
            }).start();
        });

        holder.viewOnMapButton.setOnClickListener(view -> {
            String location = task.getLocation();
            if (location != null && !location.isEmpty()) {
                /* creates a URI for a Google Maps search query */
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));

                /* creates an intent to open the location in Google Maps */
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                /* checks whether Google Maps is available and start the activity */
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                } else {
                    Toast.makeText(context, "Google Maps is currently unavailable", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please provide an appropriate location", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return embeddedTasks.size();
    }

    /* updates tasks in the adapter */
    public void updateTasks(List<EmbeddedTask> newTasks) {
        embeddedTasks.clear();
        embeddedTasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskId, taskName, taskStatus, taskDescription, taskStartTime, taskDuration, taskLocation;
        CheckBox taskCompleted;
        Button editTaskButton, deleteTaskButton, viewOnMapButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskId = itemView.findViewById(R.id.task_id);
            taskName = itemView.findViewById(R.id.task_name);
            taskStatus = itemView.findViewById(R.id.task_status);
            taskDescription = itemView.findViewById(R.id.task_description);
            taskStartTime = itemView.findViewById(R.id.task_start_time);
            taskDuration = itemView.findViewById(R.id.task_duration);
            taskLocation = itemView.findViewById(R.id.task_location);
            taskCompleted = itemView.findViewById(R.id.task_completed);
            editTaskButton = itemView.findViewById(R.id.edit_task_button);
            deleteTaskButton = itemView.findViewById(R.id.delete_task_button);
            viewOnMapButton = itemView.findViewById(R.id.view_on_map_button);
        }
    }
}
