package nl.tiesdavid.ssproject.test;
/**
 * Created by Ties on 20-12-2015.
 */

import nl.tiesdavid.ssproject.Board;
import nl.tiesdavid.ssproject.Tile;
import nl.tiesdavid.ssproject.enums.Color;
import nl.tiesdavid.ssproject.enums.Shape;
import nl.tiesdavid.ssproject.exceptions.CoordinatesAlreadyFilledException;
import nl.tiesdavid.ssproject.exceptions.MoveException;
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
            board.placeTile(new Tile(0, 0, Color.RED, Shape.PLUS));
        } catch (MoveException e) {
            fail(e.getMessage());
        }
        board.reset();
        assertEquals("", board.toString());
    }

    @Test
    public void addTileTest() {
        Tile tile = new Tile(0, 0, Color.RED, Shape.PLUS);
        try {
            board.placeTile(tile);
        } catch (MoveException e) {
            fail(e.getMessage());
        }

        assertEquals("R+ @ 0, 0 | ", board.toString());
    }

    @Test(expected = CoordinatesAlreadyFilledException.class)
    public void addAlreadyExistentTile() throws MoveException {
        try {
            board.placeTile(new Tile(0, 0, Color.RED, Shape.PLUS));
            board.placeTile(new Tile(0, 0, Color.GREEN, Shape.STAR));
        } catch (CoordinatesAlreadyFilledException e) {
            throw new CoordinatesAlreadyFilledException();
        }
    }

    @Test
    public void scoreTest() {
        try {
            assertEquals(0, board.placeTile(new Tile(0, 0, Color.RED, Shape.STAR)));
            assertEquals(2, board.placeTile(new Tile(0, 1, Color.BLUE, Shape.STAR)));

            assertEquals(2, board.placeTile(new Tile(-1, 0, Color.RED, Shape.DIAMOND)));
            assertEquals(4, board.placeTile(new Tile(-1, 1, Color.BLUE, Shape.DIAMOND)));


        } catch (MoveException e) {
            fail(e.getMessage());
        }
    }
}
