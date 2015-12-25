/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.exceptions.CoordinatesAlreadyFilledException;
import nl.tiesdavid.ssproject.exceptions.MoveException;
import nl.tiesdavid.ssproject.exceptions.OutOfBoundsException;
import nl.tiesdavid.ssproject.exceptions.TooLongLineException;

import java.util.ArrayList;

public class Board {

    private ArrayList<Tile> tiles;
    private int minX, maxX, minY, maxY;

    public Board() {
        tiles = new ArrayList<>();
        minX = 1; maxX = 0; minY = 0; maxY = 0;
    }

    public int placeTile(Tile tile) throws MoveException {
        int x = tile.getX();
        int y = tile.getY();
        if (tileExists(x, y)) {
            throw new CoordinatesAlreadyFilledException();
        }

        //TODO: Implement method so that it checks whether there is a tile next to the tile the player wants to place. And of course think of the first tile.

        if (x < minX - 1 || x > maxX + 1 || y < minY - 1 || y > maxY + 1) {
            throw new OutOfBoundsException();
        }

        if (isEmpty() && (x != 0 || y != 0)) {
            throw new OutOfBoundsException();
        }

        int horizontalLine = 0;
        int verticalLine = 0;

        //To the left
        int tempX = x - 1;
        Tile nextTile = getTile(tempX, y);
        while (nextTile != null) {
            horizontalLine += 1;
            nextTile = getTile(--tempX, y);
        }

        //To the right
        tempX = x + 1;
        nextTile = getTile(tempX, y);
        while (nextTile != null) {
            horizontalLine += 1;
            nextTile = getTile(++tempX, y);
        }

        //Upwards
        int tempY = y - 1;
        nextTile = getTile(x, tempY);
        while (nextTile != null) {
            verticalLine += 1;
            nextTile = getTile(x, --tempY);
        }

        //Downwards
        tempY = y + 1;
        nextTile = getTile(tempX, y);
        while (nextTile != null) {
            verticalLine += 1;
            nextTile = getTile(x, ++tempY);
        }

        if (horizontalLine > 6 || verticalLine > 6) {
            throw new TooLongLineException();
        }

        tiles.add(tile);
        calculateMinMax(x, y);
        return getScore(tile);
    }

    /**
     * Gives whether a tile exists at a given coordinate.
     * @param x The X coordinate of the requested tile.
     * @param y The Y coordinate of the requested tile.
     * @return Whether a tile exists at a given coordinate.
     */
    public boolean tileExists(int x, int y) {
        return getTile(x, y) != null;
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    /**
     * Gives the tile at the given coordinate.
     * @param x The X coordinate of the requested tile.
     * @param y The Y coordinate of the requested tile.
     * @return The tile at the given coordinate, or null when it's non-existent.
     */
    private Tile getTile(int x, int y) {
        for (Tile tile : tiles) {
            if (tile.getX() == x && tile.getY() == y) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Gives the score a player gets for putting the given tile.
     * @param tile The tile the player would like to put down.
     * @return The score a player gets for putting the given tile.
     */
    public int getScore(Tile tile) {
        int score = 0;
        int x = tile.getX();
        int y = tile.getY();

        int tempX, tempY, lengthOfLine;
        boolean setToColor, setToShape;
        boolean horizontalLine = false, verticalLine = false;

        //Check to the left
        tempX = x;
        lengthOfLine = 1;
        setToColor = true;
        setToShape = true;
        Tile nextTile = getTile(tempX - 1, y);
        while (nextTile != null && lengthOfLine < 6) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                horizontalLine = true;
                setToShape = false;
                score++;
                lengthOfLine++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                horizontalLine = true;
                setToColor = false;
                score++;
                lengthOfLine++;
            }
            tempX -= 1;
            nextTile = getTile(tempX - 1, y);
        }

        //Check to the right
        tempX = x;
        lengthOfLine = 1;
        setToColor = true;
        setToShape = true;
        nextTile = getTile(tempX + 1, y);
        while (nextTile != null && lengthOfLine < 6) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                horizontalLine = true;
                setToShape = false;
                score++;
                lengthOfLine++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                horizontalLine = true;
                setToColor = false;
                score++;
                lengthOfLine++;
            }
            tempX += 1;
            nextTile = getTile(tempX + 1, y);
        }

        //Check upwards
        tempY = y;
        lengthOfLine = 1;
        setToColor = true;
        setToShape = true;
        nextTile = getTile(x, tempY - 1);
        while (nextTile != null && lengthOfLine < 6) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                verticalLine = true;
                setToShape = false;
                score++;
                lengthOfLine++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                verticalLine = true;
                setToColor = false;
                score++;
                lengthOfLine++;
            }
            tempY -= 1;
            nextTile = getTile(x, tempY - 1);
        }

        //Check downwards
        tempY = y;
        lengthOfLine = 1;
        setToColor = true;
        setToShape = true;
        nextTile = getTile(x, tempY + 1);
        while (nextTile != null && lengthOfLine < 6) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                verticalLine = true;
                setToShape = false;
                score++;
                lengthOfLine++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                verticalLine = true;
                setToColor = false;
                score++;
                lengthOfLine++;
            }
            tempY += 1;
            nextTile = getTile(x, tempY + 1);
        }

        if (horizontalLine) {
            score++;
        }
        if (verticalLine) {
            score++;
        }

        return score;
    }

    private void calculateMinMax(int x, int y) {
        if (x < minX) {
            minX = x;
        } else if (x > maxX) {
            maxX = x;
        }

        if (y < minY) {
            minY = y;
        } else if (y > maxY) {
            maxY = y;
        }
    }

    /**
     * Resets the board to a state where there are no tiles put down.
     */
    public void reset() {
        tiles.clear();
    }

    @Override
    public String toString() {
        String string = "";

        for (int i = minY - 2; i <= maxY + 2; i++) {
            if (i == minY - 2) {
                string += String.format("%5s", "");
            } else if (i <= maxY + 1) {
                string += String.format("%5s", Integer.toString(i)) + "|";
            } else {
                string += String.format("%5s", "y");
            }

            for (int j = minX - 2; j <= maxX + 1; j++) {
                if (i == minY - 2) {
                    string += String.format("%4s", Integer.toString(j));
                } else {
                    Tile tile = getTile(j, i);
                    if (tile == null) {
                        string += String.format("%4s", "");
                    } else {
                        string += String.format("%4s", tile);
                    }
                }
            }

            if (i == minY - 2) {
                string += String.format("%4s", "x");
            }

            System.out.println(string);
            string = "";
        }

        for (int i = 0; i < tiles.size() - 1; i++) {
            string += tiles.get(i) + " | ";
        }
        if (tiles.size() > 0) {
            string += tiles.get(tiles.size() - 1);
        }

        return string;
    }
}
