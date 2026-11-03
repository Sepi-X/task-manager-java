// File: strategy/PriorityStrategy.java
package strategy;

import model.Task;
import java.util.List;

/**
 * PriorityStrategy interface defines how to sort tasks.
 * This eliminates if-else chains for different sorting algorithms.
 *
 * Open/Closed Principle: Can add new strategies without modifying existing code.
 */
public interface PriorityStrategy {
    /**
     * Sort tasks according to the strategy
     * @param tasks List of tasks to sort (will be modified)
     */
    void sortTasks(List<Task> tasks);

    /**
     * Get strategy name for UI display
     */
    String getStrategyName();
}
