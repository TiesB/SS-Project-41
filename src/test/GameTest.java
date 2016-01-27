package test; /**
 * Created by Ties Bolding on 30-12-2015.
 * @author tiesb
 */

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameTest {
    private TestGame game;

    @Before
    public void setUp() {
        game = new TestGame();
    }
    
    @Test
    public void initTest() {
        assertEquals("", game.toString());
    }

    @Test
    public void addPlayerTest() {
        game.addPlayer(new TestPlayer(game));
        assertEquals("Test: 0" + System.lineSeparator(), game.toString());
    }

    @Test
    public void addPlayersTest() {
        game.addPlayer(new TestPlayer(game));
        assertEquals("Test: 0" + System.lineSeparator(), game.toString());

        game.addPlayer(new TestPlayer(game));
        String string = "Test: 0" +
                System.lineSeparator() +
                "Test: 0" + System.lineSeparator();
        assertEquals(string, game.toString());
    }

    @Test
    public void testGameOver() {
        assertTrue(game.gameOver());

        game.addPlayer(new TestPlayer(game));
        assertTrue(game.gameOver());
    }
}
