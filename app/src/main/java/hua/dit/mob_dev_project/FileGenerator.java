package hua.dit.mob_dev_project;

/* imports */
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import hua.dit.mob_dev_project.Database.AppDB;
import hua.dit.mob_dev_project.Entity.EmbeddedTask;
import hua.dit.mob_dev_project.Entity.Task;

public class FileGenerator {

    private static final String TAG = "FileGenerator";
    private final Context context;

    public FileGenerator(Context context) {
        this.context = context;
    }

    public String generateContent(List<EmbeddedTask> tasks) {
        StringBuilder content = new StringBuilder();

        /* starts HTML structure */
        content.append("<html><head><style>");
        content.append("table { width: 100%; border-collapse: collapse; }");
        content.append("th, td { padding: 10px; text-align: left; border: 1px solid #ddd; }");
        content.append("th { background-color: #f2f2f2; }");
        content.append("tr:nth-child(even) { background-color: #f9f9f9; }");
        content.append("</style></head><body>");

        content.append("<h1>Incomplete Tasks</h1>");
        content.append("<table>");
        content.append("<tr><th>Task Name</th><th>Description</th><th>Start Time</th><th>Duration (hours)</th><th>Location</th></tr>");

        /* loops through the tasks and display them */
        for (EmbeddedTask taskWithStatus : tasks) {
            Task task = taskWithStatus.getTask();
            content.append("<tr>")
                    .append("<td>").append(task.getShortName()).append("</td>")
                    .append("<td>").append(task.getDescription()).append("</td>")
                    .append("<td>").append(formatStartTime(task.getStartTime())).append("</td>")
                    .append("<td>").append(task.getDuration()).append("</td>")
                    .append("<td>").append(task.getLocation()).append("</td>")
                    .append("</tr>");
        }

        content.append("</table>");
        content.append("</body></html>");

        return content.toString();
    }

    private String formatStartTime(String startTime) {
        /* assumes startTime is in HH:mm format and return it as is */
        return startTime != null ? startTime : "N/A";
    }

    public void exportToFile(String fileName, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/html");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream os = resolver.openOutputStream(uri)) {
                    os.write(content.getBytes());
                    runOnMainThread(() -> Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnMainThread(() -> Toast.makeText(context, "Error saving file.", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnMainThread(() -> Toast.makeText(context, "Failed to create file.", Toast.LENGTH_SHORT).show());
            }
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes());
                runOnMainThread(() -> Toast.makeText(context, "File saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
                runOnMainThread(() -> Toast.makeText(context, "Error saving file.", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void runOnMainThread(Runnable action) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(action);
    }

    public void generateAndExportTasks(String fileName) {
        new Thread(() -> {
            AppDB db = AppDB.getInstance(context);
            List<EmbeddedTask> incompleteTasks = db.taskDAO().getIncompleteTasks();

            if (incompleteTasks.isEmpty()) {
                /* displays a message in the case that no tasks exist */
                if (context instanceof Activity) {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "No tasks to export.", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    Log.w(TAG, "Context is not an Activity. Cannot show Toast.");
                }
                return;
            }
            /* generates HTML content */
            String content = generateContent(incompleteTasks);

            /* saves the content to a file */
            exportToFile(fileName, content);
        }).start();
    }
}
