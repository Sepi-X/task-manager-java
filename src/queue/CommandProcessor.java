// File: queue/CommandProcessor.java
package queue;

import command.*;
import model.*;
import strategy.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CommandProcessor provides high-level interface for command processing.
 * Facade pattern - simplifies interaction with queue system.
 */
public class CommandProcessor {
    private final TaskQueue queue;
    private final ScheduleStrategy defaultScheduleStrategy;

    public CommandProcessor() {
        this.queue = TaskQueue.getInstance();
        this.defaultScheduleStrategy = new AutoRescheduleStrategy();
    }

    /**
     * Create a new task asynchronously
     */
    public void createTask(String title, String description,
                           LocalDateTime dueDate, Priority priority) {
        Command command = new CreateTaskCommand(title, description, dueDate, priority);
        queue.submitCommand(command);
    }

    /**
     * Update an existing task asynchronously
     */
    public void updateTask(Task task) {
        Command command = new UpdateTaskCommand(task.getId(), task);
        queue.submitCommand(command);
    }

    /**
     * Delete a task asynchronously
     */
    public void deleteTask(String taskId) {
        Command command = new DeleteTaskCommand(taskId);
        queue.submitCommand(command);
    }

    /**
     * Schedule a task with conflict resolution
     */
    public void scheduleTask(Task task) {
        scheduleTask(task, defaultScheduleStrategy);
    }

    /**
     * Schedule a task with specific strategy
     */
    public void scheduleTask(Task task, ScheduleStrategy strategy) {
        Command command = new ScheduleTaskCommand(task, strategy);
        queue.submitCommand(command);
    }

    /**
     * Process multiple commands as a batch
     */
    public void processBatch(String description, List<Command> commands) {
        BatchCommand batch = new BatchCommand(description);
        for (Command cmd : commands) {
            batch.addCommand(cmd);
        }
        queue.submitCommand(batch);
    }

    /**
     * Get queue statistics
     */
    public String getStatistics() {
        return queue.getStatistics();
    }

    /**
     * Shutdown the processor
     */
    public void shutdown() {
        queue.shutdown();
    }
}