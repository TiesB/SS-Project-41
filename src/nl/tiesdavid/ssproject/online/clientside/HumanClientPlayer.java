/**
 * Created by Ties on 21-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.online.clientside;

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.HumanPlayer;
import nl.tiesdavid.ssproject.game.Move;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.enums.MoveType;
import nl.tiesdavid.ssproject.game.exceptions.NotInDeckException;

import java.util.ArrayList;

public class HumanClientPlayer extends HumanPlayer {
    HumanClientPlayer(String name) {
        super(name, new Game());
    }

    public String getCommand() {
        return parseMove(determineMove());
    }

    public void parseResponse(String response) {
        System.out.println(response);
    }

    private String parseMove (Move move) {
        MoveType moveType = move.getMoveType();
        switch (moveType) {
            case ADD_TILE_AND_DRAW_NEW:
                return "1 " + move.getTile().getColor().user + move.getTile().getShape().user;
            case ADD_MULTIPLE_TILES:
                String string1 = "2";
                ArrayList<Tile> tiles1 = move.getTileList();
                for (Tile tile : tiles1) {
                    string1 += " " + tile.getColor().user + tile.getShape().user;
                }
                return string1;
            case TRADE_TILES:
                String string2 = "3";
                ArrayList<Tile> tiles2 = move.getTileList();
                for (Tile tile : tiles2) {
                    string2 += " " + tile.getColor().user + tile.getShape().user;
                }
                return string2;
            default:
                return null;
        }
    }

    @Override
    protected Tile getTileFromUser(String string, boolean readXY) {
        Tile tile = readTile(string);
        try {
            tile = findTile(tile);

            if (readXY) {
                int x, y;
                x = readInt("Choose a X coordinate (0 if board is empty): ");
                y = readInt("Choose a Y coordinate (0 if board is empty): ");

                tile.setX(x);
                tile.setY(y);
            }

            return tile;
        } catch (NotInDeckException e) {
            handleMoveException(e);
            return null;
        }
    }
}