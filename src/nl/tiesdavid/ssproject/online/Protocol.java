/**
 * Created by Ties on 23-1-2016.
 */
package nl.tiesdavid.ssproject.online;

public class Protocol {
    // Features
    public static final String CHAT_FEATURE = "chat";
    public static final String CHALLENGE_FEATURE = "challenge";
    public static final String DISCONNECT_FEATURE = "disconnect";
    public static final String LEADERBOARD_FEATURE = "leaderboard"; // TODO: 25-1-2016 Use enum


    // Server -> Client control commands
    public static final String SERVER_WELCOME_COMMAND = "hello_from_the_other_side";
    public static final String SERVER_PLAYERS_COMMAND = "players";
    public static final String SERVER_JOIN_COMMAND = "joinlobby";
    public static final String SERVER_DISCONNECT_COMMAND = "disconnect";
    public static final String SERVER_START_GAME_COMMAND = "start";
    public static final String SERVER_END_GAME_COMMAND = "endgame";

    // Server -> Client chat commands
    public static final String SERVER_GENERAL_CHAT_MESSAGE_COMMAND = "msg";
    public static final String SERVER_PRIVATE_CHAT_MESSAGE_COMMAND = "msgpm";

    // Server -> Client challenge commands
    public static final String SERVER_NEW_CHALLENGE_COMMAND = "newchallenge";
    public static final String SERVER_ACCEPT_CHALLENGE_SERVER_COMMAND = "accept";
    public static final String SERVER_DECLINE_CHALLENGE_SERVER_COMMAND = "decline";

    // Server -> Client game commands
    public static final String SERVER_TURN_COMMAND = "turn";
    public static final String SERVER_NEW_STONES_COMMAND = "newstones";
    public static final String SERVER_PLACED_COMMAND = "placed";
    public static final String SERVER_TRADED_COMMAND = "traded";


    // Client -> Server control commands
    public static final String CLIENT_HELLO_COMMAND = "hello";
    public static final String CLIENT_WAIT_FOR_GAME_COMMAND = "join";
    public static final String CLIENT_DISCONNECT_COMMAND = "client_disconnect";

    // Client -> Server chat commands
    public static final String CLIENT_GENERAL_CHAT_COMMAND = "chat";
    public static final String CLIENT_PRIVATE_CHAT_COMMAND = "chatpm";

    // Client -> Server challenge commands
    public static final String CLIENT_CREATE_CHALLENGE_COMMAND = "challenge";
    public static final String CLIENT_ACCEPT_CHALLENGE_COMMAND = "accept";
    public static final String CLIENT_START_CHALLENGE_COMMAND = "start";
    public static final String CLIENT_DECLINE_CHALLENGE_COMMAND = "decline";

    // Client -> Server game commands
    public static final String CLIENT_PLACE_COMMAND = "place";
    public static final String CLIENT_TRADE_COMMAND = "trade";


    // Error codes
    public static final int NAME_ALREADY_EXISTS_ERROR = 2;
    public static final int UNACCEPTABLE_NAME_ERROR = 2;
    public static final int WRONG_COMMAND_ERROR = 0;
}
