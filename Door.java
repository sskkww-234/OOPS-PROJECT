import java.awt.*;
import java.io.Serializable;

class Door implements Serializable {
    int x, y;
    boolean isHorizontal;

    public Door(int x, int y, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.isHorizontal = isHorizontal;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        if (isHorizontal) {
            g.fillRect(x - 20, y - 2, 40, 4); // Door width: 40 pixels, height: 4 pixels
        } else {
            g.fillRect(x - 2, y - 20, 4, 40); // Door width: 4 pixels, height: 40 pixels
        }
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
}
