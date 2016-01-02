/**
 * Created by Ties on 21-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.online.clientside;

import nl.tiesdavid.ssproject.game.*;
import nl.tiesdavid.ssproject.game.enums.MoveType;
import nl.tiesdavid.ssproject.game.exceptions.NotInDeckException;

import java.util.ArrayList;
import java.util.Arrays;

public class HumanClientPlayer extends HumanPlayer {
    private boolean needsToMakeMove;

    HumanClientPlayer(String name) {
        super(name, new Game());
        deck.clear();
        needsToMakeMove = false;
    }

    public String getCommand() {
        if (needsToMakeMove) {
            return parseMove(determineMove());
        }
        return "";
    }

    public void parseResponse(String response) {
        if (response.startsWith("MAKE_MOVE")) {
            needsToMakeMove = true;
            return;
        } else if (response.startsWith("DECK")) {
            parseDeckString(response);
            return;
        }
        int res = -1;
        try {
            res = Integer.parseInt(response);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        switch (res) {
            case 0:
                System.out.println("Move succeeded!");
                break;
            default:
                System.out.println(":(" + Integer.toString(res));
                break;
        }
    }

    public void parseDeckString(String deckString) {
        String[] parts = deckString.split(" ");
        Deck newDeck = new Deck(DECK_SIZE);
        for (String string : Arrays.copyOfRange(parts, 1, parts.length)) {
            newDeck.add(parseTileString(new String[] {string}));
        }
        this.deck = newDeck;
    }

    private String parseMove (Move move) {
        if (move == null) {
            return "";
        } else {
            MoveType moveType = move.getMoveType();
            switch (moveType) {
                case ADD_TILE_AND_DRAW_NEW:
                    return Integer.toString(MoveType.ADD_TILE_AND_DRAW_NEW_CODE) + " "
                            + move.getTile().getColor().user + move.getTile().getShape().user
                            + " " + move.getTile().getX() + " " + move.getTile().getY();
                case ADD_MULTIPLE_TILES:
                    String string1 = Integer.toString(MoveType.ADD_MULTIPLE_TILES_CODE);
                    ArrayList<Tile> tiles1 = move.getTileList();
                    for (Tile tile : tiles1) {
                        string1 += " " + tile.getColor().user + tile.getShape().user;
                    }
                    return string1;
                case TRADE_TILES:
                    String string2 = Integer.toString(MoveType.TRADE_TILES_CODE);
                    ArrayList<Tile> tiles2 = move.getTileList();
                    for (Tile tile : tiles2) {
                        string2 += " " + tile.getColor().user + tile.getShape().user;
                    }
                    return string2;
                default:
                    return null;
            }
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