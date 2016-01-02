/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game.enums;

public enum MoveType {
    ADD_TILE_AND_DRAW_NEW(1),
    ADD_MULTIPLE_TILES(2),
    TRADE_TILES(3);

    public static final int ADD_TILE_AND_DRAW_NEW_CODE = 1;
    public static final int ADD_MULTIPLE_TILES_CODE = 2;
    public static final int TRADE_TILES_CODE = 3;

    public final int code;

    MoveType(int code) {
        this.code = code;
    }
}
