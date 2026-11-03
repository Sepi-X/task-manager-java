// File: observer/TaskEvent.java
package observer;

import model.Task;
import java.time.LocalDateTime;

/**
 * TaskEvent represents an event in the task management system.
 * Immutable class that carries event information to observers.
 *
 * Demonstrates:
 * - Immutability: All fields are final
 * - Encapsulation: Private fields with getters
 * - Single Responsibility: Only carries event data
 */
public class TaskEvent {
    private final TaskEventType type;
    private final Task task;
    private final LocalDateTime timestamp;

    /**
     * Create a new task event
     */
    public TaskEvent(TaskEventType type, Task task) {
        this.type = type;
        this.task = task;
        this.timestamp = LocalDateTime.now();
    }

    public TaskEventType getType() {
        return type;
    }

    public Task getTask() {
        return task;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("TaskEvent[type=%s, task=%s, time=%s]",
                type, task != null ? task.getId() : "null", timestamp);
    }
}