package hua.dit.mob_dev_project.Entity;

/* imports */
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "short_name")
    private String shortName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "start_time")
    private String startTime; //in 'HH:mm' format

    @ColumnInfo(name = "duration")
    private int duration; //in hours

    @ColumnInfo(name = "location")
    private String location;

    public Task(String shortName, String description, String startTime, int duration, String location) {
        this.shortName = shortName;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.location = location;
    }

    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
}
