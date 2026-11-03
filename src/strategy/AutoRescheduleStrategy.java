// File: strategy/AutoRescheduleStrategy.java
package strategy;

import model.Task;
import model.TaskManager;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Automatically reschedules tasks to next available slot.
 * Concrete implementation of ScheduleStrategy.
 */
public class AutoRescheduleStrategy implements ScheduleStrategy {
    // Hours to shift when conflict detected
    private static final int RESCHEDULE_HOURS = 1;
    private static final int MAX_ATTEMPTS = 48; // Max 2 days ahead

    @Override
    public Task resolveConflict(Task task, List<Task> conflictingTasks) {
        // Find next available time slot
        LocalDateTime newTime = findNextAvailableSlot(task, conflictingTasks);

        // Move the same Task object (same id), so an existing task is updated, not duplicated
        task.setDueDate(newTime);
        return task;
    }

    /**
     * Find next available time slot by checking hourly intervals
     */
    private LocalDateTime findNextAvailableSlot(Task task, List<Task> conflictingTasks) {
        LocalDateTime proposedTime = task.getDueDate();
        Set<LocalDateTime> conflictTimes = new HashSet<>();

        // Collect all conflict times
        for (Task conflict : conflictingTasks) {
            conflictTimes.add(conflict.getDueDate());
        }

        // Try to find a free slot
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            proposedTime = proposedTime.plusHours(RESCHEDULE_HOURS);

            // Check against all tasks in the system
            boolean hasConflict = false;
            for (Task existingTask : TaskManager.getInstance().getAllTasks()) {
                if (!existingTask.getId().equals(task.getId()) &&
                        existingTask.getDueDate().toLocalDate().equals(proposedTime.toLocalDate()) &&
                        existingTask.getDueDate().getHour() == proposedTime.getHour() &&
                        existingTask.getStatus() != model.TaskStatus.COMPLETED) {
                    hasConflict = true;
                    break;
                }
            }

            if (!hasConflict) {
                return proposedTime;
            }

            attempts++;
        }

        // If no slot found, return far future date
        return proposedTime.plusDays(7);
    }

    @Override
    public String getDescription() {
        return "Automatically reschedule to next available hour";
    }
}
