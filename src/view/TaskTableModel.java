// File: view/TaskTableModel.java
package view;

import javax.swing.table.AbstractTableModel;
import model.Task;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

/**
 * TaskTableModel is a custom table model for displaying tasks.
 * Extends AbstractTableModel to provide data to JTable.
 *
 * Follows MVC pattern - this is part of the View layer.
 */
public class TaskTableModel extends AbstractTableModel {
    // Column names
    private static final String[] COLUMN_NAMES = {
            "Title", "Priority", "Due Date", "Status", "Overdue"
    };

    // Column types for proper sorting and rendering
    private static final Class<?>[] COLUMN_TYPES = {
            String.class, Object.class, String.class, Object.class, Boolean.class
    };

    // Data storage
    private List<Task> tasks;

    // Date formatter
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor initializes empty task list
     */
    public TaskTableModel() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Set the tasks to display
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks); // Defensive copy
        fireTableDataChanged(); // Notify table to refresh
    }

    /**
     * Get task at specific row
     */
    public Task getTaskAt(int row) {
        if (row >= 0 && row < tasks.size()) {
            return tasks.get(row);
        }
        return null;
    }

    // AbstractTableModel implementation

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return COLUMN_TYPES[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Task task = tasks.get(row);

        // Return appropriate value for each column
        // Note: No switch statement to avoid violating OCP
        return getColumnValue(task, column);
    }

    /**
     * Get value for specific column using array lookup
     * Avoids switch/if-else chain
     */
    private Object getColumnValue(Task task, int column) {
        // Use array of value providers
        Object[] values = {
                task.getTitle(),
                task.getPriority(),
                DATE_FORMAT.format(task.getDueDate()),
                task.getStatus(),
                task.isOverdue()
        };

        return values[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // Table cells are not directly editable
        return false;
    }
}




