// File: queue/TaskQueue.java
package queue;

import command.Command;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TaskQueue implements a thread-safe message queue for commands.
 * Simulates distributed message queue like RabbitMQ or JMS.
 *
 * Key features:
 * - Thread-safe blocking queue
 * - Priority ordering
 * - Producer-consumer pattern
 */
public class TaskQueue {
    // Singleton instance
    private static volatile TaskQueue instance;

    // Priority blocking queue for commands
    private final BlockingQueue<Command> commandQueue;

    // Thread pool for workers
    private final ExecutorService workerPool;

    // Workers, kept so they can be stopped on shutdown
    private final List<TaskWorker> workers = new CopyOnWriteArrayList<>();

    // Number of worker threads
    private static final int WORKER_COUNT = 3;

    // Queue statistics
    private final AtomicInteger processedCount;
    private final AtomicInteger failedCount;

    /**
     * Private constructor for singleton
     */
    private TaskQueue() {
        // Priority queue orders by command priority
        this.commandQueue = new PriorityBlockingQueue<>(100,
                (c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority()));

        // Fixed thread pool simulates distributed workers
        this.workerPool = Executors.newFixedThreadPool(WORKER_COUNT);

        // Statistics
        this.processedCount = new AtomicInteger(0);
        this.failedCount = new AtomicInteger(0);

        // Start workers
        startWorkers();
    }

    /**
     * Get singleton instance
     */
    public static TaskQueue getInstance() {
        if (instance == null) {
            synchronized (TaskQueue.class) {
                if (instance == null) {
                    instance = new TaskQueue();
                }
            }
        }
        return instance;
    }

    /**
     * Submit command to queue (Producer)
     * Non-blocking - returns immediately
     */
    public void submitCommand(Command command) {
        try {
            commandQueue.offer(command);
            System.out.println("Command queued: " + command.getDescription());
        } catch (Exception e) {
            System.err.println("Failed to queue command: " + e.getMessage());
        }
    }

    /**
     * Submit command with timeout
     */
    public boolean submitCommand(Command command, long timeout, TimeUnit unit) {
        try {
            return commandQueue.offer(command, timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Start worker threads
     */
    private void startWorkers() {
        for (int i = 0; i < WORKER_COUNT; i++) {
            TaskWorker worker = new TaskWorker("Worker-" + i, this);
            workers.add(worker);
            workerPool.submit(worker);
        }
    }

    /**
     * Get next command from queue (Consumer)
     * Blocks if queue is empty
     */
    public Command takeCommand() throws InterruptedException {
        return commandQueue.take();
    }

    /**
     * Get next command with timeout
     */
    public Command pollCommand(long timeout, TimeUnit unit) throws InterruptedException {
        return commandQueue.poll(timeout, unit);
    }

    /**
     * Record successful processing
     */
    public void recordSuccess() {
        processedCount.incrementAndGet();
    }

    /**
     * Record failed processing
     */
    public void recordFailure() {
        failedCount.incrementAndGet();
    }

    /**
     * Get queue size
     */
    public int getQueueSize() {
        return commandQueue.size();
    }

    /**
     * Get processing statistics
     */
    public String getStatistics() {
        return String.format("Queue Stats - Pending: %d, Processed: %d, Failed: %d",
                commandQueue.size(), processedCount.get(), failedCount.get());
    }

    /**
     * Shutdown the queue and workers
     */
    public void shutdown() {
        // Workers loop forever, so tell them to stop first (each exits within ~1 s)
        workers.forEach(TaskWorker::stop);
        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(5, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}



