import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class FurniturePanel extends JPanel {
    private final CanvasPanel canvasPanel;
    private final Map<String, ImageIcon> furnitureImages;
    private final Map<String, ImageIcon> fixtureImages;
    
    public FurniturePanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
        this.furnitureImages = new HashMap<>();
        this.fixtureImages = new HashMap<>();
        
        setLayout(new GridLayout(0, 2, 5, 5)); // 2 columns, 5px spacing
        setBorder(BorderFactory.createTitledBorder("Furniture & Fixtures"));
        
        initializeFurnitureImages();
        initializeFixtureImages();
        createButtons();
    }
    
    private void initializeFurnitureImages() {
        String[] furnitureTypes = {"bed", "chair", "table", "sofa", "dining_set"};
        for (String type : furnitureTypes) {
            loadImage(furnitureImages, type, "furniture");
        }
    }
    
    private void initializeFixtureImages() {
        String[] fixtureTypes = {"commode", "washbasin", "shower", "kitchen_sink", "stove"};
        for (String type : fixtureTypes) {
            loadImage(fixtureImages, type, "fixtures");
        }
    }
    
    private void loadImage(Map<String, ImageIcon> imageMap, String type, String folder) {
        try {
            ImageIcon icon = new ImageIcon("images/" + folder + "/" + type + ".png");
            // Scale image to button size
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            imageMap.put(type, new ImageIcon(img));
        } catch (Exception e) {
            System.err.println("Error loading image for " + type + ": " + e.getMessage());
            // Use a placeholder if image loading fails
            imageMap.put(type, createPlaceholderIcon());
        }
    }
    
    private ImageIcon createPlaceholderIcon() {
        // Create a simple colored rectangle as placeholder
        BufferedImage bi = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 40, 40);
        g.dispose();
        return new ImageIcon(bi);
    }
    
    private void createButtons() {
        // Add furniture buttons
        for (Map.Entry<String, ImageIcon> entry : furnitureImages.entrySet()) {
            addItemButton(entry.getKey(), entry.getValue(), "furniture");
        }
        
        // Add fixture buttons
        for (Map.Entry<String, ImageIcon> entry : fixtureImages.entrySet()) {
            addItemButton(entry.getKey(), entry.getValue(), "fixtures");
        }
    }
    
    private void addItemButton(String type, ImageIcon icon, String folder) {
        JButton button = new JButton(icon);
        button.setToolTipText(formatName(type));
        button.addActionListener(e -> {
            // Call addFurniture with the type and folder
            canvasPanel.addFurniture(type, folder);
        });
        add(button);
    }
    
    private String formatName(String name) {
        // Convert snake_case to Title Case
        String[] words = name.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            formatted.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return formatted.toString().trim();
    }
}