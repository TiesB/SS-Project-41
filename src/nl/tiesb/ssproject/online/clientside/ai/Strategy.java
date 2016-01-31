package nl.tiesb.ssproject.online.clientside.ai;

import nl.tiesb.ssproject.game.Tile;
import nl.tiesb.ssproject.online.clientside.ClientGame;

import java.util.ArrayList;

/**
 * Created by Ties on 26-1-2016.
 */
public interface Strategy {
    public ArrayList<Tile> determinePlaceMove(ClientGame game,
                                              ArrayList<ArrayList<Tile>> previousPlaceMoves);
}
