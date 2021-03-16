package com.cleanup.todoc.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cleanup.todoc.repository.ProjectDataRepository;
import com.cleanup.todoc.repository.TaskDataRepository;
import com.cleanup.todoc.viewModel.TaskViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final TaskDataRepository taskData;
    private final ProjectDataRepository projectData;
    private final Executor executor;

    public ViewModelFactory(TaskDataRepository taskData, ProjectDataRepository projectData,Executor executor) {
        this.taskData = taskData;
        this.projectData = projectData;
        this.executor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(taskData, projectData, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
