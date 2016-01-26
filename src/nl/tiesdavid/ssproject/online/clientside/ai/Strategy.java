package nl.tiesdavid.ssproject.online.clientside.ai;

import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.online.clientside.ClientGame;

import java.util.ArrayList;

/**
 * Created by Ties on 26-1-2016.
 */
public interface Strategy {
    public ArrayList<Tile> determinePlaceMove(ClientGame game);
}
