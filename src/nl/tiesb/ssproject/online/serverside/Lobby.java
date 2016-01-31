/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesb.ssproject.online.serverside;

import nl.tiesb.ssproject.game.exceptions.NonexistingPlayerException;
import nl.tiesb.ssproject.game.exceptions.UnsupportedOptionException;
import nl.tiesb.ssproject.online.Protocol;
import nl.tiesb.ssproject.game.exceptions.ExistingNameException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lobby {
    public static final String[] SERVER_FEATURES = {Protocol.CHALLENGE_FEATURE, Protocol.CHAT_FEATURE};

    private Map<String, ClientHandler> namesWithClients;
    private Map<OnlineGame, ArrayList<ClientHandler>> gamesWithClients;
    private Map<Integer, Challenge> challengesByID;
    private ArrayList<ClientHandler> clientsInLobby;
    private Map<Integer, ArrayList<ClientHandler>> waitingClientsByRequestedNo;

    public Lobby() {
        this.namesWithClients = new HashMap<>();
        this.gamesWithClients = new HashMap<>();
        this.challengesByID = new HashMap<>();
        this.clientsInLobby = new ArrayList<>();
        this.waitingClientsByRequestedNo = new HashMap<>();
    }

    public void sendGeneralChatMessage(ClientHandler sender, String message) {
        if (ClientHandler.DEBUG) {
            System.out.println("Sending message: " + sender.getPlayerName() + " " + message);
        }
        ArrayList<ClientHandler> receivers;
        if (sender.isInGame()) {
            OnlineGame game = sender.getCurrentGame();
            receivers = gamesWithClients.get(game);
        } else {
            receivers = clientsInLobby;
        }

        //TODO: Decide whether sender should receive own message.
        receivers.stream().forEach(client -> { //TODO: Decide whether sender should receive own message.
            client.sendMessageToClient(Protocol.SERVER_GENERAL_CHAT_MESSAGE_COMMAND + " "
                    + sender.getPlayerName() + " " + message);
        });
    }

    public void sendPrivateChatMessage(ClientHandler sender, String receiver, String message)
            throws UnsupportedOptionException, NonexistingPlayerException {
        if (ClientHandler.DEBUG) {
            System.out.println("Sending private message " + sender.getPlayerName()
                    + " " + receiver + " " + message);
        }

        ClientHandler receiverHandler = getClientHandlerByName(receiver);
        if (!receiverHandler.hasOption(Protocol.CHAT_FEATURE)) {
            throw new UnsupportedOptionException(receiver, Protocol.CHAT_FEATURE);
        }

        receiverHandler.sendMessageToClient(Protocol.SERVER_PRIVATE_CHAT_MESSAGE_COMMAND
                + " " + sender.getPlayerName()
                + " " + message);
    }

    public void createChallenge(ClientHandler creator, String[] players)
            throws UnsupportedOptionException, NonexistingPlayerException {
        if (ClientHandler.DEBUG) {
            System.out.println("Creating challenge: " + creator.getPlayerName());
        }

        if (players.length + 1 < 2) {
            creator.sendWrongCommandMessage();
            return;
        }

        ArrayList<ClientHandler> playerHandlers = new ArrayList<>();
        for (String player : players) {
            playerHandlers.add(getClientHandlerByName(player));
        }
        for (ClientHandler playerHandler : playerHandlers) {
            if (!playerHandler.hasOption(Protocol.CHALLENGE_FEATURE)) {
                String name = playerHandler.getPlayerName();
                //Dit hoeft dus alleen maar omdat CheckStyle anders zeurt over 100+ karakters
                // , wat achterlijk is.
                throw new UnsupportedOptionException(name, Protocol.CHALLENGE_FEATURE);
            }

            if (!clientsInLobby.contains(playerHandler)) {
                //TODO
                playerHandlers.remove(playerHandler);
            }
        }

        int id = gamesWithClients.size() + 1;

        Challenge challenge = new Challenge(id, creator, playerHandlers);
        challengesByID.put(id, challenge);
        warnInvitedPlayers(challenge);
    }

    private void warnInvitedPlayers(Challenge challenge) {
        ArrayList<ClientHandler> players = challenge.getInvitedPlayers();
        String creatorName = challenge.getCreator().getPlayerName();
        for (ClientHandler player : players) {
            player.sendMessageToClient(Protocol.SERVER_NEW_CHALLENGE_COMMAND
                    + " " + Integer.toString(challenge.getId()) + " " + creatorName);
        }
    }

    public void acceptChallenge(ClientHandler client, int challengeId) {
        Challenge challenge = getChallengeById(challengeId);
        if (challenge == null) {
            client.sendWrongCommandMessage();
            return;
        }
        if (!clientsInLobby.contains(client)) {
            client.sendWrongCommandMessage();
            return;
        }
        waitingClientsByRequestedNo.values().stream().filter(clientHandlers -> clientHandlers.contains(client)).forEach(clientHandlers -> clientHandlers.remove(client));

        challenge.playerAccepts(client);
        challenge.getCreator()
                .sendMessageToClient(Protocol.SERVER_ACCEPT_CHALLENGE_SERVER_COMMAND
                        + " " + Integer.toString(challengeId)
                        + " " + client.getPlayerName());
    }

    public void declineChallenge(ClientHandler client, int challengeId) {
        Challenge challenge = getChallengeById(challengeId);
        if (challenge == null) {
            //TODO
            return;
        }

        challenge.playerDeclines(client);
        challenge.getCreator()
                .sendMessageToClient(Protocol.SERVER_DECLINE_CHALLENGE_SERVER_COMMAND
                        + " " + Integer.toString(challengeId)
                        + " " + client.getPlayerName());
    }

    public void startChallenge(ClientHandler client, int challengeId) {
        Challenge challenge = getChallengeById(challengeId);
        if (!challenge.getCreator().equals(client)) {
            return;
        }

        OnlineGame game = challenge.startGame(this);

        startGame(game);
    }

    public void waitForGame(ClientHandler client, int requestedNoOfPlayers) {
        if (!waitingClientsByRequestedNo.containsKey(requestedNoOfPlayers)) {
            waitingClientsByRequestedNo.put(requestedNoOfPlayers, new ArrayList<>());
        }

        waitingClientsByRequestedNo.get(requestedNoOfPlayers).add(client);

        checkWaitingPlayers();
    }

    private void checkWaitingPlayers() {
        waitingClientsByRequestedNo.keySet().stream().filter(requestedNo -> waitingClientsByRequestedNo.get(requestedNo).size() == requestedNo).forEach(this::startWaitingPlayersGame);
    }

    private void startWaitingPlayersGame(int requestedNo) {
        if (ClientHandler.DEBUG) {
            System.out.println("Starting waiting players game...");
        }

        ArrayList<ClientHandler> clients = waitingClientsByRequestedNo.get(requestedNo);
        OnlineGame game = new OnlineGame(this);
        for (ClientHandler client : clients) {
            if (ClientHandler.DEBUG) {
                System.out.println(client.getPlayerName());
            }

            game.addPlayer(client);
        }

        startGame(game);
    }

    private void sendMessageToAllClients(String message) {
        if (ClientHandler.DEBUG) {
            System.out.println("Sending message to all clients: " + message);
        }

        for (ClientHandler client : namesWithClients.values()) {
            client.sendMessageToClient(message);
        }
    }

    public void connectClient(ClientHandler client) throws ExistingNameException {
        String name = client.getPlayerName();
        if (namesWithClients.containsKey(name)) {
            throw new ExistingNameException(client.getPlayerName());
        }

        String options = client.getOptionsString();

        namesWithClients.put(name, client);
        clientsInLobby.add(client);
        sendWelcomeMessage(client);
        sendPlayersMessage(client);
        sendMessageToAllClients(Protocol.SERVER_JOIN_COMMAND + " " + name + " " + options);
    }

    private void startGame(OnlineGame game) {
        if (ClientHandler.DEBUG) {
            System.out.println("Starting game...");
        }

        ArrayList<ClientHandler> clientsInGame = game.getClientHandlers();
        ArrayList<String> clientNames = new ArrayList<>();

        for (ClientHandler clientInGame : clientsInGame) {
            removeWaitingClient(clientInGame);

            clientInGame.assignGame(game);

            clientNames.add(clientInGame.getPlayerName());
        }

        gamesWithClients.put(game, clientsInGame);

        String startMessage = Protocol.SERVER_START_GAME_COMMAND;
        for (String clientName : clientNames) {
            startMessage += " " + clientName;
        }

        sendMessageToAllClients(startMessage);

        game.start();
    }

    public void endGame(OnlineGame game) {
        ArrayList<ClientHandler> handlers = gamesWithClients.get(game);
        for (ClientHandler handler : handlers) {
            handler.disconnectFromGame();
        }

        for (ClientHandler handler : handlers) {
            if (namesWithClients.containsValue(handler)) {
                clientsInLobby.add(handler);
            }
        }
        gamesWithClients.remove(game);
    }

    private void removeWaitingClient(ClientHandler client) {
        for (ArrayList<ClientHandler> waitingClients : waitingClientsByRequestedNo.values()) {
            waitingClients.remove(client);
        }
        clientsInLobby.remove(client);
    }

    public void disconnectClient(ClientHandler client) {
        namesWithClients.remove(client.getPlayerName());
        for (ArrayList<ClientHandler> clientHandlerArrayList :
                waitingClientsByRequestedNo.values()) {
            for (ClientHandler clientHandler : clientHandlerArrayList) {
                if (clientHandler.equals(client)) {
                    clientHandlerArrayList.remove(client);
                    break;
                }
            }
        }
        sendMessageToAllClients(Protocol.SERVER_DISCONNECT_COMMAND + " " + client.getPlayerName());
    }

    private void sendWelcomeMessage(ClientHandler client) {
        String message = Protocol.SERVER_WELCOME_COMMAND;
        for (String option : SERVER_FEATURES) {
            message += " " + option;
        }

        client.sendMessageToClient(message);
    }

    private void sendPlayersMessage(ClientHandler client) {
        String message = Protocol.SERVER_PLAYERS_COMMAND;
        for (String s : namesWithClients.keySet()) {
            message += " " + s;
        }

        client.sendMessageToClient(message);
    }

    private ClientHandler getClientHandlerByName(String name) throws NonexistingPlayerException {
        ClientHandler handler = namesWithClients.get(name);

        if (handler == null) {
            throw new NonexistingPlayerException(name);
        }

        return handler;
    }

    private Challenge getChallengeById(int challengeId) {
        return this.challengesByID.get(challengeId);
    }
}
