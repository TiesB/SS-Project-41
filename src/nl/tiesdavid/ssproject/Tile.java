/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject;

public class Tile {
    public enum Color {
        BLUE('B', 0x0000FF),
        GREEN('G', 0x00FF00),
        ORANGE('O', 0xFFA500),
        PURPLE('P', 0x551A8B),
        RED('R', 0xFF0000),
        YELLOW('Y', 0xFFFF00);

        public final char user;
        public final int hex;

        Color(char user, int hex) {
            this.user = user;
            this.hex = hex;
        }

        @Override
        public String toString() {
            return "" + this.user;
        }
    }

    public enum Shape {
        CIRCLE('A', "\u25cb"),
        DIAMOND('B', "\u25c7"),
        CLOVER('C', "\uD83C\uDF40"),
        STARBURST('D', "\u273A"),
        SQUARE('E', "\u25A0"),
        CRISSCROSS('F', "\u2716");

        public final char user;
        public final String printable;

        Shape(char user, String printable) {
            this.user = user;
            this.printable = printable;
        }

        @Override
        public String toString() {
            return this.printable;
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

    /**
     * Returns a long string describing the tile.
     * @return a long string describing the tile, include coordinates.
     */
    public String toLongString() {
        return color.toString() + shape.toString() + " @ " + getX() + ", " + getY();
    }

    @Override
    public String toString() {
        return color.toString() + shape.toString();
    }
}
