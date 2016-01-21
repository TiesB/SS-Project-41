/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import java.util.ArrayList;

public class Challenge {
    private final int id;
    private final ClientHandler creator;
    private final ArrayList<ClientHandler> invitedPlayers;
    private final ArrayList<ClientHandler> playersWhoAccepted;

    public Challenge(int id, ClientHandler creator, ArrayList<ClientHandler> invitedPlayers) {
        this.id = id;
        this.creator = creator;
        this.invitedPlayers = invitedPlayers;

        this.playersWhoAccepted = new ArrayList<>();

        this.playersWhoAccepted.add(creator);
    }

    public OnlineGame startGame(Lobby lobby) {
        synchronized (this) {
            OnlineGame game = new OnlineGame(lobby);
            playersWhoAccepted.forEach(game::addPlayer);
            return game;
        }
    }

    public void playerAccepts(ClientHandler player) {
        if (!invitedPlayers.contains(player)) {
            return;
        }

        playersWhoAccepted.add(player);
        invitedPlayers.remove(player);
    }

    public void playerDeclines(ClientHandler player) {
        if (!invitedPlayers.contains(player)) {
            return;
        }

        invitedPlayers.remove(player);
    }

    public int getId() {
        return this.id;
    }

    public ClientHandler getCreator() {
        return this.creator;
    }

    public ArrayList<ClientHandler> getInvitedPlayers() {
        return this.invitedPlayers;
    }

    public ArrayList<ClientHandler> getPlayersWhoAccepted() {
        return this.playersWhoAccepted;
    }
}
