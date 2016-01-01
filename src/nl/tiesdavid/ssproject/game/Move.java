/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game;

import nl.tiesdavid.ssproject.game.enums.MoveType;
import nl.tiesdavid.ssproject.game.exceptions.InvalidMoveTypeWithArgumentsException;

import java.util.ArrayList;

public class Move {
    private final MoveType moveType;

    private Tile tile;
    private ArrayList<Tile> tileList;

    public Move(MoveType moveType, Tile mTile) {
        this.moveType = moveType;
        this.tile = mTile;
    }

    public Move(MoveType moveType, ArrayList<Tile> tiles)
            throws InvalidMoveTypeWithArgumentsException {
        if (tiles.size() < 2) {
            throw new InvalidMoveTypeWithArgumentsException();
        }
        this.moveType = moveType;
        this.tileList = tiles;
    }

    public MoveType getMoveType() {
        return this.moveType;
    }

    public Tile getTile() {
        return this.tile;
    }

    public ArrayList<Tile> getTileList() {
        return this.tileList;
    }
}
