/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.Color;
import nl.tiesdavid.ssproject.enums.Shape;

public class Tile {
    private int x;
    private int y;
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
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Color getColor() {
        return color;
    }

    public Shape getShape() {
        return shape;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override
    public String toString() {
        return color.toString() + shape.toString();
    }
}
