package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.exceptions.NotEnoughPlayersException;

public class Qwirkle {
    public static void main(String[] args) {
        Game game = new Game();
        game.addPlayer(new HumanPlayer("Ties", game));
        game.addPlayer(new HumanPlayer("Freek", game));
        try {
            game.start();
        } catch (NotEnoughPlayersException e) {
            System.out.println(e.getMessage());
        }
    }
}
