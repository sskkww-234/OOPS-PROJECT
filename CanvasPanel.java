import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
class CanvasPanel extends JPanel {
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Door> doors = new ArrayList<>();
    private ArrayList<Window> windows = new ArrayList<>();
    private ArrayList<Furniture> furnitureList = new ArrayList<>();
    private Room selectedRoom = null;
    private Furniture selectedFurniture = null;
    private Point initialClick = null;
    private int gridSize = 50;

    public CanvasPanel() {
        // Mouse Listener for dragging rooms
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedRoom = getRoomAtPoint(e.getPoint());
                if (selectedRoom != null) {
                    initialClick = e.getPoint();
                    // Store the original position
                    selectedRoom.saveOriginalPosition();
                }
            }
    
            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedRoom != null) {
                    selectedRoom.x = snapToGrid(selectedRoom.x, gridSize);
                    selectedRoom.y = snapToGrid(selectedRoom.y, gridSize);
                    // Check for overlap after dragging
                    if (isOverlap(new Rectangle(selectedRoom.x, selectedRoom.y, selectedRoom.width, selectedRoom.height))) {
                        JOptionPane.showMessageDialog(CanvasPanel.this, "Overlap error! Reverting to original position.");
                        selectedRoom.revertPosition(); // Snap back to original position
                    }
                    repaint();
                }
                selectedRoom = null; // Clear selection
            }
            
        });
    
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedRoom != null && initialClick != null) {
                    int dx = e.getX() - initialClick.x;
                    int dy = e.getY() - initialClick.y;
                    selectedRoom.move(dx, dy);
                    initialClick = e.getPoint();
                    repaint();
                }
            }
        });
        

    }
    private int snapToGrid(int coordinate, int gridSize) {
        return Math.round((float) coordinate / gridSize) * gridSize;
    }


    private void drawGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        int width = getWidth();
        int height = getHeight();

        // Draw vertical grid lines
        for (int x = 0; x <= width; x += gridSize) {
            g.drawLine(x, 0, x, height);
        }

        // Draw horizontal grid lines
        for (int y = 0; y <= height; y += gridSize) {
            g.drawLine(0, y, width, y);
        }
    }
    

    private boolean isOverlap(Rectangle draggedRoom) {
        for (Room room : rooms) {
            Rectangle existingRoom = new Rectangle(room.x, room.y, room.width, room.height);
            if (!draggedRoom.equals(existingRoom) && draggedRoom.intersects(existingRoom)) {
                return true;
            }
        }
        return false;
    }
    
    class Wall {
        int x1, y1, x2, y2;
    
        public Wall(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    
        public void draw(Graphics g, ArrayList<Window> windows) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
    
            // Check for windows along the wall
            for (Window window : windows) {
                if (isHorizontal()) {
                    if (y1 == window.y && window.x >= x1 && window.x <= x2) {
                        // Draw left segment of the wall up to the window
                        g2d.drawLine(x1, y1, window.x - 15, y1);
                        // Draw right segment of the wall after the window
                        g2d.drawLine(window.x + 15, y1, x2, y2);
                        return; // Stop further drawing for this wall
                    }
                } else {
                    if (x1 == window.x && window.y >= y1 && window.y <= y2) {
                        // Draw top segment of the wall up to the window
                        g2d.drawLine(x1, y1, x1, window.y - 15);
                        // Draw bottom segment of the wall after the window
                        g2d.drawLine(x1, window.y + 15, x2, y2);
                        return; // Stop further drawing for this wall
                    }
                }
            }
    
            // Draw the full wall if no window intersects
            g2d.drawLine(x1, y1, x2, y2);
        }
    
        public boolean isHorizontal() {
            return y1 == y2; // True if the wall is horizontal
        }
    }
    
    // Add this method inside the CanvasPanel class
private Point[] getSharedWall(Room room1, Room room2) {
    // Check if the two rooms share a vertical wall
    if (room1.x + room1.width == room2.x || room2.x + room2.width == room1.x) {
        int sharedYStart = Math.max(room1.y, room2.y);
        int sharedYEnd = Math.min(room1.y + room1.height, room2.y + room2.height);
        if (sharedYStart < sharedYEnd) { // Ensure there's overlap
            int x = room1.x + room1.width == room2.x ? room1.x + room1.width : room2.x + room2.width;
            return new Point[]{new Point(x, sharedYStart), new Point(x, sharedYEnd)};
        }
    }

    // Check if the two rooms share a horizontal wall
    if (room1.y + room1.height == room2.y || room2.y + room2.height == room1.y) {
        int sharedXStart = Math.max(room1.x, room2.x);
        int sharedXEnd = Math.min(room1.x + room1.width, room2.x + room2.width);
        if (sharedXStart < sharedXEnd) { // Ensure there's overlap
            int y = room1.y + room1.height == room2.y ? room1.y + room1.height : room2.y + room2.height;
            return new Point[]{new Point(sharedXStart, y), new Point(sharedXEnd, y)};
        }
    }

    return null; // No shared wall
}

    

    public void addRoom() {
        // Room Types and Colors
        String[] roomTypes = {"Bedroom", "Bathroom", "Kitchen", "Living/Dining"};
        Color[] colors = {Color.GREEN, Color.BLUE, Color.RED, Color.ORANGE};
    
        // Ask for Room Type
        String roomType = (String) JOptionPane.showInputDialog(
                this, "Select Room Type:", "Add Room",
                JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
    
        if (roomType != null) {
            // Ask for Room Dimensions
            try {
                int width = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Room Width (e.g., 100):"));
                int height = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Room Height (e.g., 100):"));
    
                if (width > 0 && height > 0) {
                    // Ask if the room should be placed relative to an existing room
                    int response = JOptionPane.showConfirmDialog(
                            this, "Do you want to place this room relative to an existing room?", 
                            "Relative Placement", JOptionPane.YES_NO_OPTION);
    
                    if (response == JOptionPane.YES_OPTION) {
                        // Select a reference room
                        Room referenceRoom = selectRoom("Select a reference room:");
                        if (referenceRoom != null) {
                            // Ask for relative position
                            String[] directions = {"North", "South", "East", "West"};
                            String direction = (String) JOptionPane.showInputDialog(
                                    this, "Select Relative Position:", "Relative Position",
                                    JOptionPane.QUESTION_MESSAGE, null, directions, directions[0]);
    
                            // Ask for alignment
                            String[] alignments = {"Left", "Center", "Right"};
                            String alignment = (String) JOptionPane.showInputDialog(
                                    this, "Select Alignment:", "Alignment",
                                    JOptionPane.QUESTION_MESSAGE, null, alignments, alignments[0]);
    
                            // Calculate position
                            Point newPosition = calculateRelativePosition(referenceRoom, direction, alignment, width, height);
    
                            // Check for overlap
                            if (newPosition != null && !isOverlap(new Rectangle(newPosition.x, newPosition.y, width, height))) {
                                int index = java.util.Arrays.asList(roomTypes).indexOf(roomType);
                                Room newRoom = new Room(newPosition.x, newPosition.y, width, height, colors[index]);
                                rooms.add(newRoom);
                                repaint();
                            } else {
                                JOptionPane.showMessageDialog(this, "Cannot place the room here! It overlaps with another room.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        // Place room at the next available position (row-major order)
                        Point nextPosition = getNextAvailablePosition(width, height);
                        if (nextPosition != null) {
                            int index = java.util.Arrays.asList(roomTypes).indexOf(roomType);
                            Room newRoom = new Room(nextPosition.x, nextPosition.y, width, height, colors[index]);
                            rooms.add(newRoom);
                            repaint();
                        } else {
                            JOptionPane.showMessageDialog(this, "No space available for the new room!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Dimensions must be positive integers!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid integers for width and height.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Point calculateRelativePosition(Room referenceRoom, String direction, String alignment, int roomWidth, int roomHeight) {
        int x = referenceRoom.x;
        int y = referenceRoom.y;
    
        switch (direction) {
            case "North":
                y -= roomHeight + 10; // Place above the reference room
                break;
            case "South":
                y += referenceRoom.height + 10; // Place below the reference room
                break;
            case "East":
                x += referenceRoom.width + 10; // Place to the right of the reference room
                break;
            case "West":
                x -= roomWidth + 10; // Place to the left of the reference room
                break;
        }
    
        // Adjust alignment
        if (direction.equals("North") || direction.equals("South")) {
            switch (alignment) {
                case "Left":
                    break; // Keep x unchanged
                case "Center":
                    x += (referenceRoom.width - roomWidth) / 2;
                    break;
                case "Right":
                    x += referenceRoom.width - roomWidth;
                    break;
            }
        } else if (direction.equals("East") || direction.equals("West")) {
            switch (alignment) {
                case "Left":
                    break; // Keep y unchanged
                case "Center":
                    y += (referenceRoom.height - roomHeight) / 2;
                    break;
                case "Right":
                    y += referenceRoom.height - roomHeight;
                    break;
            }
        }
    
        return new Point(x, y);
    }

    private ArrayList<Wall> calculateWalls() {
        ArrayList<Wall> walls = new ArrayList<>();
    
        for (Room room : rooms) {
            boolean hasSharedWall;
    
            // Check top wall
            hasSharedWall = false;
            for (Room other : rooms) {
                if (room != other && room.x == other.x && room.x + room.width == other.x + other.width &&
                    room.y == other.y + other.height) {
                    hasSharedWall = true;
                    break;
                }
            }
            if (!hasSharedWall) {
                walls.add(new Wall(room.x, room.y, room.x + room.width, room.y));
            }
    
            // Check bottom wall
            hasSharedWall = false;
            for (Room other : rooms) {
                if (room != other && room.x == other.x && room.x + room.width == other.x + other.width &&
                    room.y + room.height == other.y) {
                    hasSharedWall = true;
                    break;
                }
            }
            if (!hasSharedWall) {
                walls.add(new Wall(room.x, room.y + room.height, room.x + room.width, room.y + room.height));
            }
    
            // Check left wall
            hasSharedWall = false;
            for (Room other : rooms) {
                if (room != other && room.y == other.y && room.y + room.height == other.y + other.height &&
                    room.x == other.x + other.width) {
                    hasSharedWall = true;
                    break;
                }
            }
            if (!hasSharedWall) {
                walls.add(new Wall(room.x, room.y, room.x, room.y + room.height));
            }
    
            // Check right wall
            hasSharedWall = false;
            for (Room other : rooms) {
                if (room != other && room.y == other.y && room.y + room.height == other.y + other.height &&
                    room.x + room.width == other.x) {
                    hasSharedWall = true;
                    break;
                }
            }
            if (!hasSharedWall) {
                walls.add(new Wall(room.x + room.width, room.y, room.x + room.width, room.y + room.height));
            }
        }
    
        return walls;
    }
    
    
    
    
    private Point getNextAvailablePosition(int roomWidth, int roomHeight) {
        int canvasWidth = getWidth();  // Total width of the canvas
        int padding = 10;             // Space between rooms
        int currentX = padding;
        int currentY = padding;
    
        // Try positions row by row
        while (true) {
            boolean positionFound = true;
    
            // Check if the room fits in the canvas
            if (currentX + roomWidth > canvasWidth) {
                // Move to the next row
                currentX = padding;
                currentY += roomHeight + padding;
    
                // Stop if the position exceeds canvas height
                if (currentY + roomHeight > getHeight()) {
                    return null; // No space available
                }
            }
    
            // Check for overlap with existing rooms
            for (Room room : rooms) {
                if (new Rectangle(currentX, currentY, roomWidth, roomHeight).intersects(
                        new Rectangle(room.x, room.y, room.width, room.height))) {
                    positionFound = false;
                    break;
                }
            }
    
            // If the position is valid, return it
            if (positionFound) {
                return new Point(currentX, currentY);
            }
    
            // Move to the next position in the row
            currentX += roomWidth + padding;
        }
    }

    private Room selectRoom(String message) {
        for (Room room : rooms) {
            int response = JOptionPane.showConfirmDialog(this, message + "\n" + room, "Select Room",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                return room;
            }
        }
        JOptionPane.showMessageDialog(this, "No room selected!", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }
    
        
    

    public void addDoor() {
        Room targetRoom = selectRoom("Select a room to add a door:");
        if (targetRoom != null) {
            String[] orientations = {"Horizontal", "Vertical"};
            String orientation = (String) JOptionPane.showInputDialog(
                    this, "Select Door Orientation:", "Add Door",
                    JOptionPane.QUESTION_MESSAGE, null, orientations, orientations[0]);
    
            if (orientation != null) {
                boolean isHorizontal = orientation.equals("Horizontal");
                int x = isHorizontal ? targetRoom.x + targetRoom.width / 2 : targetRoom.x;
                int y = isHorizontal ? targetRoom.y : targetRoom.y + targetRoom.height / 2;
    
                // Check for overlap with existing doors or windows
                if (isDoorOverlap(x, y, isHorizontal) || isWindowOverlap(x, y, isHorizontal)) {
                    JOptionPane.showMessageDialog(this, "Overlap detected! Cannot place the door here.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Add the door
                doors.add(new Door(x, y, isHorizontal));
                repaint();
            }
        }
    }
    
    
    

    public void addWindow() {
        Room targetRoom = selectRoom("Select a room to add a window:");
        if (targetRoom != null) {
            String[] orientations = {"Horizontal", "Vertical"};
            String orientation = (String) JOptionPane.showInputDialog(
                    this, "Select Window Orientation:", "Add Window",
                    JOptionPane.QUESTION_MESSAGE, null, orientations, orientations[0]);
    
            if (orientation != null) {
                boolean isHorizontal = orientation.equals("Horizontal");
                int x = isHorizontal ? targetRoom.x + targetRoom.width / 2 : targetRoom.x;
                int y = isHorizontal ? targetRoom.y : targetRoom.y + targetRoom.height / 2;
    
                // Check for overlap with existing doors or windows
                if (isDoorOverlap(x, y, isHorizontal) || isWindowOverlap(x, y, isHorizontal)) {
                    JOptionPane.showMessageDialog(this, "Overlap detected! Cannot place the window here.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Check if the window is on a single room's border
                if (!isWindowOnSingleRoom(x, y, isHorizontal)) {
                    JOptionPane.showMessageDialog(this, "Invalid placement! Windows cannot be placed between rooms.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Add the window
                windows.add(new Window(x, y, isHorizontal));
                repaint();
            }
        }
    }
    
    
    

    // In CanvasPanel.java, replace the existing addFurniture() method with this one:

// public void addFurniture(String type, String folder) {
//     // Select a Room
//     Room targetRoom = selectRoom("Select a room to add " + formatName(type) + ":");
//     if (targetRoom != null) {
//         try {
//             // Load the image for the selected furniture
//             String imagePath = "images/" + folder + "/" + type + ".png";
//             Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
            
//             // Set default position to the center of the target room
//             int x = targetRoom.x + (targetRoom.width / 2) - 20; // Center - half of default width
//             int y = targetRoom.y + (targetRoom.height / 2) - 20; // Center - half of default height
            
//             // Default sizes based on furniture type
//             int width = 40;
//             int height = 40;
            
//             // Adjust size based on furniture type
//             switch(type) {
//                 case "bed":
//                     width = 80;
//                     height = 60;
//                     break;
//                 case "sofa":
//                     width = 70;
//                     height = 40;
//                     break;
//                 case "dining_set":
//                     width = 60;
//                     height = 60;
//                     break;
//                 case "table":
//                     width = 50;
//                     height = 50;
//                     break;
//                 // Default size for other items is 40x40
//             }
            
//             // Create a new Furniture object with the image
//             Furniture furniture = new Furniture(x, y, width, height, image);
//             furnitureList.add(furniture);
//             repaint();
            
//         } catch (Exception e) {
//             e.printStackTrace();
//             JOptionPane.showMessageDialog(this, 
//                 "Error loading furniture image: " + e.getMessage(), 
//                 "Error", 
//                 JOptionPane.ERROR_MESSAGE);
//         }
//     }
// }

// Add this helper method to format the furniture name
private String formatName(String name) {
    String[] words = name.split("_");
    StringBuilder formatted = new StringBuilder();
    for (String word : words) {
        formatted.append(Character.toUpperCase(word.charAt(0)))
                .append(word.substring(1).toLowerCase())
                .append(" ");
    }
    return formatted.toString().trim();
}

// Keep the old addFurniture() method for backward compatibility
public void addFurniture(String type, String folder) {
    Room targetRoom = selectRoom("Select a room to add " + formatName(type) + ":");
    if (targetRoom != null) {
        try {
            String imagePath = "images/" + folder + "/" + type + ".png";
            Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
            
            int x = targetRoom.x + (targetRoom.width / 2) - 20;
            int y = targetRoom.y + (targetRoom.height / 2) - 20;
            
            int width = 40;
            int height = 40;
            
            switch(type) {
                case "bed":
                    width = 80;
                    height = 60;
                    break;
                case "sofa":
                    width = 70;
                    height = 40;
                    break;
                case "dining_set":
                    width = 60;
                    height = 60;
                    break;
                case "table":
                    width = 50;
                    height = 50;
                    break;
            }
            
            Furniture furniture = new Furniture(x, y, width, height, image);
            furniture.setImagePath(imagePath); // Set the image path for serialization
            furnitureList.add(furniture);
            repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading furniture image: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    

    // private Room selectRoom(String message) {
    //     for (Room room : rooms) {
    //         if (JOptionPane.showConfirmDialog(this, message + ": " + room, "Select Room",
    //                 JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
    //             return room;
    //         }
    //     }
    //     return null;
    // }

    private Room getRoomAtPoint(Point p) {
        for (Room room : rooms) {
            if (room.contains(p)) {
                return room;
            }
        }
        return null;
    }

    private boolean isOverlap(Room currentRoom) {
        for (Room room : rooms) {
            if (room != currentRoom && room.intersects(currentRoom)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDoorOverlap(int x, int y, boolean isHorizontal) {
        for (Door door : doors) {
            if (door.x == x && door.y == y && door.isHorizontal == isHorizontal) {
                return true; // Overlap detected
            }
        }
        return false;
    }
    
    private boolean isWindowOverlap(int x, int y, boolean isHorizontal) {
        for (Window window : windows) {
            if (window.x == x && window.y == y && window.isHorizontal == isHorizontal) {
                return true; // Overlap detected
            }
        }
        return false;
    }

    private boolean isWindowOnSingleRoom(int x, int y, boolean isHorizontal) {
        int count = 0; // Count how many rooms the window aligns with
    
        for (Room room : rooms) {
            if (isHorizontal) {
                // Check if the window is on the top or bottom border of the room
                if (y == room.y || y == room.y + room.height) {
                    if (x >= room.x && x <= room.x + room.width) {
                        count++;
                    }
                }
            } else {
                // Check if the window is on the left or right border of the room
                if (x == room.x || x == room.x + room.width) {
                    if (y >= room.y && y <= room.y + room.height) {
                        count++;
                    }
                }
            }
        }
    
        return count == 1; // True if the window is on only one room's border
    }
    
    
    

    public void savePlan() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Floor Plan");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".dat") || f.isDirectory();
            }
            public String getDescription() {
                return "Floor Plan Files (*.dat)";
            }
        });
        int result = fileChooser.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        if (!selectedFile.getName().toLowerCase().endsWith(".dat")) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ".dat");
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
            oos.writeObject(rooms);
            oos.writeObject(doors);
            oos.writeObject(windows);
            oos.writeObject(furnitureList);
            JOptionPane.showMessageDialog(this, "Plan saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save plan: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

public void loadPlan() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Load Floor Plan");
    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".dat") || f.isDirectory();
        }
        public String getDescription() {
            return "Floor Plan Files (*.dat)";
        }
    });
    
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()))) {
            rooms = (ArrayList<Room>) ois.readObject();
            doors = (ArrayList<Door>) ois.readObject();
            windows = (ArrayList<Window>) ois.readObject();
            furnitureList = (ArrayList<Furniture>) ois.readObject();
            repaint();
            JOptionPane.showMessageDialog(this, "Plan loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load plan: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    //@Override
    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawGrid(g);
    // Draw all rooms, adjusting borders for windows
    for (Room room : rooms) {
        room.draw(g);
    }

    // Draw all windows
    for (Window window : windows) {
        window.draw(g);
    }

    // Draw all doors
    for (Door door : doors) {
        door.draw(g);
    }

    // Draw all furniture
    for (Furniture furniture : furnitureList) {
        furniture.draw(g);
    }
}

    


}