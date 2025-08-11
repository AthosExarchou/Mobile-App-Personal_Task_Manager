package hua.dit.mob_dev_project.Entity;

/* imports */
import androidx.room.ColumnInfo;
import androidx.room.Embedded;

public class EmbeddedTask {

    @Embedded
    public Task task;

    @ColumnInfo(name = "status_name")
    public String statusName;

    public EmbeddedTask(Task task, String statusName) {
        this.task = task;
        this.statusName = statusName;
    }

    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }
    public String getStatusName() {
        return statusName;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

}
