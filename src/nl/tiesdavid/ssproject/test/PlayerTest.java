package nl.tiesdavid.ssproject.test; /**
 * Created by Ties on 26-12-2015.
 */

import nl.tiesdavid.ssproject.Tile;
import nl.tiesdavid.ssproject.enums.Color;
import nl.tiesdavid.ssproject.enums.Shape;
import org.junit.Before;
import org.junit.Test;



public class PlayerTest {
    TestPlayer player;
    
    @Before
    public void setUp() {
        player = new TestPlayer();
    }
    
    @Test
    public void test() {
        player.addTileToDeck(new Tile(Color.BLUE, Shape.CIRCLE));
        player.addTileToDeck(new Tile(Color.BLUE, Shape.PLUS));
        player.addTileToDeck(new Tile(Color.RED, Shape.CIRCLE));
        player.addTileToDeck(new Tile(Color.GREEN, Shape.DIAMOND));
        player.addTileToDeck(new Tile(Color.ORANGE, Shape.PLUS));

        org.junit.Assert.assertEquals(4, player.getNoOfTilesSharingACharacteristic());
    }
    
}
