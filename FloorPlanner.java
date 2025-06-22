import java.awt.*;
import javax.swing.*;

public class FloorPlanner extends JFrame {
    private CanvasPanel canvas; // For drawing the floor plan
    private JPanel controlPanel; // For adding controls
    
    public FloorPlanner() {
        setTitle("2D Floor Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize Canvas
        canvas = new CanvasPanel();
        canvas.setBackground(Color.LIGHT_GRAY);

        // Initialize Control Panel
        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(200, getHeight()));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        addButtonsToControlPanel(controlPanel);

        add(canvas, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        setVisible(true);
    }

    private void addButtonsToControlPanel(JPanel controlPanel) {
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> canvas.addRoom());
        controlPanel.add(addRoomButton);

        JButton addDoorButton = new JButton("Add Door");
        addDoorButton.addActionListener(e -> canvas.addDoor());
        controlPanel.add(addDoorButton);

        JButton addWindowButton = new JButton("Add Window");
        addWindowButton.addActionListener(e -> canvas.addWindow());
        controlPanel.add(addWindowButton);

        // Removing the generic addFurniture button since we have FurniturePanel for this
        // JButton addFurnitureButton = new JButton("Add Furniture");
        // addFurnitureButton.addActionListener(e -> canvas.addFurniture("chair", "furniture")); // Remove this line

        JButton saveButton = new JButton("Save Plan");
        saveButton.addActionListener(e -> canvas.savePlan());
        controlPanel.add(saveButton);

        JButton loadButton = new JButton("Load Plan");
        loadButton.addActionListener(e -> canvas.loadPlan());
        controlPanel.add(loadButton);

        controlPanel.add(new FurniturePanel(canvas));
    }

    public static void main(String[] args) {
        new FloorPlanner();
    }
}