// File: command/UpdateTaskCommand.java
package command;

import model.*;

/**
 * Command to update an existing task.
 * Stores both old and new state for potential undo.
 */
public class UpdateTaskCommand implements Command {
    private final String taskId;
    private final Task updatedTask;
    private Task originalTask; // For undo functionality

    public UpdateTaskCommand(String taskId, Task updatedTask) {
        this.taskId = taskId;
        this.updatedTask = updatedTask;
    }

    @Override
    public boolean execute() {
        try {
            TaskManager manager = TaskManager.getInstance();

            // Store original for undo
            originalTask = manager.getTask(taskId);

            if (originalTask == null) {
                System.err.println("Task not found: " + taskId);
                return false;
            }

            // Update task
            manager.updateTask(updatedTask);

            return true;
        } catch (Exception e) {
            System.err.println("Failed to update task: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getDescription() {
        return String.format("Update task: %s", taskId);
    }

    @Override
    public int getPriority() {
        // Update commands have higher priority
        return 7;
    }
}
