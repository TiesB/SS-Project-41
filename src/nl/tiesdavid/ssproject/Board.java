/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

import java.util.ArrayList;

public class Board {

    private ArrayList<Tile> tiles;

    public Board() {
        tiles = new ArrayList<Tile>();
    }

    public void addTile(Tile mTile) {
        tiles.add(mTile);
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

    /**
     * Gives the tile at the given coordinate.
     * @param x The X coordinate of the requested tile.
     * @param y The Y coordinate of the requested tile.
     * @return The tile at the given coordinate, or null when it's non-existent.
     */
    public Tile getTile(int x, int y) {
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
        //TODO: Check if this works.
        int score = 0;
        int x = tile.getX();
        int y = tile.getY();
        Tile nextTile = tile;

        int tempX, tempY, lengthOfLine;
        boolean setToColor, setToShape;

        //Check to the left
        tempX = x;
        lengthOfLine = 1;
        setToColor = true;
        setToShape = true;
        while ((nextTile = getTile(tempX - 1, y)) != null && lengthOfLine < 6) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                setToShape = false;
                score++;
                lengthOfLine++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                setToColor = false;
                score++;
                lengthOfLine++;
            }
            tempX -= 1;
        }

        //Check to the right
        tempX = x;
        lengthOfLine = 1;
        setToColor = true;
        setToShape = true;
        while ((nextTile = getTile(tempX + 1, y)) != null && lengthOfLine < 6) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                setToShape = false;
                score++;
                lengthOfLine++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                setToColor = false;
                score++;
                lengthOfLine++;
            }
            tempX += 1;
        }

        //Check upwards
        tempY = y;
        lengthOfLine = 1;
        setToColor = true;
        setToShape = true;
        while ((nextTile = getTile(x, tempY + 1)) != null && lengthOfLine < 6) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                setToShape = false;
                score++;
                lengthOfLine++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                setToColor = false;
                score++;
                lengthOfLine++;
            }
            tempY += 1;
        }

        //Check downwards
        tempY = y;
        lengthOfLine = 1;
        setToColor = true;
        setToShape = true;
        while ((nextTile = getTile(x, tempY - 1)) != null && lengthOfLine < 6) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                setToShape = false;
                score++;
                lengthOfLine++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                setToColor = false;
                score++;
                lengthOfLine++;
            }
            tempY -= 1;
        }

        return score;
    }

    /**
     * Resets the board to a state where there are no tiles put down.
     */
    public void reset() {
        tiles.clear();
    }
}
