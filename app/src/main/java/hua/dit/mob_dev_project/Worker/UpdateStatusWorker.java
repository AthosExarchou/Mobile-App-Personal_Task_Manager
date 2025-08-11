package hua.dit.mob_dev_project.Worker;

/* imports */
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.List;
import hua.dit.mob_dev_project.DAO.TaskDAO;
import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.EmbeddedTask;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdateStatusWorker extends Worker {

    private static final String TAG = "UpdateStatusWorker";

    public UpdateStatusWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        AppDB DB = AppDB.getInstance(getApplicationContext());
        TaskDAO taskDAO = DB.taskDAO();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Result> futureResult = executor.submit(() -> {
            try {
                List<EmbeddedTask> tasks = taskDAO.getEmbeddedTasks();
                Calendar currentTime = Calendar.getInstance();
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timeFormatter.setLenient(false);

                for (EmbeddedTask task : tasks) {
                    if ("completed".equals(task.getStatusName())) {
                        continue;
                    }

                    String startTimeStr = task.getTask().getStartTime();
                    if (!startTimeStr.matches("^[0-2][0-9]:[0-5][0-9]$")) {
                        continue;
                    }

                    Date startTime = timeFormatter.parse(startTimeStr);
                    Calendar start = Calendar.getInstance();
                    start.setTime(startTime);
                    start.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
                    start.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
                    start.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));

                    Calendar endTime = (Calendar) start.clone();
                    endTime.add(Calendar.HOUR_OF_DAY, task.getTask().getDuration());

                    String newStatus = task.getStatusName();
                    if (currentTime.after(endTime)) {
                        newStatus = "expired";
                    } else if (currentTime.after(start)) {
                        newStatus = "in-progress";
                    }

                    if (!task.getStatusName().equals(newStatus)) {
                        taskDAO.updateTaskStatus(task.getTask().getId(), newStatus);
                        Log.i(TAG, "Task id: " + task.getTask().getId() + " updated to: " + newStatus);
                    }
                }
                Log.i(TAG, "Worker completed successfully");
                return Result.success();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Worker job failed");
                return Result.failure();
            }
        });

        try {
            return futureResult.get(); //waits for the background task to complete
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        } finally {
            executor.shutdown();
        }
    }
}
