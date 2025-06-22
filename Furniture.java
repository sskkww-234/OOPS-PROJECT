import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;
import javax.swing.*;

class Furniture implements Serializable {
    private static final long serialVersionUID = 1L;
    int x, y, width, height, rotation;
    transient Image image; // Mark image as transient
    private String imagePath; // Store the path to reload the image

    public Furniture(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.rotation = 0;
    }

    // Custom serialization method
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Write the default serializable fields
        // Save the current image path
        if (imagePath != null) {
            out.writeObject(imagePath);
        } else {
            out.writeObject("");
        }
    }

    // Custom deserialization method
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Read the default serializable fields
        // Read the image path
        String path = (String) in.readObject();
        if (!path.isEmpty()) {
            try {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    image = ImageIO.read(imageFile);
                } else {
                    // Try loading from resources if file doesn't exist in exact path
                    image = new ImageIcon(path).getImage();
                }
            } catch (IOException e) {
                System.err.println("Failed to load image: " + path);
                e.printStackTrace();
            }
        }
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }

    public void rotate() {
        rotation = (rotation + 90) % 360;
    }

    public void draw(Graphics g) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(x + width / 2, y + height / 2);
            g2d.rotate(Math.toRadians(rotation));
            g2d.drawImage(image, -width / 2, -height / 2, width, height, null);
            g2d.rotate(-Math.toRadians(rotation));
            g2d.translate(-x - width / 2, -y - height / 2);
        }
    }
}