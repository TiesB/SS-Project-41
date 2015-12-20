/**
 * Created by Ties on 20-12-2015.
 */
package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.MoveType;
import nl.tiesdavid.ssproject.exceptions.MoveException;

public class HumanPlayer extends Player {
    public HumanPlayer(String name, Board mBoard, Game mGame) {
        super(name, mBoard, mGame);
    }

    @Override
    public Move determineMove() throws MoveException{
        try {
            return new Move(MoveType.ADD_TILE_AND_DRAW_NEW, new Tile());
        } catch (MoveException e) {
            return null;
        }
    }
}
