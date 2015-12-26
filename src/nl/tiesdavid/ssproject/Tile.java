/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

public class Tile {
    public enum Color {
        BLUE,
        GREEN,
        ORANGE,
        PURPLE,
        RED,
        YELLOW;

        @Override
        public String toString() {
            switch (this)  {
                case BLUE:
                    return "B";
                case GREEN:
                    return "G";
                case ORANGE:
                    return "O";
                case PURPLE:
                    return "P";
                case RED:
                    return "R";
                case YELLOW:
                    return "Y";
                default:
                    return ".";
            }
        }
    }

    public enum Shape {
        CIRCLE,
        DIAMOND,
        PLUS,
        STAR,
        SQUARE,
        X;

        @Override
        public String toString() {
            switch (this) {
                case CIRCLE:
                    return "O";
                case DIAMOND:
                    return "#";
                case PLUS:
                    return "+";
                case STAR:
                    return "*";
                case SQUARE:
                    return "@";
                case X:
                    return "X";
                default:
                    return ".";
            }
        }
    }

    private int x, y;
    private boolean checked;
    private final Color color;
    private final Shape shape;

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

    public void setChecked() {
        this.checked = true;
    }

    public String toLongString() {
        return color.toString() + shape.toString() + " @ " + getX() + ", " + getY();
    }

    @Override
    public String toString() {
        return color.toString() + shape.toString();
    }
}
