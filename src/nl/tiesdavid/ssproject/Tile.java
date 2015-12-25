/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.Color;
import nl.tiesdavid.ssproject.enums.Shape;

public class Tile {
    private int x, y;
    private boolean checked;
    private Color color;
    private Shape shape;

    public Tile(int x, int y, Color color, Shape shape) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.shape = shape;
    }

    public Tile(Color color, Shape shape) {
        this.color = color;
        this.shape = shape;
        this.checked = false;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean getChecked() {
        return this.checked;
    }

    public Color getColor() {
        return color;
    }

    public Shape getShape() {
        return shape;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public String toLongString() {
        return color.toString() + shape.toString() + " @ " + getX() + ", " + getY();
    }

    @Override
    public String toString() {
        return color.toString() + shape.toString();
    }
}
