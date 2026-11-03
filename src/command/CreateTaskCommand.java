// File: command/CreateTaskCommand.java
package command;

import model.*;
import java.time.LocalDateTime;

/**
 * Command to create a new task.
 * Encapsulates all data needed to create a task.
 */
public class CreateTaskCommand implements Command {
    private final String title;
    private final String description;
    private final LocalDateTime dueDate;
    private final Priority priority;
    private Task createdTask; // Store created task for reference

    public CreateTaskCommand(String title, String description,
                             LocalDateTime dueDate, Priority priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    @Override
    public boolean execute() {
        try {
            // Create new task
            createdTask = new Task(title, description, dueDate, priority);

            // Add to task manager
            TaskManager.getInstance().addTask(createdTask);

            return true;
        } catch (Exception e) {
            System.err.println("Failed to create task: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getDescription() {
        return String.format("Create task: %s (Priority: %s, Due: %s)",
                title, priority, dueDate);
    }

    @Override
    public int getPriority() {
        // Create commands have medium priority
        return 5;
    }

    public Task getCreatedTask() {
        return createdTask;
    }
}
