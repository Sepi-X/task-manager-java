// File: observer/TaskSubject.java
package observer;

import model.Task;

/**
 * TaskSubject interface for Observer pattern.
 * Implemented by classes that need to notify observers about changes.
 *
 * Following Dependency Inversion Principle (DIP) -
 * High-level modules depend on abstractions, not concrete classes.
 */
public interface TaskSubject {
    /**
     * Attach an observer to receive notifications
     */
    void attach(TaskObserver observer);

    /**
     * Detach an observer to stop receiving notifications
     */
    void detach(TaskObserver observer);

    /**
     * Notify all observers about an event
     */
    void notifyObservers(TaskEventType eventType, Task task);
}
