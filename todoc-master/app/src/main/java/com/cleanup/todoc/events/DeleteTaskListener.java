package com.cleanup.todoc.events;

import com.cleanup.todoc.model.Task;

/**
 * Listener for deleting tasks
 */
public interface DeleteTaskListener {

    /**
     * Called when a task needs to be deleted.
     */
    void onDeleteTask(Task task);
}
