package com.cleanup.todoc.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.base.BaseActivity;
import com.cleanup.todoc.events.DeleteTaskListener;
import com.cleanup.todoc.injection.Injection;
import com.cleanup.todoc.injection.ViewModelFactory;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.viewModel.TaskViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.System.currentTimeMillis;

public class MainActivity extends BaseActivity implements DeleteTaskListener {

    /**
     * The RecyclerView which displays the list of mTasks
     */
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.list_tasks)
    public RecyclerView listTasks;

    /**
     * The TextView displaying the empty state
     */
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lbl_no_task)
    public TextView lblNoTasks;

    /**
     * The adapter which handles the list of mTasks
     */
    private TasksAdapter adapter;

    /**
     * The sort method to be used to display mTasks
     */
    @NonNull
    private SortMethod sortMethod = SortMethod.NONE;

    /**
     * Dialog to create a new task
     */
    @Nullable
    public AlertDialog dialog = null;

    /**
     * EditText that allows user to set the name of a task
     */
    @Nullable
    private EditText dialogEditText = null;

    /**
     * Spinner that allows the user to associate a project to a task
     */
    @Nullable
    private Spinner dialogSpinner = null;

    /**
     * viewModel that is used in the repository
     */
    private TaskViewModel viewModel;

    /**
     * List of all projects available in the application
     */
    private List<Project> allProjects ;
    /**
     * List of all current mTasks of the application
     */
    @NonNull
    private List<Task> tasks = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            sortMethod = SortMethod.values()[
                    savedInstanceState.getInt("sortMethod", 4)];
        }
        ButterKnife.bind(this);
        configureRecyclerview();
        configureViewModel();
        listTasksInData();
        currentProjectInData();
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("sortMethod", sortMethod.ordinal());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter_alphabetical) {
            sortMethod = SortMethod.ALPHABETICAL;
        } else if (id == R.id.filter_alphabetical_inverted) {
            sortMethod = SortMethod.ALPHABETICAL_INVERTED;
        } else if (id == R.id.filter_oldest_first) {
            sortMethod = SortMethod.OLD_FIRST;
        } else if (id == R.id.filter_recent_first) {
            sortMethod = SortMethod.RECENT_FIRST;
        }
        updateTask();
        return super.onOptionsItemSelected(item);
    }

    public void updateTask() {
        if (tasks.size() == 0) {
            lblNoTasks.setVisibility(View.VISIBLE);
            listTasks.setVisibility(View.GONE);
        } else {
            lblNoTasks.setVisibility(View.GONE);
            listTasks.setVisibility(View.VISIBLE);
            switch (sortMethod) {
                case ALPHABETICAL:
                    Collections.sort(tasks, new Task.TaskAZComparator());
                    break;
                case ALPHABETICAL_INVERTED:
                    Collections.sort(tasks, new Task.TaskZAComparator());
                    break;
                case RECENT_FIRST:
                    Collections.sort(tasks, new Task.TaskRecentComparator());
                    break;
                case OLD_FIRST:
                    Collections.sort(tasks, new Task.TaskOldComparator());
                    break;
            }
            adapter.updateTasks(tasks);
        }
    }

    public void configureRecyclerview() {
        adapter = new TasksAdapter(tasks,this);
        listTasks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listTasks.setAdapter(adapter);
    }

    public void configureViewModel() {
        ViewModelFactory modelFactory = Injection.provideViewModelFactory(getApplicationContext());
        viewModel = new ViewModelProvider(this, modelFactory).get(TaskViewModel.class);
    }

    public void  getAllProject(List<Project> projects) {
        allProjects = projects;
        adapter.updateProjects(projects);
    }

    public void currentProjectInData() {
        viewModel.getCurrentAllProject().observe(this, this::getAllProject);
    }

    public void listTasksInData() {
        viewModel.getTask().observe(this, this::updateListTaskInData);
    }

    public void updateListTaskInData(List<Task> tasksList) {
        this.tasks = tasksList;
        updateTask();
    }

    public void addTaskData(Task task) {
        viewModel.createTask(task);
        this.viewModel.updateTask(task);
    }

    @Override
    public void onDeleteTask(Task task) {
        this.viewModel.deleteTask(task.getId());
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.fab_add_task)
    void addTask() {
        showAddTaskDialog();
    }

    /**
     * Called when the user clicks on the positive button of the Create Task Dialog.
     *
     * @param dialogInterface the current displayed dialog
     */
    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        if (dialogEditText != null && dialogSpinner != null) {
            String taskName = dialogEditText.getText().toString();
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }
            if (taskName.trim().isEmpty()) {
                dialogEditText.setError(getString(R.string.empty_task_name));
            } else if (taskProject != null) {
                long id = currentTimeMillis();
                Task task = new Task(
                        id,
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()
                );
                addTaskData(task);
                dialogInterface.dismiss();
            } else {
                dialogInterface.dismiss();
            }
        } else {
            dialogInterface.dismiss();
        }
    }

    /**
     * Shows the Dialog for adding a Task
     */
    private void showAddTaskDialog() {
        final AlertDialog dialog = getAddTaskDialog();
        dialog.show();
        dialogEditText = dialog.findViewById(R.id.txt_task_name);
        dialogSpinner = dialog.findViewById(R.id.project_spinner);
        populateDialogSpinner();
    }

    /**
     * Returns the dialog allowing the user to create a new task.
     *
     * @return the dialog allowing the user to create a new task
     */
    @NonNull
    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);
        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(R.layout.dialog_add_task);
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(dialogInterface -> {
            dialogEditText = null;
            dialogSpinner = null;
            dialog = null;
        });
        dialog = alertBuilder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> onPositiveButtonClick(dialog));
        });
        return dialog;
    }

    /**
     * Sets the data of the Spinner with projects to associate to a new task
     */
    private void populateDialogSpinner() {
        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) {
            dialogSpinner.setAdapter(adapter);
        }
    }

    /**
     * List of all possible sort methods for task
     */
    private enum SortMethod {
        /**
         * Sort alphabetical by name
         */
        ALPHABETICAL,
        /**
         * Inverted sort alphabetical by name
         */
        ALPHABETICAL_INVERTED,
        /**
         * Lastly created first
         */
        RECENT_FIRST,
        /**
         * First created first
         */
        OLD_FIRST,
        /**
         * No sort
         */
        NONE
    }
}