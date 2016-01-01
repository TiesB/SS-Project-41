package test;
/**
 * Created by Ties on 20-12-2015.
 * @author Ties
 */

import nl.tiesdavid.ssproject.Board;
import nl.tiesdavid.ssproject.Tile;
import nl.tiesdavid.ssproject.exceptions.CoordinatesAlreadyFilledException;
import nl.tiesdavid.ssproject.exceptions.MoveException;
import nl.tiesdavid.ssproject.exceptions.NoNeighboringTileException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BoardTest {
    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void creationTest() {
        assertEquals("", board.toString());
    }

    @Test
    public void resetTest() {
        assertEquals("", board.toString());
        try {
            board.placeTile(new Tile(0, 0, Tile.Color.RED, Tile.Shape.CLOVER));
        } catch (MoveException e) {
            fail(e.getMessage());
        }
        board.reset();
        assertEquals("", board.toString());
    }

    @Test
    public void addTileTest() {
        Tile tile = new Tile(0, 0, Tile.Color.RED, Tile.Shape.CLOVER);
        try {
            board.placeTile(tile);
        } catch (MoveException e) {
            fail(e.getMessage());
        }

        assertEquals(Tile.Color.RED.toString() + Tile.Shape.CLOVER.toString(), board.toString());
    }

    @Test(expected = CoordinatesAlreadyFilledException.class)
    public void addAlreadyExistentTile() throws MoveException {
        try {
            board.placeTile(new Tile(0, 0, Tile.Color.RED, Tile.Shape.CLOVER));
            board.placeTile(new Tile(0, 0, Tile.Color.GREEN, Tile.Shape.STARBURST));
        } catch (CoordinatesAlreadyFilledException e) {
            throw new CoordinatesAlreadyFilledException();
        }
    }

    @Test(expected = NoNeighboringTileException.class)
    public void addUnsocialTile() throws MoveException {
        try {
            board.placeTile(new Tile(0, 0, Tile.Color.RED, Tile.Shape.CLOVER));
            board.placeTile(new Tile(0, 1, Tile.Color.RED, Tile.Shape.DIAMOND));
            board.placeTile(new Tile(1, 0, Tile.Color.GREEN, Tile.Shape.STARBURST));
        } catch (NoNeighboringTileException e) {
            throw new NoNeighboringTileException();
        }
    }

    @Test
    public void scoreTest() {
        try {
            assertEquals(0, board.placeTile(new Tile(0, 0, Tile.Color.RED, Tile.Shape.STARBURST)));
        } catch (MoveException e) {
            fail(e.getMessage());
        }
        try {
            assertEquals(2, board.placeTile(new Tile(0, 1, Tile.Color.BLUE, Tile.Shape.STARBURST)));
        } catch (MoveException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals(2, board.placeTile(new Tile(-1, 0, Tile.Color.RED, Tile.Shape.DIAMOND)));
        } catch (MoveException e) {
            fail(e.getMessage());
        }
        try {
            assertEquals(4, board.placeTile(new Tile(-1, 1, Tile.Color.BLUE, Tile.Shape.DIAMOND)));
        } catch (MoveException e) {
            fail(e.getMessage());
        }
    }
}
