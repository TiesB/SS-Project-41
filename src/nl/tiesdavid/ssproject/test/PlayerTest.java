package nl.tiesdavid.ssproject.test; /**
 * Created by Ties on 26-12-2015.
 */

import nl.tiesdavid.ssproject.Tile;
import org.junit.Before;
import org.junit.Test;



public class PlayerTest {
    private TestPlayer player;
    
    @Before
    public void setUp() {
        player = new TestPlayer();
    }
    
    @Test
    public void test() {
        player.addTileToDeck(new Tile(Tile.Color.BLUE, Tile.Shape.CIRCLE));
        player.addTileToDeck(new Tile(Tile.Color.BLUE, Tile.Shape.PLUS));
        player.addTileToDeck(new Tile(Tile.Color.RED, Tile.Shape.CIRCLE));
        player.addTileToDeck(new Tile(Tile.Color.GREEN, Tile.Shape.DIAMOND));
        player.addTileToDeck(new Tile(Tile.Color.ORANGE, Tile.Shape.PLUS));

        org.junit.Assert.assertEquals(4, player.getNoOfTilesSharingACharacteristic());
    }
    
}
