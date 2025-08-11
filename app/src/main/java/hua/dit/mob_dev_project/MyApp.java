package hua.dit.mob_dev_project;

/* imports */
import android.app.Application;
import hua.dit.mob_dev_project.Database.AppDB;

public class MyApp extends Application {
    @Override
    public void onTerminate() {
        super.onTerminate();
        AppDB.getInstance(getApplicationContext()).closeDatabase();
    }
}
