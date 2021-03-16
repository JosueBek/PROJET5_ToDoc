package com.cleanup.todoc.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.repository.ProjectDataRepository;
import com.cleanup.todoc.repository.TaskDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class TaskViewModel extends ViewModel {

    // REPOSITORIES
    private final TaskDataRepository taskData;
    private final ProjectDataRepository projectData;
    private final Executor executor;

    public TaskViewModel(TaskDataRepository taskData, ProjectDataRepository projectData, Executor executor) {
        this.taskData = taskData;
        this.projectData = projectData;
        this.executor = executor;
    }

    // -------------
    // FOR TASK
    // -------------
    public LiveData<List<Task>> getTask() {
        return taskData.getTasks();
    }

    // -------------
    // FOR PROJECT
    // -------------
    public LiveData<List<Project>> getCurrentAllProject() {
        return projectData.getAllProject();
    }

    public void createTask(Task task) {
        executor.execute(() -> taskData.createTask(task));
    }

    public void deleteTask(long taskId) {
        executor.execute(() -> taskData.deleteTask(taskId));
    }

    public void updateTask(Task task) {
        executor.execute(() -> taskData.updateTask(task));
    }
}
