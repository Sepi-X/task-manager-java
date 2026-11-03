// File: queue/TaskWorker.java
package queue;

import command.Command;
import java.util.concurrent.TimeUnit;

/**
 * TaskWorker represents a distributed worker processing commands.
 * Implements Runnable for thread execution.
 *
 * In a real distributed system, these would be separate processes/machines.
 */
public class TaskWorker implements Runnable {
    private final String workerName;
    private final TaskQueue queue;
    private volatile boolean running = true;

    public TaskWorker(String workerName, TaskQueue queue) {
        this.workerName = workerName;
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println(workerName + " started");

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Wait for command with timeout
                Command command = queue.pollCommand(1, TimeUnit.SECONDS);

                if (command != null) {
                    processCommand(command);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println(workerName + " error: " + e.getMessage());
            }
        }

        System.out.println(workerName + " stopped");
    }

    /**
     * Process a single command
     */
    private void processCommand(Command command) {
        System.out.println(workerName + " processing: " + command.getDescription());

        long startTime = System.currentTimeMillis();

        try {
            // Execute command
            boolean success = command.execute();

            long duration = System.currentTimeMillis() - startTime;

            if (success) {
                queue.recordSuccess();
                System.out.println(workerName + " completed: " +
                        command.getDescription() + " in " + duration + "ms");
            } else {
                queue.recordFailure();
                System.err.println(workerName + " failed: " + command.getDescription());
            }
        } catch (Exception e) {
            queue.recordFailure();
            System.err.println(workerName + " exception: " + e.getMessage());
        }
    }

    /**
     * Stop the worker
     */
    public void stop() {
        running = false;
    }
}