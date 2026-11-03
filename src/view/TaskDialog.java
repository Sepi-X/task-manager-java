package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import model.*;

/**
 * TaskDialog for creating and editing tasks.
 * Modal dialog that collects task information.
 *
 * Design principles:
 * - Separation of concerns: Only UI logic, no business logic
 * - Validation: Input validation before accepting
 * - Reusable: Same dialog for create and edit
 */
public class TaskDialog extends JDialog {
    // Form fields
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField dueDateField;
    private JComboBox<Priority> priorityCombo;

    // Buttons
    private JButton okButton;
    private JButton cancelButton;

    // Result flag
    private boolean confirmed = false;

    // Date formatter with example
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String DATE_EXAMPLE = "2025-12-31 14:30";

    /**
     * Constructor for new task
     */
    public TaskDialog(Frame parent) {
        this(parent, null);
    }

    /**
     * Constructor for new or edit task
     */
    public TaskDialog(Frame parent, Task task) {
        super(parent, task == null ? "New Task" : "Edit Task", true);

        initializeComponents();
        layoutComponents();

        // If editing, populate fields
        if (task != null) {
            populateFields(task);
        }

        // Set dialog properties
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    /**
     * Initialize dialog components
     */
    private void initializeComponents() {
        // Text fields
        titleField = new JTextField(30);
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // Due date field with tooltip
        dueDateField = new JTextField(20);
        dueDateField.setToolTipText("Format: " + DATE_EXAMPLE);

        // Priority combo box
        priorityCombo = new JComboBox<>(Priority.values());
        priorityCombo.setSelectedItem(Priority.MEDIUM); // Default

        // Buttons
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        // Button actions
        okButton.addActionListener(new OkButtonListener());
        cancelButton.addActionListener(new CancelButtonListener());

        // Enable enter key for OK
        getRootPane().setDefaultButton(okButton);
    }

    /**
     * Layout components using GridBagLayout
     */
    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title row
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleField, gbc);

        // Description row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        mainPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, gbc);

        // Due date row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Due Date:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.add(dueDateField);
        datePanel.add(new JLabel(" (" + DATE_EXAMPLE + ")"));
        mainPanel.add(datePanel, gbc);

        // Priority row
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Priority:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(priorityCombo, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add to dialog
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Populate fields for editing
     */
    private void populateFields(Task task) {
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        dueDateField.setText(DATE_FORMAT.format(task.getDueDate()));
        priorityCombo.setSelectedItem(task.getPriority());
    }

    /**
     * Validate input fields
     */
    private boolean validateInput() {
        // Validate title
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Title cannot be empty");
            titleField.requestFocus();
            return false;
        }

        // Validate due date
        try {
            LocalDateTime.parse(dueDateField.getText().trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            showError("Invalid date format. Use: " + DATE_EXAMPLE);
            dueDateField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // Getters for dialog results

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getTaskTitle() {
        return titleField.getText().trim();
    }

    public String getTaskDescription() {
        return descriptionArea.getText().trim();
    }

    public LocalDateTime getDueDate() {
        return LocalDateTime.parse(dueDateField.getText().trim(), DATE_FORMAT);
    }

    public Priority getPriority() {
        return (Priority) priorityCombo.getSelectedItem();
    }

    // Inner classes for action listeners

    /**
     * OK button listener
     */
    private class OkButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        }
    }

    /**
     * Cancel button listener
     */
    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            confirmed = false;
            dispose();
        }
    }
}