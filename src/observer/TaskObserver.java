// File: observer/TaskObserver.java
package observer;

/**
 * TaskObserver interface for Observer pattern.
 * Classes implementing this interface can observe changes in TaskManager.
 *
 * This demonstrates Interface Segregation Principle (ISP) -
 * small, focused interface with single method.
 */
public interface TaskObserver {
    /**
     * Called when a task-related event occurs.
     * @param event The event containing type and affected task
     */
    void update(TaskEvent event);
}