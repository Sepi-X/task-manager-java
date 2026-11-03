// File: view/OverdueRenderer.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom renderer for Overdue column.
 * Shows warning icon for overdue tasks.
 */
public class OverdueRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (value instanceof Boolean) {
            Boolean overdue = (Boolean) value;

            if (overdue) {
                setText("⚠️ Overdue");
                if (!isSelected) {
                    setBackground(new Color(255, 220, 220)); // Light red
                }
            } else {
                setText("On Time");
                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
            }

            // Center align
            setHorizontalAlignment(CENTER);
        }

        return c;
    }
}