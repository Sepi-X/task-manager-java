// File: observer/TaskEventType.java
package observer;

/**
 * TaskEventType enum defines all possible task events.
 * Using enum instead of strings for type safety.
 */
public enum TaskEventType {
    TASK_ADDED("Task Added"),
    TASK_UPDATED("Task Updated"),
    TASK_DELETED("Task Deleted"),
    TASK_COMPLETED("Task Completed"),
    TASK_SCHEDULED("Task Scheduled"),
    TASKS_CLEARED("All Tasks Cleared");

    private final String description;

    TaskEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}