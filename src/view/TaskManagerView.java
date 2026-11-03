package view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.*;
import observer.*;
import controller.TaskController;
import strategy.*;

/**
 * TaskManagerView is the main GUI window.
 * Implements Observer to update when model changes.
 *
 * Design principles:
 * - MVC: View separated from Model and Controller
 * - Observer: Updates automatically when data changes
 * - No business logic in view
 */
public class TaskManagerView extends JFrame implements TaskObserver {
    // Controller reference
    private final TaskController controller;

    // GUI Components
    private JTable taskTable;
    private TaskTableModel tableModel;
    private JButton addButton, editButton, deleteButton, scheduleButton;
    private JComboBox<PriorityStrategy> sortStrategyCombo;
    private JLabel statusLabel;
    private JTextArea queueStatusArea;

    // Date formatter for display
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor sets up the GUI
     */
    public TaskManagerView(TaskController controller) {
        this.controller = controller;

        // Register as observer
        TaskManager.getInstance().attach(this);

        // Setup window
        setTitle("Distributed Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize components
        initializeComponents();

        // Layout components
        layoutComponents();

        // Load initial data
        refreshTaskList();

        // Set window properties
        setSize(900, 600);
        setLocationRelativeTo(null); // Center on screen
    }

    /**
     * Initialize all GUI components
     */
    private void initializeComponents() {
        // Create table model and table
        tableModel = new TaskTableModel();
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(25);

        // Configure table columns
        configureTableColumns();

        // Create buttons
        addButton = new JButton("Add Task");
        editButton = new JButton("Edit Task");
        deleteButton = new JButton("Delete Task");
        scheduleButton = new JButton("Auto Schedule");

        // Add button actions using method references (no lambdas as requested)
        addButton.addActionListener(new AddTaskListener());
        editButton.addActionListener(new EditTaskListener());
        deleteButton.addActionListener(new DeleteTaskListener());
        scheduleButton.addActionListener(new ScheduleTaskListener());

        // Create sort strategy combo box
        sortStrategyCombo = new JComboBox<>();
        sortStrategyCombo.addItem(new HighPriorityFirstStrategy());
        sortStrategyCombo.addItem(new DeadlineFirstStrategy());
        sortStrategyCombo.addActionListener(new SortStrategyListener());

        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Queue status area
        queueStatusArea = new JTextArea(3, 40);
        queueStatusArea.setEditable(false);
        queueStatusArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    /**
     * Configure table columns with renderers
     */
    private void configureTableColumns() {
        TableColumnModel columnModel = taskTable.getColumnModel();

        // Set column widths
        columnModel.getColumn(0).setPreferredWidth(200); // Title
        columnModel.getColumn(1).setPreferredWidth(100); // Priority
        columnModel.getColumn(2).setPreferredWidth(150); // Due Date
        columnModel.getColumn(3).setPreferredWidth(100); // Status
        columnModel.getColumn(4).setPreferredWidth(80);  // Overdue

        // Custom renderer for priority column
        columnModel.getColumn(1).setCellRenderer(new PriorityRenderer());

        // Custom renderer for status column
        columnModel.getColumn(3).setCellRenderer(new StatusRenderer());

        // Custom renderer for overdue column
        columnModel.getColumn(4).setCellRenderer(new OverdueRenderer());
    }

    /**
     * Layout all components in the frame
     */
    private void layoutComponents() {
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(scheduleButton);
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortStrategyCombo);

        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tasks"));

        // Bottom panel with status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        // Queue status panel
        JPanel queuePanel = new JPanel(new BorderLayout());
        queuePanel.setBorder(BorderFactory.createTitledBorder("Queue Status"));
        queuePanel.add(new JScrollPane(queueStatusArea), BorderLayout.CENTER);
        bottomPanel.add(queuePanel, BorderLayout.EAST);

        // Add all to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Start queue status updater
        startQueueStatusUpdater();
    }

    /**
     * Refresh the task list from model
     */
    private void refreshTaskList() {
        // Copy: getAllTasks() returns a read-only list, and sorting it crashed the app on start
        List<Task> tasks = new java.util.ArrayList<>(controller.getAllTasks());

        // Apply current sort strategy
        PriorityStrategy strategy = (PriorityStrategy) sortStrategyCombo.getSelectedItem();
        if (strategy != null) {
            strategy.sortTasks(tasks);
        }

        tableModel.setTasks(tasks);
    }

    /**
     * Observer method - called when model changes
     */
    @Override
    public void update(TaskEvent event) {
        // Update on EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshTaskList();
                statusLabel.setText("Event: " + event.getType().getDescription());
            }
        });
    }

    /**
     * Start background thread to update queue status
     */
    private void startQueueStatusUpdater() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stats = controller.getQueueStatistics();
                queueStatusArea.setText(stats);
            }
        });
        timer.start();
    }

    // Inner classes for action listeners (avoiding lambdas as requested)

    /**
     * Listener for Add Task button
     */
    private class AddTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TaskDialog dialog = new TaskDialog(TaskManagerView.this, null);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                controller.createTask(
                        dialog.getTaskTitle(),
                        dialog.getTaskDescription(),
                        dialog.getDueDate(),
                        dialog.getPriority()
                );
            }
        }
    }

    /**
     * Listener for Edit Task button
     */
    private class EditTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow >= 0) {
                Task task = tableModel.getTaskAt(selectedRow);
                TaskDialog dialog = new TaskDialog(TaskManagerView.this, task);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    task.setTitle(dialog.getTaskTitle());
                    task.setDescription(dialog.getTaskDescription());
                    task.setDueDate(dialog.getDueDate());
                    task.setPriority(dialog.getPriority());

                    controller.updateTask(task);
                }
            } else {
                JOptionPane.showMessageDialog(TaskManagerView.this,
                        "Please select a task to edit.");
            }
        }
    }

    /**
     * Listener for Delete Task button
     */
    private class DeleteTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow >= 0) {
                Task task = tableModel.getTaskAt(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(
                        TaskManagerView.this,
                        "Delete task: " + task.getTitle() + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    controller.deleteTask(task.getId());
                }
            } else {
                JOptionPane.showMessageDialog(TaskManagerView.this,
                        "Please select a task to delete.");
            }
        }
    }

    /**
     * Listener for Schedule Task button
     */
    private class ScheduleTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow >= 0) {
                Task task = tableModel.getTaskAt(selectedRow);
                controller.scheduleTask(task);
                JOptionPane.showMessageDialog(TaskManagerView.this,
                        "Task scheduled. Check for conflicts resolved.");
            } else {
                JOptionPane.showMessageDialog(TaskManagerView.this,
                        "Please select a task to schedule.");
            }
        }
    }

    /**
     * Listener for sort strategy combo box
     */
    private class SortStrategyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshTaskList();
        }
    }
}