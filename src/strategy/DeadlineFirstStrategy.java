// File: strategy/DeadlineFirstStrategy.java
package strategy;

import model.Task;
import java.util.List;
import java.util.Comparator;

/**
 * Sorts tasks by due date - earliest deadline first.
 * Another concrete implementation showing strategy variety.
 */
public class DeadlineFirstStrategy implements PriorityStrategy {

    @Override
    public void sortTasks(List<Task> tasks) {
        // Sort by due date, then by priority for same dates
        tasks.sort(Comparator.comparing(Task::getDueDate)
                .thenComparing(t -> t.getPriority().getWeight(), Comparator.reverseOrder()));
    }

    @Override
    public String getStrategyName() {
        return "Deadline (Earliest First)";
    }

    // Shown in the "Sort by" box (was printing the class name and hash)
    @Override
    public String toString() {
        return getStrategyName();
    }
}
