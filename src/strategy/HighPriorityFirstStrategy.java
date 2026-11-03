// File: strategy/HighPriorityFirstStrategy.java
package strategy;

import model.Task;
import java.util.List;
import java.util.Comparator;

/**
 * Sorts tasks by priority - highest priority first.
 * Concrete implementation of PriorityStrategy.
 */
public class HighPriorityFirstStrategy implements PriorityStrategy {

    @Override
    public void sortTasks(List<Task> tasks) {
        // Sort using Comparator - no if-else needed
        // Compare by weight: enum order would put LOW first after reversed()
        tasks.sort(Comparator.comparingInt((Task t) -> t.getPriority().getWeight())
                .reversed() // High priority first
                .thenComparing(Task::getDueDate)); // Then by due date
    }

    @Override
    public String getStrategyName() {
        return "Priority (High to Low)";
    }

    // Shown in the "Sort by" box (was printing the class name and hash)
    @Override
    public String toString() {
        return getStrategyName();
    }
}
