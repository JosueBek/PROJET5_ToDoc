package com.cleanup.todoc;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cleanup.todoc.database.SaveDatabase;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
public class DataBaseInstrumentedTest {

    // FOR DATA
    private SaveDatabase database;

    // DATA SET FOR TEST
    private static final Project project = new Project(4L, "Project my project", 0xFFEADAD1);
    private static final Task task = new Task(1, 4L, "aaa", new Date().getTime());

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void InitDatabase() {
        // BEFORE: Init Database
        this.database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                SaveDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDatabase() {
        // AFTER: close database
        database.close();
    }

    @Test
    public void clearDatabase() throws InterruptedException {
        List<Project> projects = LiveDataTestUtil.getValue(this.database.projectDao().getProject());
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTask());
        assertTrue(projects.isEmpty());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void createAndGetProjectInDatabase() throws InterruptedException {
        // Create a new Project in the Database
        this.database.projectDao().createProject(project);
        // TEST this project is created in Database
        List<Project> projects = LiveDataTestUtil.getValue(this.database.projectDao().getProject());
        assertEquals(1, projects.size());
        assertTrue(projects.get(0).getId() == project.getId() && projects.get(0).getName().equals("Project my project"));
    }

    @Test
    public void createAndUpdateTaskInDatabase() throws InterruptedException {
        // Create new Task, update List Task in Database.
        this.database.projectDao().createProject(project);
        this.database.taskDao().insertTask(task);
        Task taskAdded = LiveDataTestUtil.getValue(this.database.taskDao().getTask()).get(0);
        this.database.taskDao().updateTask(taskAdded);
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTask());
        List<Project> projects = LiveDataTestUtil.getValue(this.database.projectDao().getProject());
        //Verify this task has been added to the database in relation to the project
        assertTrue(tasks.size() == 1 && projects.get(0).getName().equals("Project my project"));
        assertEquals(tasks.get(0).getProjectId(), projects.get(0).getId());
    }

    @Test
    public void CreateAndDeleteTaskInDatabase() throws InterruptedException {
        // Adding New Project & new Task in Database.
        this.database.projectDao().createProject(project);
        this.database.taskDao().insertTask(task);
        Task taskAdded = LiveDataTestUtil.getValue(this.database.taskDao().getTask()).get(0);
        this.database.taskDao().deleteTask(taskAdded.getId());
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTask());
        //Verify this task has been deleted to the database
        assertTrue(tasks.isEmpty());
    }
}