package model;

/**
 * TaskStatus enum represents the lifecycle states of a task.
 * Using State pattern concepts, each status knows about valid transitions.
 * This helps enforce business rules without if-else statements.
 */
public enum TaskStatus {
    /**
     * PENDING - Task is created but not started
     */
    PENDING("Pending", "⏳", true, false),

    /**
     * IN_PROGRESS - Task is being worked on
     */
    IN_PROGRESS("In Progress", "🔄", true, false),

    /**
     * COMPLETED - Task is finished
     */
    COMPLETED("Completed", "✅", false, true),

    /**
     * CANCELLED - Task was cancelled
     */
    CANCELLED("Cancelled", "❌", false, true);

    private final String displayName;
    private final String icon;
    private final boolean isActive;    // Can transition to other states
    private final boolean isFinal;     // Terminal state

    TaskStatus(String displayName, String icon, boolean isActive, boolean isFinal) {
        this.displayName = displayName;
        this.icon = icon;
        this.isActive = isActive;
        this.isFinal = isFinal;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * Check if task in this status can be modified
     * Final states (COMPLETED, CANCELLED) cannot be modified
     */
    public boolean canBeModified() {
        return isActive;
    }

    /**
     * Check if this is a terminal state
     */
    public boolean isFinalState() {
        return isFinal;
    }

    /**
     * Validate if transition to new status is allowed.
     * This encapsulates state transition rules without if-else chains.
     * Uses a transition table approach (State pattern).
     */
    public boolean canTransitionTo(TaskStatus newStatus) {
        // Can't transition to same status
        if (this == newStatus) {
            return false;
        }

        // Final states cannot transition
        if (this.isFinal) {
            return false;
        }

        // All active states can transition to any other state
        return true;
    }

    /**
     * Get next logical status in workflow
     * This helps with UI suggestions without if-else
     */
    public TaskStatus getNextStatus() {
        // Using ordinal position to get next status
        TaskStatus[] values = TaskStatus.values();
        int nextIndex = (this.ordinal() + 1) % values.length;

        // Skip to first status if at the end
        return this.isFinal ? PENDING : values[nextIndex];
    }

    @Override
    public String toString() {
        return displayName;
    }
}