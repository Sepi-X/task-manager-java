// File: strategy/PriorityBasedRescheduleStrategy.java
package strategy;

import model.Task;
import model.Priority;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Reschedules based on priority - lower priority tasks get moved.
 * Shows alternative conflict resolution approach.
 */
public class PriorityBasedRescheduleStrategy implements ScheduleStrategy {

    @Override
    public Task resolveConflict(Task task, List<Task> conflictingTasks) {
        // Check if this task has higher priority than conflicts
        Priority taskPriority = task.getPriority();

        // Find the lowest priority among conflicts
        Priority lowestConflictPriority = conflictingTasks.stream()
                .map(Task::getPriority)
                .min(Comparator.comparingInt(Priority::getWeight))
                .orElse(Priority.LOW);

        // If new task has higher priority, keep its time
        if (taskPriority.isHigherThan(lowestConflictPriority)) {
            return task; // Keep original time
        }

        // Otherwise, move to next day same time.
        // Keep the same Task object (same id) so an existing task is updated, not duplicated.
        task.setDueDate(task.getDueDate().plusDays(1));
        return task;
    }

    @Override
    public String getDescription() {
        return "Reschedule based on priority (lower priority moves)";
    }
}