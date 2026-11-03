package model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Task class represents a single task in the system.
 * This class follows the Single Responsibility Principle (SRP) -
 * it only holds task data and provides access to it.
 *
 * Key OOP concepts:
 * - Encapsulation: All fields are private with public getters/setters
 * - Immutable ID: Once created, task ID cannot be changed
 * - Value Objects: Using enums for type safety
 */
public class Task {
    // Unique identifier for each task - using UUID to ensure uniqueness
    private final String id;

    // Task properties - all private for encapsulation
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor creates a new task with default values.
     * Notice: No complex logic here - following SRP
     */
    public Task(String title, String description, LocalDateTime dueDate, Priority priority) {
        // Generate unique ID for each task
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = TaskStatus.PENDING; // Default status
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters with validation
    // Each setter updates the 'updatedAt' timestamp

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        // Validation: title cannot be null or empty
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        // Validation: due date cannot be null
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Check if task is overdue.
     * This is a business logic method that belongs in the Task class
     * because it only uses Task's own data.
     */
    public boolean isOverdue() {
        return status != TaskStatus.COMPLETED &&
                LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Check if this task conflicts with another task.
     * Two tasks conflict if they have the same due date and are both not completed.
     * This demonstrates object interaction without violating encapsulation.
     */
    public boolean conflictsWith(Task other) {
        // Tasks don't conflict with themselves
        if (this.id.equals(other.id)) {
            return false;
        }

        // Completed tasks don't conflict
        if (this.status == TaskStatus.COMPLETED || other.status == TaskStatus.COMPLETED) {
            return false;
        }

        // Check if due dates are on the same day and hour
        return this.dueDate.toLocalDate().equals(other.dueDate.toLocalDate()) &&
                this.dueDate.getHour() == other.dueDate.getHour();
    }

    @Override
    public String toString() {
        return String.format("Task[id=%s, title=%s, priority=%s, due=%s, status=%s]",
                id, title, priority, dueDate, status);
    }

    /**
     * Proper equals and hashCode implementation.
     * Two tasks are equal if they have the same ID.
     * This is important for collections and testing.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}