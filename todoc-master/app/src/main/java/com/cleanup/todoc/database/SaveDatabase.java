package com.cleanup.todoc.database;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.database.dao.TaskDao;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

@Database(entities = {Task.class, Project.class}, version = 1, exportSchema = false)
public abstract class SaveDatabase extends RoomDatabase {

    private static final Project[] allProjects = Project.getAllProjects();
    private static SaveDatabase Instance;

    public abstract TaskDao taskDao();
    public abstract ProjectDao projectDao();

    public static synchronized SaveDatabase getInstance(Context context) {
        if (Instance == null) {
            Instance = Room.databaseBuilder(context.getApplicationContext(),
                     SaveDatabase.class, "MyDatabase.db").addCallback(prepopulateDatabase())
                    .build();
        }
        return Instance;
    }

    private static Callback prepopulateDatabase() {
        return new Callback() {

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                for (Project project : allProjects) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id",project.getId());
                    contentValues.put("name", project.getName());
                    contentValues.put("color", project.getColor());

                    db.insert("project_table", OnConflictStrategy.IGNORE, contentValues);
                }
            }
        };
    }
}
