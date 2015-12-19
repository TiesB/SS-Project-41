package nl.tiesdavid.ssproject;

public class Main {

    public static void main(String[] args) {
        Game game = new Game();
        for (int i = 1; i <= 4; i++) {
            game.addPlayer(new Player(Integer.toString(i)) {
                @Override
                public Tile determineMove() {
                    return null;
                }
            });
        }
        game.printScores();
    }
}
