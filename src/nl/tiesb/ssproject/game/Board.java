/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
package nl.tiesb.ssproject.game;

import nl.tiesb.ssproject.game.exceptions.InvalidTilePlacementException;
import nl.tiesb.ssproject.game.exceptions.OutOfBoundsException;
import nl.tiesb.ssproject.game.exceptions.CoordinatesAlreadyFilledException;
import nl.tiesb.ssproject.game.exceptions.NoNeighboringTileException;

import java.util.ArrayList;

public class Board {

    protected ArrayList<Tile> tiles;
    private int minX, maxX, minY, maxY;

    public Board() {
        tiles = new ArrayList<>();
        minX = 1; maxX = 0; minY = 0; maxY = 0;
    }

    public synchronized int placeTiles(ArrayList<Tile> newTiles)
            throws InvalidTilePlacementException {
        int score = 0;

        ArrayList<Tile> tilesToBePlaced = new ArrayList<>();
        tilesToBePlaced.addAll(newTiles);

        int tries = 0;
        while (!tilesToBePlaced.isEmpty() && tries < 720) {
            Tile tile = tilesToBePlaced.get(0);
            try {
                int tileScore = placeTile(tile);
                score += tileScore;
            } catch (InvalidTilePlacementException e) {
                tilesToBePlaced.add(tilesToBePlaced.size(), tile);
            } finally {
                tilesToBePlaced.remove(0);
            }
            tries++;
        }

        if (!tilesToBePlaced.isEmpty()) {
            throw new InvalidTilePlacementException();
        }

        return score;
    }

    public synchronized int placeTile(Tile tile) throws InvalidTilePlacementException {
        int x = tile.getX();
        int y = tile.getY();

        if (tileExists(x, y)) {
            throw new CoordinatesAlreadyFilledException();
        }

        if (tile.getX() != 0 || tile.getY() != 0) {
            if (x < minX - 1 || x > maxX + 1 || y < minY - 1 || y > maxY + 1) {
                throw new OutOfBoundsException();
            }

            if (isEmpty()) {
                throw new OutOfBoundsException();
            }

            if (!checkNeighboringTiles(tile)) {
                throw new NoNeighboringTileException();
            }
        }

        tiles.add(tile);
        calculateMinMax(x, y);
        return getScore(tile);
    }

    public int tryPlace(ArrayList<Tile> checkTiles) throws InvalidTilePlacementException {
        Board board = this.deepCopy();
        ArrayList<Tile> tilesToBePlaced = new ArrayList<>();
        tilesToBePlaced.addAll(checkTiles);

        int score = 0;
        int tries = 0;
        while (!tilesToBePlaced.isEmpty() && tries < 720) {
            Tile tile = tilesToBePlaced.get(0);
            try {
                score += board.placeTile(tile);
            } catch (InvalidTilePlacementException e) {
                tilesToBePlaced.add(tilesToBePlaced.size(), tile);
            } finally {
                tilesToBePlaced.remove(0);
            }
            tries++;
        }

        if (!tilesToBePlaced.isEmpty()) {
            throw new InvalidTilePlacementException();
        }

        return score;
    }

    protected boolean checkNeighboringTiles(Tile tile) {
        int x = tile.getX();
        int y = tile.getY();
        Tile.Color color = tile.getColor();
        Tile.Shape shape = tile.getShape();

        int horizontalLine = 0;
        int verticalLine = 0;

        //To the left
        int tempX = x - 1;
        Tile nextTile = getTile(tempX, y);
        while (nextTile != null) {
            if (!checkTile(nextTile, color, shape)) {
                return false;
            }

            horizontalLine += 1;
            nextTile = getTile(--tempX, y);
        }

        //To the right
        tempX = x + 1;
        nextTile = getTile(tempX, y);
        while (nextTile != null) {
            if (!checkTile(nextTile, color, shape)) {
                return false;
            }

            horizontalLine += 1;
            nextTile = getTile(++tempX, y);
        }

        //Upwards
        int tempY = y - 1;
        nextTile = getTile(x, tempY);
        while (nextTile != null) {
            if (!checkTile(nextTile, color, shape)) {
                return false;
            }

            verticalLine += 1;
            nextTile = getTile(x, --tempY);
        }

        //Downwards
        tempY = y + 1;
        nextTile = getTile(tempX, y);
        while (nextTile != null) {
            if (!checkTile(nextTile, color, shape)) {
                return false;
            }

            verticalLine += 1;
            nextTile = getTile(x, ++tempY);
        }

        if (horizontalLine > 6 || verticalLine > 6) {
            return false;
        }

        int[][] values = {{x - 1, y}, {x + 1, y}, {x, y - 1}, {x, y + 1}};

        for (int[] value : values) {
            Tile testTile = getTile(value[0], value[1]);
            if (testTile != null && !checkTile(testTile, color, shape)) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkTile(Tile tile, Tile.Color color, Tile.Shape shape) {
        return tile.getColor().equals(color) || tile.getShape().equals(shape);
    }

    /**
     * Gives whether a tile exists at a given coordinate.
     * @param x The X coordinate of the requested tile.
     * @param y The Y coordinate of the requested tile.
     * @return Whether a tile exists at a given coordinate.
     */
    private boolean tileExists(int x, int y) {
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
    protected Tile getTile(int x, int y) {
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
        //TODO: Maybe integrate this and place tile. Lot of redundant code now.
        int score = 0;
        int x = tile.getX();
        int y = tile.getY();

        int tempX, tempY, horizontalLine = 0, verticalLine = 0;
        boolean setToColor, setToShape;

        //TODO: Find a nicer way to do this. Maybe just like I did in neighboring stuff.

        //Check to the left
        tempX = x;
        setToColor = true;
        setToShape = true;
        Tile nextTile = getTile(tempX - 1, y);
        while (nextTile != null) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                horizontalLine++;
                setToShape = false;
                score++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                horizontalLine++;
                setToColor = false;
                score++;
            }
            tempX -= 1;
            nextTile = getTile(tempX - 1, y);
        }

        //Check to the right
        tempX = x;
        setToColor = true;
        setToShape = true;
        nextTile = getTile(tempX + 1, y);
        while (nextTile != null) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                horizontalLine++;
                setToShape = false;
                score++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                horizontalLine++;
                setToColor = false;
                score++;
            }
            tempX += 1;
            nextTile = getTile(tempX + 1, y);
        }

        //Check upwards
        tempY = y;
        setToColor = true;
        setToShape = true;
        nextTile = getTile(x, tempY - 1);
        while (nextTile != null) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                verticalLine++;
                setToShape = false;
                score++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                verticalLine++;
                setToColor = false;
                score++;
            }
            tempY -= 1;
            nextTile = getTile(x, tempY - 1);
        }

        //Check downwards
        tempY = y;
        setToColor = true;
        setToShape = true;
        nextTile = getTile(x, tempY + 1);
        while (nextTile != null) {
            if (tile.getColor().equals(nextTile.getColor()) && setToColor) {
                verticalLine++;
                setToShape = false;
                score++;
            } else if (tile.getShape().equals(nextTile.getShape()) && setToShape) {
                verticalLine++;
                setToColor = false;
                score++;
            }
            tempY += 1;
            nextTile = getTile(x, tempY + 1);
        }

        if (horizontalLine > 0) {
            score++;
        }
        if (verticalLine > 0) {
            score++;
        }

        if (horizontalLine == 6) {
            score += 6;
        }
        if (verticalLine == 6) {
            score += 6;
        }

        return score;
    }

    protected void calculateMinMax(int x, int y) {
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

    public void forcePlace(Tile tile) {
        tiles.add(tile);
        calculateMinMax(tile.getX(), tile.getY());
    }

    public Board deepCopy() {
        Board board = new Board();
        tiles.forEach(board::forcePlace);
        return board;
    }

    /**
     * Resets the board to a state where there are no tiles put down.
     */
    public void reset() {
        tiles.clear();
    }

    public void printBoard() {
        if (tiles.size() == 0) {
            System.out.println("The board is empty.");
        } else {
            for (int i = minY - 2; i <= maxY + 2; i++) {
                String string = "";
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
            }
        }
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    @Override
    public String toString() {
        String string = "";

        for (int i = 0; i < tiles.size() - 1; i++) {
            string += tiles.get(i).toLongString() + " | ";
        }
        if (tiles.size() > 0) {
            string += tiles.get(tiles.size() - 1).toLongString();
        }

        return string;
    }
}
