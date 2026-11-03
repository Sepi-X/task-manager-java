import javax.swing.*;
import controller.TaskController;
import view.TaskManagerView;

/**
 * Main class - Entry point for the Distributed Task Manager application.
 *
 * This class demonstrates:
 * - Proper Swing initialization on EDT (Event Dispatch Thread)
 * - MVC pattern initialization
 * - Clean shutdown handling
 *
 * To run: java Main
 */
public class Main {

    /**
     * Main method - application entry point
     */
    public static void main(String[] args) {
        // Set system look and feel for better UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If system look and feel fails, continue with default
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Initialize application on EDT (Swing requirement)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initializeApplication();
            }
        });
    }

    /**
     * Initialize the application components
     */
    private static void initializeApplication() {
        try {
            // Create controller
            TaskController controller = new TaskController();

            // Create and show main window
            TaskManagerView mainView = new TaskManagerView(controller);

            // Add shutdown hook for clean shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Shutting down task manager...");
                    controller.shutdown();
                }
            }));

            // Show the window
            mainView.setVisible(true);

            System.out.println("Distributed Task Manager started successfully");

        } catch (Exception e) {
            // Print the full error, then show a dialog
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }
    }
}