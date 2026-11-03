// File: command/ScheduleTaskCommand.java
package command;

import model.*;
import strategy.*;
import java.util.List;

/**
 * Command to schedule/reschedule a task using strategy pattern.
 * Demonstrates command and strategy patterns working together.
 */
public class ScheduleTaskCommand implements Command {
    private final Task task;
    private final ScheduleStrategy strategy;
    private Task scheduledTask;

    public ScheduleTaskCommand(Task task, ScheduleStrategy strategy) {
        this.task = task;
        this.strategy = strategy;
    }

    @Override
    public boolean execute() {
        try {
            TaskManager manager = TaskManager.getInstance();

            // Find conflicts
            List<Task> conflicts = manager.findConflictingTasks(task);

            if (conflicts.isEmpty()) {
                // No conflicts, just add/update task
                if (manager.taskExists(task.getId())) {
                    manager.updateTask(task);
                } else {
                    manager.addTask(task);
                }
                scheduledTask = task;
            } else {
                // Use strategy to resolve conflicts
                scheduledTask = strategy.resolveConflict(task, conflicts);

                if (scheduledTask != null) {
                    if (manager.taskExists(task.getId())) {
                        manager.updateTask(scheduledTask);
                    } else {
                        manager.addTask(scheduledTask);
                    }
                }
            }

            return scheduledTask != null;
        } catch (Exception e) {
            System.err.println("Failed to schedule task: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getDescription() {
        return String.format("Schedule task: %s using %s",
                task.getTitle(), strategy.getDescription());
    }

    @Override
    public int getPriority() {
        // Scheduling has highest priority
        return 10;
    }

    public Task getScheduledTask() {
        return scheduledTask;
    }
}
