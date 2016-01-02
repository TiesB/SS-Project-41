/**
 * Created by Ties on 1-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.HumanPlayer;
import nl.tiesdavid.ssproject.game.Move;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.enums.MoveType;
import nl.tiesdavid.ssproject.game.exceptions.*;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

public class OnlinePlayer extends HumanPlayer {
    public static final int UNUSABLE_COMMAND = -1;

    private ClientHandler handler;

    OnlinePlayer(String name, Game game, ClientHandler handler) {
        super(name, game);
        this.handler = handler;
    }

    @Override
    public void makeMove(Move move) throws NotInDeckException, InvalidTilePlacementException, NotEnoughTilesGivenException, NonMatchingAttributesException, NotTouchingException {
        setMoveFinished(false);
        System.out.println("Move needs to be made by: " + getName());
        handler.startMove();
    }

    private void doMove(Move move) {
        try {
            super.makeMove(move);
        } catch (MoveException e) {
            e.printStackTrace();
        }
    }

    public void sendDeck(Writer out) {
        try {
            out.write("DECK " + deck.toUserString() + System.lineSeparator());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int handleAddTileDrawNew(String[] tileString) {
        for (String s : tileString) {
            System.out.println(s);
        }
        Tile tile = parseTileString(tileString);
        doMove(new Move(MoveType.ADD_TILE_AND_DRAW_NEW, tile));
        return 0;
    }

    private int handleMultiple(MoveType moveType, String[] strings) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < strings.length; i = i + 4) {
            tiles.add(parseTileString(Arrays.copyOfRange(strings, i, i + 4)));
        }
        try {
            doMove(new Move(moveType, tiles));
        } catch (MoveException e) {
            handleMoveException(e);
            return e.getCode();
        }
        return 0;
    }

    public int handleCommand(String msg) {
        String[] parts = msg.split(" ");
        int type = -1;
        try {
            type = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return UNUSABLE_COMMAND;
        }
        switch (type) {
            case MoveType.ADD_TILE_AND_DRAW_NEW_CODE:
                return handleAddTileDrawNew(Arrays.copyOfRange(parts, 1, parts.length));
            case MoveType.ADD_MULTIPLE_TILES_CODE:
                return handleMultiple(MoveType.ADD_MULTIPLE_TILES, Arrays.copyOfRange(parts, 1, parts.length));
            case MoveType.TRADE_TILES_CODE:
                return handleMultiple(MoveType.TRADE_TILES, Arrays.copyOfRange(parts, 1, parts.length));
            default:
                return UNUSABLE_COMMAND;
        }
    }

    @Override
    protected Move determineMove() {
        //An exception has been thrown. TODO: Communicate with user.
        return null;
    }
}
