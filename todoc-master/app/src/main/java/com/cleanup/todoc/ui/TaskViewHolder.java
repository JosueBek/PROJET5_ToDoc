package com.cleanup.todoc.ui;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.events.DeleteTaskListener;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * The circle icon showing the color of the project
     */
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_project)
    public AppCompatImageView imgProject;

    /**
     * The TextView displaying the name of the task
     */
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lbl_task_name)
    public TextView lblTaskName;

    /**
     * The TextView displaying the name of the project
     */
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lbl_project_name)
    public TextView lblProjectName;

    /**
     * The delete icon
     */
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_delete)
    public AppCompatImageView imgDelete;

    /**
     * The listener for when a task needs to be deleted
     */
    private DeleteTaskListener deleteTaskListener ;

    /**
     * Instantiates a new TaskViewHolder.
     *  @param itemView the view of the task item
     */
    TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public Project getCurrentProject (Task task, List<Project> projects) {
        for(Project project : projects) {
            if(project.getId() == task.getProjectId())
                return project;
        }
        return null;
    }

    /**
     * Binds a task and project to the item view.
     *  @param task the task to bind in the item view
     * @param deleteTaskListener the listener for when a task needs to be deleted to set
     * @param projects the project to bind the item view.
     */
    void bind(Task task, DeleteTaskListener deleteTaskListener, List<Project> projects) {
        this.deleteTaskListener = deleteTaskListener;
        lblTaskName.setText(task.getName());
        imgDelete.setOnClickListener(this);
        imgDelete.setTag(task);

        final Project taskProject = getCurrentProject(task, projects);
        if (taskProject != null) {
            imgProject.setSupportImageTintList(ColorStateList.valueOf(taskProject.getColor()));
            lblProjectName.setText(taskProject.getName());
        } else {
            imgProject.setVisibility(View.INVISIBLE);
            lblProjectName.setText("");
        }
    }

    /**
     * override method in interface deleteTaskListener, task needs to be deleted.
     */
    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Task) {
            deleteTaskListener.onDeleteTask((Task) tag);
        }
    }
}
