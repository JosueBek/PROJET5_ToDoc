package com.cleanup.todoc.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.events.DeleteTaskListener;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import java.util.List;

/**
 * <p>Adapter which handles the list of tasks to display in the dedicated RecyclerView.</p>
 */
public class TasksAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private List<Project> projects;
    private List<Task> tasks;
    private final DeleteTaskListener deleteTaskListener;

    /**
     * Instantiates a new TasksAdapter.
     */
    TasksAdapter(List<Task> items, DeleteTaskListener deleteTaskListener) {
        this.tasks = items ;
        this.deleteTaskListener = deleteTaskListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        taskViewHolder.bind(tasks.get(position), deleteTaskListener, projects);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * Updates the list of tasks the adapter deals with.
     *
     * @param tasks the list of tasks the adapter deals with to set
     */
    void updateTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    void updateProjects(@NonNull final List<Project> projects) {
        this.projects = projects;
        notifyDataSetChanged();
    }
}
