package com.cleanup.todoc.injection;

import android.content.Context;

import com.cleanup.todoc.database.SaveDatabase;
import com.cleanup.todoc.repository.ProjectDataRepository;
import com.cleanup.todoc.repository.TaskDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static TaskDataRepository provideTaskData(Context context) {
        SaveDatabase database = SaveDatabase.getInstance(context);
        return new TaskDataRepository(database.taskDao());
    }

    public static ProjectDataRepository provideProjectData(Context context) {
        SaveDatabase database = SaveDatabase.getInstance(context);
        return new ProjectDataRepository(database.projectDao());
    }

    public static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        TaskDataRepository taskDataRepository = provideTaskData(context);
        ProjectDataRepository projectDataRepository = provideProjectData(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(taskDataRepository, projectDataRepository, executor);
    }
}
