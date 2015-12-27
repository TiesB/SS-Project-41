package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.exceptions.NotEnoughPlayersException;

class Qwirkle {
    public static void main(String[] args) {
        Game game = new Game();
        game.addPlayer(new HumanPlayer("Ties", game));
        game.addPlayer(new HumanPlayer("Freek", game));
        try {
            game.play();
            System.out.println(game);
        } catch (NotEnoughPlayersException e) {
            System.out.println(e.getMessage());
        }
    }
}
