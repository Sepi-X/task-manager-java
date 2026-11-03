// File: strategy/ScheduleStrategy.java
package strategy;

import model.Task;
import java.util.List;

/**
 * ScheduleStrategy interface for handling scheduling conflicts.
 * Different implementations can resolve conflicts differently.
 */
public interface ScheduleStrategy {
    /**
     * Resolve conflicts for a task
     * @param task The task to schedule
     * @param conflictingTasks List of tasks that conflict
     * @return Modified task with new schedule, or null if cannot resolve
     */
    Task resolveConflict(Task task, List<Task> conflictingTasks);

    /**
     * Get strategy description
     */
    String getDescription();
}