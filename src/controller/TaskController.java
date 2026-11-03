package controller;

import model.*;
import queue.*;
import strategy.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskController is the MVC controller that coordinates between View and Model.
 * It handles all business logic and user actions.
 *
 * Design principles:
 * - Single Responsibility: Only handles coordination
 * - Dependency Injection: Strategies can be injected
 * - Facade Pattern: Simplifies complex operations for the view
 */
public class TaskController {
    // Model components
    private final TaskManager taskManager;
    private final CommandProcessor commandProcessor;

    // Default strategies
    private PriorityStrategy defaultPriorityStrategy;
    private ScheduleStrategy defaultScheduleStrategy;

    /**
     * Constructor initializes the controller
     */
    public TaskController() {
        this.taskManager = TaskManager.getInstance();
        this.commandProcessor = new CommandProcessor();

        // Set default strategies
        this.defaultPriorityStrategy = new HighPriorityFirstStrategy();
        this.defaultScheduleStrategy = new AutoRescheduleStrategy();
    }

    // Task CRUD operations - all async through command queue

    /**
     * Create a new task asynchronously
     */
    public void createTask(String title, String description,
                           LocalDateTime dueDate, Priority priority) {
        // Delegate to command processor
        commandProcessor.createTask(title, description, dueDate, priority);
    }

    /**
     * Update an existing task asynchronously
     */
    public void updateTask(Task task) {
        commandProcessor.updateTask(task);
    }

    /**
     * Delete a task asynchronously
     */
    public void deleteTask(String taskId) {
        commandProcessor.deleteTask(taskId);
    }

    /**
     * Schedule a task with conflict resolution
     */
    public void scheduleTask(Task task) {
        commandProcessor.scheduleTask(task, defaultScheduleStrategy);
    }

    /**
     * Schedule a task with specific strategy
     */
    public void scheduleTask(Task task, ScheduleStrategy strategy) {
        commandProcessor.scheduleTask(task, strategy);
    }

    // Synchronous read operations

    /**
     * Get all tasks
     */
    public List<Task> getAllTasks() {
        return taskManager.getAllTasks();
    }

    /**
     * Get tasks by status
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskManager.getTasksByStatus(status);
    }

    /**
     * Get tasks by priority
     */
    public List<Task> getTasksByPriority(Priority priority) {
        return taskManager.getTasksByPriority(priority);
    }

    /**
     * Get overdue tasks
     */
    public List<Task> getOverdueTasks() {
        return taskManager.getOverdueTasks();
    }

    /**
     * Find conflicting tasks
     */
    public List<Task> findConflictingTasks(Task task) {
        return taskManager.findConflictingTasks(task);
    }

    /**
     * Get queue statistics
     */
    public String getQueueStatistics() {
        return commandProcessor.getStatistics();
    }

    // Strategy setters

    /**
     * Set default priority strategy
     */
    public void setDefaultPriorityStrategy(PriorityStrategy strategy) {
        this.defaultPriorityStrategy = strategy;
    }

    /**
     * Set default schedule strategy
     */
    public void setDefaultScheduleStrategy(ScheduleStrategy strategy) {
        this.defaultScheduleStrategy = strategy;
    }

    // Utility methods

    /**
     * Check if any tasks conflict on a given date
     */
    public boolean hasConflictsOnDate(LocalDateTime date) {
        List<Task> tasksOnDate = taskManager.getTasksDueOn(date);
        return tasksOnDate.size() > 1;
    }

    /**
     * Get task count by status
     */
    public java.util.Map<TaskStatus, Long> getTaskStatistics() {
        return taskManager.getTaskCountByStatus();
    }

    /**
     * Shutdown the controller and its resources
     */
    public void shutdown() {
        commandProcessor.shutdown();
    }
}