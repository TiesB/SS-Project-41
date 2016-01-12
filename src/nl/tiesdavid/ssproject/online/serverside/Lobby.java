/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.exceptions.ExistingNameException;
import nl.tiesdavid.ssproject.game.exceptions.NonexistingPlayerException;
import nl.tiesdavid.ssproject.game.exceptions.UnsupportedOptionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lobby {
    public static final boolean DEBUG = true;

    private static final String[] OPTIONS = new String[]{"chat", "challenge"};

    public static final int NAME_ALREADY_EXISTS_ERROR = 2;

    public static final String WELCOME_COMMAND = "hello_from_the_otherside";
    public static final String JOIN_COMMAND = "joinlobby";

    public static final String GENERAL_CHAT_MESSAGE_COMMAND = "msg";
    public static final String PRIVATE_CHAT_MESSAGE_COMMAND = "msgpm";

    public static final String START_GAME_COMMAND = "start";

    public static final String NEW_CHALLENGE_COMMAND = "newchallenge";
    public static final String ACCEPT_CHALLENGE_SERVER_COMMAND = "accept";
    public static final String DECLINE_CHALLENGE_SERVER_COMMAND = "decline";

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
        if (DEBUG) {
            System.out.println("Sending message: " + sender.getPlayerName() + " " + message);
        }
        ArrayList<ClientHandler> receivers;
        if (sender.isInGame()) {
            OnlineGame game = sender.getCurrentGame();
            receivers = gamesWithClients.get(game);
        } else {
            receivers = clientsInLobby;
        }

        for (ClientHandler client : receivers) {
            if (client != sender) { //TODO: Decide whether sender should receive own message.
                client.sendMessageToClient(GENERAL_CHAT_MESSAGE_COMMAND + " "
                        + sender.getPlayerName() + " " + message);
            }
        }
    }

    public void sendPrivateChatMessage(ClientHandler sender, String receiver, String message)
            throws UnsupportedOptionException, NonexistingPlayerException {
        if (DEBUG) {
            System.out.println("Sending private message " + sender.getPlayerName()
                    + " " + receiver + " " + message);
        }

        ClientHandler receiverHandler = getClientHandlerByName(receiver);
        if (!receiverHandler.hasOption(ClientHandler.CHAT_OPTION)) {
            throw new UnsupportedOptionException(receiver, ClientHandler.CHAT_OPTION);
        }

        receiverHandler.sendMessageToClient(PRIVATE_CHAT_MESSAGE_COMMAND
                + " " + sender.getPlayerName()
                + " " + message);
    }

    public void createChallenge(ClientHandler creator, String[] players)
            throws UnsupportedOptionException, NonexistingPlayerException {
        if (DEBUG) {
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
            if (!playerHandler.hasOption(ClientHandler.CHALLENGE_OPTION)) {
                String name = playerHandler.getPlayerName();
                //Dit hoeft dus alleen maar omdat CheckStyle anders zeurt over 100+ karakters
                // , wat achterlijk is.
                throw new UnsupportedOptionException(name, ClientHandler.CHALLENGE_OPTION);
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
            player.sendMessageToClient(NEW_CHALLENGE_COMMAND
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
        for (ArrayList<ClientHandler> clientHandlers : waitingClientsByRequestedNo.values()) {
            if (clientHandlers.contains(client)) {
                clientHandlers.remove(client);
            }
        }

        challenge.playerAccepts(client);
        challenge.getCreator()
                .sendMessageToClient(ACCEPT_CHALLENGE_SERVER_COMMAND
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
                .sendMessageToClient(DECLINE_CHALLENGE_SERVER_COMMAND
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
            waitingClientsByRequestedNo.put(requestedNoOfPlayers, new ArrayList<ClientHandler>());
        }

        waitingClientsByRequestedNo.get(requestedNoOfPlayers).add(client);

        checkWaitingPlayers();
    }

    private void checkWaitingPlayers() {
        for (Integer requestedNo : waitingClientsByRequestedNo.keySet()) {
            if (waitingClientsByRequestedNo.get(requestedNo).size() == requestedNo) {
                startWaitingPlayersGame(requestedNo);
            }
        }
    }

    private void startWaitingPlayersGame(int requestedNo) {
        if (DEBUG) {
            System.out.println("Starting waiting players game...");
        }

        ArrayList<ClientHandler> clients = waitingClientsByRequestedNo.get(requestedNo);
        OnlineGame game = new OnlineGame(this);
        for (ClientHandler client : clients) {
            if (DEBUG) {
                System.out.println(client.getPlayerName());
            }

            game.addPlayer(client);
        }

        startGame(game);
    }

    private void sendMessageToAllClients(String message) {
        if (DEBUG) {
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
        sendMessageToAllClients(JOIN_COMMAND + " " + name + " " + options);
    }

    private void startGame(OnlineGame game) {
        if (DEBUG) {
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

        String startMessage = START_GAME_COMMAND;
        for (String clientName : clientNames) {
            startMessage += " " + clientName;
        }

        sendMessageToAllClients(startMessage);

        game.play();
    }

    private void removeWaitingClient(ClientHandler client) {
        for (ArrayList<ClientHandler> waitingClients : waitingClientsByRequestedNo.values()) {
            waitingClients.remove(client);
        }
        clientsInLobby.remove(client);
    }

    public void disconnectClient(ClientHandler client) {
        namesWithClients.remove(client.getPlayerName());
    }

    private void sendWelcomeMessage(ClientHandler client) {
        String message = WELCOME_COMMAND;
        for (String option : OPTIONS) {
            message += " " + option;
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
