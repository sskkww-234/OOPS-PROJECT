import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

class Room implements Serializable {
    int x, y, width, height;
    private int originalX, originalY;
    private final Color color;
    private ArrayList<Door> doors = new ArrayList<>();
    private ArrayList<Window> windows = new ArrayList<>();

    public Room(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    // Add a door to the room
    public void addDoor(Door door) {
        doors.add(door);
    }

    // Add a window to the room
    public void addWindow(Window window) {
        windows.add(window);
    }

    // Draw the room and its associated doors and windows
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
    
        // Draw the room
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);
    
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, width, height);
    
        // Draw the doors
        for (Door door : doors) {
            door.draw(g);
        }
    
        // Draw the windows
        for (Window window : windows) {
            window.draw(g);
        }
    }
    

    // Move the room
    public void move(int dx, int dy) {
        x += dx;
        y += dy;

        // Move doors and windows with the room
        for (Door door : doors) {
            door.move(dx, dy);
        }

        for (Window window : windows) {
            window.move(dx, dy);
        }
    }

    public void saveOriginalPosition() {
        originalX = x;
        originalY = y;
    }

    public void revertPosition() {
        x = originalX;
        y = originalY;
    }

    public boolean contains(Point p) {
        return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
    }

    public boolean intersects(Room other) {
        return x < other.x + other.width && x + width > other.x &&
               y < other.y + other.height && y + height > other.y;
    }

    public Color getColor() {
        return color;
    }
}

