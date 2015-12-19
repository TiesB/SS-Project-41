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

    public boolean tileExists(int x, int y) {
        return getTile(x, y) != null;
    }

    public Tile getTile(int x, int y) {
        for (Tile tile : tiles) {
            if (tile.getX() == x && tile.getY() == y) {
                return tile;
            }
        }
        return null;
    }

    public int getScore(Tile tile) {
        //TODO: Check if this works.
        int score = 0;
        int x = tile.getX();
        int y = tile.getY();
        Tile nextTile = tile;

        int tempX, tempY, lengthOfLine;

        //Check to the left
        tempX = x;
        lengthOfLine = 1;
        while ((nextTile = getTile(tempX - 1, y)) != null) {
            if ((tile.getColor().equals(nextTile.getColor())
                    || tile.getShape().equals(nextTile.getShape()))
                    && lengthOfLine < 6) {
                score++;
                lengthOfLine++;
                tempX -= 1;
            }
        }

        //Check to the right
        tempX = x;
        lengthOfLine = 1;
        while ((nextTile = getTile(tempX + 1, y)) != null) {
            if ((tile.getColor().equals(nextTile.getColor())
                    || tile.getShape().equals(nextTile.getShape()))
                    && lengthOfLine < 6) {
                score++;
                lengthOfLine++;
                tempX += 1;
            }
        }

        //Check up
        tempY = y;
        lengthOfLine = 1;
        while ((nextTile = getTile(x, tempY + 1)) != null) {
            if ((tile.getColor().equals(nextTile.getColor())
                    || tile.getShape().equals(nextTile.getShape()))
                    && lengthOfLine < 6) {
                score++;
                lengthOfLine++;
                tempY += 1;
            }
        }

        //Check down
        tempY = y;
        lengthOfLine = 1;
        while ((nextTile = getTile(x, tempY - 1)) != null) {
            if ((tile.getColor().equals(nextTile.getColor())
                    || tile.getShape().equals(nextTile.getShape()))
                    && lengthOfLine < 6) {
                score++;
                lengthOfLine++;
                tempY -= 1;
            }
        }

        return score;
    }

    public void reset() {
        tiles.clear();
    }
}
