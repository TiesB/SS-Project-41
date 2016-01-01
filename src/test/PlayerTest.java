package test; /**
 * Created by Ties on 26-12-2015.
 * @author Ties
 */

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.MoveException;
import nl.tiesdavid.ssproject.game.exceptions.NonMatchingAttributesException;
import nl.tiesdavid.ssproject.game.exceptions.NotEnoughTilesGivenException;
import nl.tiesdavid.ssproject.game.exceptions.NotTouchingException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PlayerTest {
    private TestPlayer player;
    
    @Before
    public void setUp() {
        player = new TestPlayer(new Game());
    }
    
    @Test
    public void noOfTilesSharingACharacteristicTest() {
        player.addTileToDeck(new Tile(Tile.Color.BLUE, Tile.Shape.CIRCLE));
        player.addTileToDeck(new Tile(Tile.Color.BLUE, Tile.Shape.CLOVER));
        player.addTileToDeck(new Tile(Tile.Color.RED, Tile.Shape.CIRCLE));
        player.addTileToDeck(new Tile(Tile.Color.GREEN, Tile.Shape.DIAMOND));
        player.addTileToDeck(new Tile(Tile.Color.ORANGE, Tile.Shape.CLOVER));

        assertEquals(4, player.getNoOfTilesSharingACharacteristic());
    }

    @Test
    public void correctTileSetTest() {
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(1, 1, Tile.Color.BLUE, Tile.Shape.STARBURST));
        tiles.add(new Tile(2, 1, Tile.Color.BLUE, Tile.Shape.STARBURST));
        tiles.add(new Tile(3, 1, Tile.Color.BLUE, Tile.Shape.STARBURST));

        try {
            assertTrue(player.checkCorrectTileSet(tiles));
        } catch (MoveException e) {
            fail(e.getMessage());
        }

        tiles = new ArrayList<>();
        tiles.add(new Tile(1, 1, Tile.Color.BLUE, Tile.Shape.STARBURST));
        tiles.add(new Tile(1, 2, Tile.Color.BLUE, Tile.Shape.STARBURST));
        tiles.add(new Tile(1, 3, Tile.Color.BLUE, Tile.Shape.STARBURST));

        try {
            assertTrue(player.checkCorrectTileSet(tiles));
        } catch (MoveException e) {
            fail(e.getMessage());
        }
    }

    @Test (expected = NotTouchingException.class)
    public void notOnSameLineTest() throws NotTouchingException, NonMatchingAttributesException, NotEnoughTilesGivenException {
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(1, 2, Tile.Color.BLUE, Tile.Shape.STARBURST));
        tiles.add(new Tile(1, 1, Tile.Color.BLUE, Tile.Shape.STARBURST));
        tiles.add(new Tile(1, 5, Tile.Color.BLUE, Tile.Shape.STARBURST));

        try {
            assertFalse(player.checkCorrectTileSet(tiles));
        } catch (NotTouchingException e) {
            throw new NotTouchingException();
        } catch (NonMatchingAttributesException e) {
            throw new NonMatchingAttributesException();
        } catch (NotEnoughTilesGivenException e) {
            throw new NotEnoughTilesGivenException();
        }
    }

    @Test (expected = NotTouchingException.class)
    public void notInSameColumnTest() throws NotTouchingException, NonMatchingAttributesException, NotEnoughTilesGivenException {
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(2, 1, Tile.Color.BLUE, Tile.Shape.STARBURST));
        tiles.add(new Tile(1, 1, Tile.Color.BLUE, Tile.Shape.STARBURST));
        tiles.add(new Tile(5, 1, Tile.Color.BLUE, Tile.Shape.STARBURST));

        try {
            assertFalse(player.checkCorrectTileSet(tiles));
        } catch (NotTouchingException e) {
            throw new NotTouchingException();
        } catch (NonMatchingAttributesException e) {
            throw new NonMatchingAttributesException();
        } catch (NotEnoughTilesGivenException e) {
            throw new NotEnoughTilesGivenException();
        }
    }
}
