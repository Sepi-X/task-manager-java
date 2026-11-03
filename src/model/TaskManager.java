package model;

import observer.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * TaskManager is a Singleton that manages all tasks in the system.
 * It implements the Subject interface for Observer pattern.
 *
 * Design patterns used:
 * 1. Singleton - Only one instance manages all tasks
 * 2. Observer - Notifies listeners when tasks change
 *
 * Thread-safe implementation using concurrent collections.
 */
public class TaskManager implements TaskSubject {
    // Singleton instance - volatile for thread safety
    private static volatile TaskManager instance;

    // Thread-safe collections
    private final Map<String, Task> tasks;
    private final List<TaskObserver> observers;

    /**
     * Private constructor prevents external instantiation.
     * Uses concurrent collections for thread safety in distributed environment.
     */
    private TaskManager() {
        // ConcurrentHashMap for thread-safe operations
        this.tasks = new ConcurrentHashMap<>();
        // CopyOnWriteArrayList for safe iteration during notifications
        this.observers = new CopyOnWriteArrayList<>();
    }

    /**
     * Get singleton instance - thread-safe double-checked locking.
     * This ensures only one instance exists even in multi-threaded environment.
     */
    public static TaskManager getInstance() {
        // First check without locking for performance
        if (instance == null) {
            // Synchronize only when creating instance
            synchronized (TaskManager.class) {
                // Double-check inside synchronized block
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    /**
     * Add a new task to the manager.
     * Notifies all observers about the addition.
     */
    public void addTask(Task task) {
        // Validate task
        validateTask(task);

        // Add to collection
        tasks.put(task.getId(), task);

        // Notify observers
        notifyObservers(TaskEventType.TASK_ADDED, task);
    }

    /**
     * Update an existing task.
     * Validates the task exists before updating.
     */
    public void updateTask(Task task) {
        // Validate task
        validateTask(task);

        // Check if task exists
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task not found: " + task.getId());
        }

        // Update task
        tasks.put(task.getId(), task);

        // Notify observers
        notifyObservers(TaskEventType.TASK_UPDATED, task);
    }

    /**
     * Delete a task by ID.
     * Returns the deleted task or null if not found.
     */
    public Task deleteTask(String taskId) {
        Task removedTask = tasks.remove(taskId);

        if (removedTask != null) {
            notifyObservers(TaskEventType.TASK_DELETED, removedTask);
        }

        return removedTask;
    }

    /**
     * Get a task by ID.
     * Returns null if not found (avoids exceptions for normal flow).
     */
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * Get all tasks as an unmodifiable list.
     * This prevents external modification of the internal collection.
     */
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(
                new ArrayList<>(tasks.values())
        );
    }

    /**
     * Get tasks filtered by status.
     * Uses Stream API to avoid explicit loops.
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks filtered by priority.
     * Demonstrates functional programming approach.
     */
    public List<Task> getTasksByPriority(Priority priority) {
        return tasks.values().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    /**
     * Get overdue tasks.
     * Shows how to filter based on business logic.
     */
    public List<Task> getOverdueTasks() {
        return tasks.values().stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Find tasks that conflict with the given task.
     * Used by the scheduler to detect conflicts.
     */
    public List<Task> findConflictingTasks(Task task) {
        return tasks.values().stream()
                .filter(t -> t.conflictsWith(task))
                .collect(Collectors.toList());
    }

    /**
     * Get tasks due on a specific date.
     * Useful for daily planning views.
     */
    public List<Task> getTasksDueOn(LocalDateTime date) {
        return tasks.values().stream()
                .filter(task -> task.getDueDate().toLocalDate().equals(date.toLocalDate()))
                .collect(Collectors.toList());
    }

    /**
     * Clear all tasks - useful for testing.
     * Notifies observers about the clear operation.
     */
    public void clearAllTasks() {
        tasks.clear();
        notifyObservers(TaskEventType.TASKS_CLEARED, null);
    }

    /**
     * Get count of tasks by status.
     * Returns a map of status to count for dashboard displays.
     */
    public Map<TaskStatus, Long> getTaskCountByStatus() {
        return tasks.values().stream()
                .collect(Collectors.groupingBy(
                        Task::getStatus,
                        Collectors.counting()
                ));
    }

    // Observer pattern implementation

    @Override
    public void attach(TaskObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(TaskObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(TaskEventType eventType, Task task) {
        // Create event object
        TaskEvent event = new TaskEvent(eventType, task);

        // Notify all observers
        for (TaskObserver observer : observers) {
            observer.update(event);
        }
    }

    /**
     * Validate task before adding/updating.
     * Centralizes validation logic.
     */
    private void validateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (task.getDueDate() == null) {
            throw new IllegalArgumentException("Task due date cannot be null");
        }
        if (task.getPriority() == null) {
            throw new IllegalArgumentException("Task priority cannot be null");
        }
    }

    /**
     * Get the total number of tasks.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Check if a task exists by ID.
     */
    public boolean taskExists(String taskId) {
        return tasks.containsKey(taskId);
    }
}