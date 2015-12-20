/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.MoveType;
import nl.tiesdavid.ssproject.exceptions.CoordinatesAlreadyFilledException;
import nl.tiesdavid.ssproject.exceptions.InvalidMoveTypeWithArgumentsException;
import nl.tiesdavid.ssproject.exceptions.MoveException;

public class Move {
    private MoveType moveType;

    private Tile tile;

    public Move(MoveType moveType, Tile mTile, Board mBoard) throws MoveException {
        if (!moveType.equals(MoveType.ADD_TILE_AND_DRAW_NEW)) {
            throw new InvalidMoveTypeWithArgumentsException();
        }
        this.moveType = moveType;
        this.tile = mTile;

        if (mBoard.tileExists(tile.getX(), tile.getY())) {
            throw new CoordinatesAlreadyFilledException();
        }
    }
}
