package hua.dit.mob_dev_project.DAO;

/* imports */
import android.database.Cursor;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import hua.dit.mob_dev_project.Entity.EmbeddedTask;
import hua.dit.mob_dev_project.Entity.Status;
import hua.dit.mob_dev_project.Entity.Task;

@Dao
public interface TaskDAO {

    @Insert
    long insertTask(Task task);

    @Insert
    long insertStatus(Status status);

    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("SELECT tasks.*, status.status_name FROM tasks " +
            "INNER JOIN status ON tasks.id = status.task_id")
    List<EmbeddedTask> getEmbeddedTasks();

    @Query("DELETE FROM tasks WHERE id = :taskId")
    int deleteTaskById(int taskId);

    @Query("UPDATE status SET status_name = :statusName WHERE task_id = :taskId")
    void updateTaskStatus(int taskId, String statusName);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    Task getTaskById(int taskId);

    @Update
    void updateTask(Task task);

    @Query("SELECT tasks.*, status.status_name FROM tasks " +
            "INNER JOIN status ON tasks.id = status.task_id " +
            "WHERE status.status_name != 'completed' " +
            "ORDER BY " +
            "CASE status.status_name " +
            "   WHEN 'expired' THEN 1 " +
            "   WHEN 'in-progress' THEN 2 " +
            "   WHEN 'recorded' THEN 3 " +
            "   ELSE 4 END, " +
            "tasks.start_time ASC")
    List<EmbeddedTask> getIncompleteTasks();

    @Query("SELECT * FROM tasks")
    Cursor getTasksCursor();

    @Query("SELECT * FROM tasks WHERE id = :id")
    Cursor getTaskCursorById(long id);

    @Query("DELETE FROM tasks WHERE id = :id")
    int deleteTaskById(long id);

}
