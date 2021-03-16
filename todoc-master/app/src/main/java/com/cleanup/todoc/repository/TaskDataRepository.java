package com.cleanup.todoc.repository;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.database.dao.TaskDao;
import com.cleanup.todoc.model.Task;

import java.util.List;

public class TaskDataRepository {
    private final TaskDao taskDao;

    public TaskDataRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    // --- GET TASK ---
    public LiveData<List<Task>> getTasks() {
        return taskDao.getTask();
    }

    // --- CREATE TASK ---
    public void createTask(Task task) {
        taskDao.insertTask(task);
    }

    // --- DELETE TASK ---
    public void deleteTask(long taskId) {
        taskDao.deleteTask(taskId);
    }

    // --- UPDATE TASK ---
    public void updateTask(Task task) {
        taskDao.updateTask(task);
    }
}
