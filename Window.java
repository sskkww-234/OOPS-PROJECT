import java.awt.*;
import java.io.Serializable;

class Window implements Serializable {
    int x, y;
    boolean isHorizontal;

    public Window(int x, int y, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.isHorizontal = isHorizontal;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0));

        if (isHorizontal) {
            g2d.drawLine(x - 30, y, x + 30, y); // Horizontal window
        } else {
            g2d.drawLine(x, y - 30, x, y + 30); // Vertical window
        }
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
}
