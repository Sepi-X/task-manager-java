// File: view/PriorityRenderer.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import model.Priority;

/**
 * Custom renderer for Priority column.
 * Shows priority with color and icon.
 */
public class PriorityRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        // Call parent to get default component
        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (value instanceof Priority) {
            Priority priority = (Priority) value;

            // Set text with icon
            setText(priority.getIcon() + " " + priority.getDisplayName());

            // Set tooltip
            setToolTipText("Priority weight: " + priority.getWeight());

            // Center align
            setHorizontalAlignment(CENTER);
        }

        return c;
    }
}
