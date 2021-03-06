package com.cleanup.todoc.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cleanup.todoc.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insertTask(Task task);

    @Query("SELECT * FROM task_table WHERE projectId")
    LiveData<List<Task>> getTask();

    @Update
    void updateTask(Task task);

    @Query("DELETE FROM task_table WHERE id = :taskId")
    void deleteTask(long taskId);

}
