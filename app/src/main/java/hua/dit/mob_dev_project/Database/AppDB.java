package hua.dit.mob_dev_project.Database;

/* imports */
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import hua.dit.mob_dev_project.DAO.TaskDAO;
import hua.dit.mob_dev_project.Entity.Status;
import hua.dit.mob_dev_project.Entity.Task;

@Database(entities = {Task.class, Status.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract TaskDAO taskDAO();

    private static volatile AppDB INSTANCE;

    public static AppDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDB.class, "task_database")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    db.execSQL("PRAGMA foreign_keys = ON;");
                                }
                            })
                            .fallbackToDestructiveMigration()
                            .build();

                }
            }
        }
        return INSTANCE;
    }

    public void closeDatabase() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }

}
