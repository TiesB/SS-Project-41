/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
package nl.tiesb.ssproject.game;

import nl.tiesb.ssproject.game.exceptions.UnparsableDataException;

import java.util.Comparator;

public class Tile {
    public static Comparator<Tile> tileComparator = (o1, o2) -> {
        int x1 = Integer.compare(o1.getX(), o2.getX());
        if (x1 == 0) {
            return Integer.compare(o1.getY(), o2.getY()) * -1;
        } else {
            return x1 * -1;
        }
    };

    public enum Color {
        BLUE(4, 0x0000FF),
        GREEN(3, 0x00FF00),
        ORANGE(1, 0xFFA500),
        PURPLE(5, 0x551A8B),
        RED(0, 0xFF0000),
        YELLOW(2, 0xFFFF00);

        public final int user;
        public final int hex;

        Color(int user, int hex) {
            this.user = user;
            this.hex = hex;
        }

        @Override
        public String toString() {
            return "" + this.user;
        }
    }

    public enum Shape {
        CIRCLE(0, '\u25cb'),
        DIAMOND(2, '\u25c7'),
        CLOVER(5, '\u2618'),
        CRISSCROSS(1, '\u2716'),
        STARBURST(4, '\u273A'),
        SQUARE(3, '\u25A0');

        public final int user;
        public final char printable;

        Shape(int user, char printable) {
            this.user = user;
            this.printable = printable;
        }

        @Override
        public String toString() {
            return "" + this.printable;
        }
    }

    private int x, y;
    private boolean hasXY;
    private boolean checked;
    private final Color color;
    private final Shape shape;

    public Tile(int x, int y, Color color, Shape shape) {
        this.x = x;
        this.y = y;
        this.hasXY = true;
        this.color = color;
        this.shape = shape;
    }

    public Tile(Color color, Shape shape) {
        this.color = color;
        this.shape = shape;
        this.hasXY = false;
        this.checked = false;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean hasXY() {
        return this.hasXY;
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
        this.hasXY = true;
    }

    public void setY(int y) {
        this.y = y;
        this.hasXY = true;
    }

    public void setChecked() {
        this.checked = true;
    }

    public String toShortProtocolForm() {
        return Integer.toString(this.getShape().user)
                + "," + Integer.toString(this.getColor().user);
    }

    public String toProtocolForm() {
        String string = toShortProtocolForm();
        if (this.hasXY()) {
            string += " " + Integer.toString(getX()) + "," + Integer.toString(getY());
        }
        return string;
    }

    public static Tile fromProtocolString(String tileString) throws UnparsableDataException {
        Color color = null;
        Shape shape = null;

        String[] tileStringChars = tileString.split(",");

        int shapeInt = Integer.parseInt(tileStringChars[0]);
        int colorInt = Integer.parseInt(tileStringChars[1]);

        for (Color color1 : Color.values()) {
            if (color1.user == colorInt) {
                color = color1;
            }
        }

        for (Shape shape1 : Shape.values()) {
            if (shape1.user == shapeInt) {
                shape = shape1;
            }
        }

        if (color == null || shape == null) {
            throw new UnparsableDataException(tileString);
        }

        return new Tile(color, shape);
    }

    public static Tile fromProtocolString(String tileString, String locationString)
            throws UnparsableDataException {
        Tile tile = fromProtocolString(tileString);

        String[] locationStringChars = locationString.split(",");

        int x = Integer.parseInt(locationStringChars[0]);
        int y = Integer.parseInt(locationStringChars[1]);

        tile.setX(x);
        tile.setY(y);

        return tile;
    }

    public Tile deepCopy() {
        if (hasXY()) {
            return new Tile(getX(), getY(), getColor(), getShape());
        } else {
            return new Tile(getColor(), getShape());
        }
    }

    /**
     * Returns a long string describing the tile.
     * @return a long string describing the tile, include coordinates.
     */
    public String toLongString() {
        return color.toString() + shape.toString() + " @ " + getX() + ", " + getY();
    }

    public String toUserString() {
        return "" + color.user + shape.user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile tile = (Tile) obj;
            return getColor().equals(tile.getColor()) && getShape().equals(tile.getShape());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return color.toString() + shape.toString();
    }
}
