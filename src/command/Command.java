// File: command/Command.java
package command;

/**
 * Command interface for Command pattern.
 * Encapsulates operations as objects for queuing and execution.
 *
 * This enables:
 * - Queuing operations
 * - Logging operations
 * - Undo functionality (if needed)
 * - Distributed processing
 */
public interface Command {
    /**
     * Execute the command
     * @return true if successful, false otherwise
     */
    boolean execute();

    /**
     * Get command description for logging
     */
    String getDescription();

    /**
     * Get command priority for queue ordering
     */
    int getPriority();
}
