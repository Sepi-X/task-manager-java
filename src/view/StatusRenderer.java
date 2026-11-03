// File: view/StatusRenderer.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import model.TaskStatus;

/**
 * Custom renderer for Status column.
 * Shows status with icon and appropriate colors.
 */
public class StatusRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (value instanceof TaskStatus) {
            TaskStatus status = (TaskStatus) value;

            // Set text with icon
            setText(status.getIcon() + " " + status.getDisplayName());

            // Set colors based on status (using polymorphism)
            applyStatusColors(status, isSelected);

            // Center align
            setHorizontalAlignment(CENTER);
        }

        return c;
    }

    /**
     * Apply colors based on status
     */
    private void applyStatusColors(TaskStatus status, boolean isSelected) {
        if (!isSelected) {
            // Use status properties to determine color
            if (status.isFinalState()) {
                setBackground(new Color(220, 255, 220)); // Light green
            } else {
                setBackground(new Color(255, 255, 220)); // Light yellow
            }
        }
    }
}