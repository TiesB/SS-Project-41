package test;
/**
 * Created by Ties on 20-12-2015.
 * @author Ties
 */

import nl.tiesdavid.ssproject.game.Board;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.CoordinatesAlreadyFilledException;
import nl.tiesdavid.ssproject.game.exceptions.InvalidTilePlacementException;
import nl.tiesdavid.ssproject.game.exceptions.MoveException;
import nl.tiesdavid.ssproject.game.exceptions.NoNeighboringTileException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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

        ArrayList<Tile> list = new ArrayList<>();
        list.add(tile);

        assertEquals(list, board.getTiles());
    }

    @Test
    public void realLifeExampleTest() {
        board.forcePlace(new Tile(2, 0, Tile.Color.ORANGE, Tile.Shape.DIAMOND));
        board.forcePlace(new Tile(1, 0, Tile.Color.ORANGE, Tile.Shape.CIRCLE));
        board.forcePlace(new Tile(0, 0, Tile.Color.ORANGE, Tile.Shape.CRISSCROSS));
        board.forcePlace(new Tile(2, 1, Tile.Color.BLUE, Tile.Shape.DIAMOND));
        board.forcePlace(new Tile(0, 1, Tile.Color.GREEN, Tile.Shape.CRISSCROSS));

        try {
            System.out.println("1");
            board.placeTile(new Tile(1, -1, Tile.Color.GREEN, Tile.Shape.CIRCLE));
        } catch (InvalidTilePlacementException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("2");
            board.placeTile(new Tile(1, -2, Tile.Color.YELLOW, Tile.Shape.CIRCLE));
        } catch (InvalidTilePlacementException e) {
            e.printStackTrace();
        }

        System.out.println(board);
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
