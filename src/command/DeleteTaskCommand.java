// File: command/DeleteTaskCommand.java
package command;

import model.*;

/**
 * Command to delete a task.
 * Stores deleted task for potential undo.
 */
public class DeleteTaskCommand implements Command {
    private final String taskId;
    private Task deletedTask; // Store for undo

    public DeleteTaskCommand(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean execute() {
        try {
            TaskManager manager = TaskManager.getInstance();

            // Delete and store result
            deletedTask = manager.deleteTask(taskId);

            return deletedTask != null;
        } catch (Exception e) {
            System.err.println("Failed to delete task: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getDescription() {
        return String.format("Delete task: %s", taskId);
    }

    @Override
    public int getPriority() {
        // Delete commands have high priority
        return 8;
    }
}
