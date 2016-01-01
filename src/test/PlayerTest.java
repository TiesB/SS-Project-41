package test; /**
 * Created by Ties on 26-12-2015.
 * @author Ties
 */

import nl.tiesdavid.ssproject.Game;
import nl.tiesdavid.ssproject.Tile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayerTest {
    private TestPlayer player;
    
    @Before
    public void setUp() {
        player = new TestPlayer(new Game());
    }
    
    @Test
    public void test() {
        player.addTileToDeck(new Tile(Tile.Color.BLUE, Tile.Shape.CIRCLE));
        player.addTileToDeck(new Tile(Tile.Color.BLUE, Tile.Shape.CLOVER));
        player.addTileToDeck(new Tile(Tile.Color.RED, Tile.Shape.CIRCLE));
        player.addTileToDeck(new Tile(Tile.Color.GREEN, Tile.Shape.DIAMOND));
        player.addTileToDeck(new Tile(Tile.Color.ORANGE, Tile.Shape.CLOVER));

        assertEquals(4, player.getNoOfTilesSharingACharacteristic());
    }
    
}
