package model;

/**
 * Priority enum defines the priority levels for tasks.
 * Using enums instead of strings or integers provides:
 * - Type safety: Can't assign invalid priority
 * - Readability: Code is self-documenting
 * - Maintainability: Easy to add new priorities
 *
 * The order matters here - declared from highest to lowest priority.
 * This allows natural ordering using ordinal() method.
 */
public enum Priority {
    /**
     * URGENT - Highest priority, needs immediate attention
     */
    URGENT("Urgent", 4, "🔴"),

    /**
     * HIGH - Important but not urgent
     */
    HIGH("High", 3, "🟠"),

    /**
     * MEDIUM - Regular priority
     */
    MEDIUM("Medium", 2, "🟡"),

    /**
     * LOW - Can be done when time permits
     */
    LOW("Low", 1, "🟢");

    // Properties for each priority level
    private final String displayName;
    private final int weight;  // Numerical weight for calculations
    private final String icon; // Visual indicator

    /**
     * Private constructor - enums cannot be instantiated outside
     * This ensures only the defined constants exist
     */
    Priority(String displayName, int weight, String icon) {
        this.displayName = displayName;
        this.weight = weight;
        this.icon = icon;
    }

    /**
     * Get human-readable name for UI display
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get numerical weight for priority calculations
     * Higher number = higher priority
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Get icon for visual representation
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Compare two priorities.
     * Returns positive if this priority is higher, negative if lower, 0 if equal.
     * This method enables sorting without if-else statements.
     */
    public int compareByWeight(Priority other) {
        return Integer.compare(this.weight, other.weight);
    }

    /**
     * Check if this priority is higher than another
     * Useful for priority-based decisions without if-else chains
     */
    public boolean isHigherThan(Priority other) {
        return this.weight > other.weight;
    }

    @Override
    public String toString() {
        return displayName;
    }
}