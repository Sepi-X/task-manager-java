// File: command/BatchCommand.java
package command;

import java.util.List;
import java.util.ArrayList;

/**
 * Composite command that executes multiple commands.
 * Demonstrates Composite pattern with Command pattern.
 */
public class BatchCommand implements Command {
    private final List<Command> commands;
    private final String description;

    public BatchCommand(String description) {
        this.commands = new ArrayList<>();
        this.description = description;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public boolean execute() {
        boolean allSuccessful = true;

        for (Command command : commands) {
            boolean result = command.execute();
            if (!result) {
                allSuccessful = false;
                System.err.println("Failed to execute: " + command.getDescription());
            }
        }

        return allSuccessful;
    }

    @Override
    public String getDescription() {
        return String.format("Batch: %s (%d commands)", description, commands.size());
    }

    @Override
    public int getPriority() {
        // Batch commands have lower priority
        return 3;
    }
}