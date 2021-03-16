package com.cleanup.todoc;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.repository.ProjectDataRepository;
import com.cleanup.todoc.repository.TaskDataRepository;
import com.cleanup.todoc.viewModel.TaskViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for tasks
 *
 * @author Gaëtan HERFRAY
 */
public class TaskUnitTest {

    @Mock
    private ProjectDataRepository projectDataRepository;
    @Mock
    private TaskDataRepository taskDataRepository;
    @Mock
    private TaskViewModel viewModel;
    private Executor executor;
    private LiveData<List<Task>> tasks;
    private  LiveData<List<Project>> projects;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(taskDataRepository.getTasks()).thenReturn(tasks);
        when(projectDataRepository.getAllProject()).thenReturn(projects);
        this.viewModel = new TaskViewModel(taskDataRepository, projectDataRepository, executor);
    }

    @Test
    public void test_projects() {
        // TASK
        final Task task1 = new Task(1, 1, "task 1", new Date().getTime());
        final Task task2 = new Task(2, 2, "task 2", new Date().getTime());
        final Task task3 = new Task(3, 3, "task 3", new Date().getTime());
        final Task task4 = new Task(4, 4, "task 4", new Date().getTime());

        assertEquals("Projet Tartampion", Objects.requireNonNull(task1.getProject()).getName());
        assertEquals("Projet Lucidia", Objects.requireNonNull(task2.getProject()).getName());
        assertEquals("Projet Circus", Objects.requireNonNull(task3.getProject()).getName());
        assertNull(task4.getProject());

        // Test ProjectRepository create and insert all Projects;
        LiveData<List<Project>> projectDB = this.projectDataRepository.getAllProject();
        assertEquals(projectDB, this.viewModel.getCurrentAllProject());
    }

    @Test
    public void test_az_comparator()  {
        final Task task1 = new Task(1, 1, "aaa", 123);
        final Task task2 = new Task(2, 2L, "zzz", 124);
        final Task task3 = new Task(3, 3L, "hhh", 125);

        final ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        Collections.sort(tasks, new Task.TaskAZComparator());

        assertSame(tasks.get(0), task1);
        assertSame(tasks.get(1), task3);
        assertSame(tasks.get(2), task2);

        // Test TaskRepository and view model create Task.
        this.taskDataRepository.createTask(task1);
        this.taskDataRepository.createTask(task2);
        this.taskDataRepository.createTask(task3);
        verify(taskDataRepository, times(1)).createTask(task1);
        verify(taskDataRepository, times(1)).createTask(task2);
        verify(taskDataRepository, times(1)).createTask(task3);
        LiveData<List<Task>> taskDB = this.taskDataRepository.getTasks();
        assertSame(taskDB, this.viewModel.getTask());
    }

    @Test
    public void test_za_comparator() {
        final Task task1 = new Task(1, 1, "aaa", 123);
        final Task task2 = new Task(2, 2, "zzz", 124);
        final Task task3 = new Task(3, 3, "hhh", 125);

        final ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        Collections.sort(tasks, new Task.TaskZAComparator());

        assertSame(tasks.get(0), task2);
        assertSame(tasks.get(1), task3);
        assertSame(tasks.get(2), task1);

        // Test TaskRepository create and delete task.
        this.taskDataRepository.createTask(task1);
        this.taskDataRepository.createTask(task2);
        this.taskDataRepository.createTask(task3);
        this.taskDataRepository.deleteTask(task1.getId());
        this.taskDataRepository.deleteTask(task2.getId());
        this.taskDataRepository.deleteTask(task3.getId());
        verify(taskDataRepository, times(1)).deleteTask(task1.getId());
        verify(taskDataRepository, times(1)).deleteTask(task2.getId());
        verify(taskDataRepository, times(1)).deleteTask(task3.getId());
        LiveData<List<Task>> taskDB = this.taskDataRepository.getTasks();
        assertSame(taskDB, this.viewModel.getTask());
    }

    @Test
    public void test_recent_comparator() {
        final Task task1 = new Task(1, 1, "aaa", 123);
        final Task task2 = new Task(2, 2, "zzz", 124);
        final Task task3 = new Task(3, 3, "hhh", 125);

        final ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        Collections.sort(tasks, new Task.TaskRecentComparator());

        assertSame(tasks.get(0), task3);
        assertSame(tasks.get(1), task2);
        assertSame(tasks.get(2), task1);
    }

    @Test
    public void test_old_comparator() {
        final Task task1 = new Task(1, 1, "aaa", 123);
        final Task task2 = new Task(2, 2, "zzz", 124);
        final Task task3 = new Task(3, 3, "hhh", 125);

        final ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        Collections.sort(tasks, new Task.TaskOldComparator());

        assertSame(tasks.get(0), task1);
        assertSame(tasks.get(1), task2);
        assertSame(tasks.get(2), task3);
    }
}