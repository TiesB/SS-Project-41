/**
 * Created by Ties on 21-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game;

import nl.tiesdavid.ssproject.game.enums.MoveType;
import nl.tiesdavid.ssproject.game.exceptions.InvalidMoveTypeWithArgumentsException;
import nl.tiesdavid.ssproject.game.exceptions.NotInDeckException;
import nl.tiesdavid.ssproject.game.exceptions.StopEnteringException;

import java.util.ArrayList;
import java.util.Scanner;

public class HumanPlayer extends Player {
    public HumanPlayer(String name, Game game) {
        super(name, game);
    }

    @Override
    protected Move determineMove() {
        System.out.println(getName() +
                ", what kind of move do you want to make? (Type the corresponding number)");
        System.out.println("1 - Add a tile to the grid and draw one.");
        System.out.println("2 - Add multiple tiles to the grid.");
        System.out.println("3 - Trade one or more tiles from your deck.");
        int choice = readInt("Choice: ");
        return parseChoice(choice);
    }

    protected Tile getTileFromUser(String string, boolean readXY) throws StopEnteringException{
        Tile tile = readTile(string);
        try {
            tile = findTile(tile);

            if (readXY) {
                int x, y;
                if (game.getBoard().isEmpty()) {
                    x = 0;
                    y = 0;
                } else {
                    x = readInt("Choose a X coordinate: ");
                    y = readInt("Choose a Y coordinate: ");
                }

                tile.setX(x);
                tile.setY(y);
            }

            return tile;
        } catch (NotInDeckException e) {
            handleMoveException(e);
            return null;
        }
    }

    private ArrayList<Tile> getMultipleTilesFromUser(boolean readXY) {
        ArrayList<Tile> chosenTiles = new ArrayList<>();

        do {
            try {
                //I hate the >100 character line CheckStyle rule. I think it's stupid.
                Tile tile = getTileFromUser(
                        "Choose a tile. Use the format shown. Type '-' to stop entering.", readXY);
                tile = findTile(tile);
                if (chosenTiles.contains(tile) && !hasDuplicate(tile)) {
                    System.out.println("You have already chosen this tile.");
                } else {
                    chosenTiles.add(tile);
                    //TODO: Remove tile (temporarily?) from deck.
                }
            } catch (NotInDeckException e) {
                handleMoveException(e);
            } catch (StopEnteringException e) {
                System.out.println("You have chosen " + chosenTiles.size() + " tiles.");
            }
        } while (chosenTiles.size() + 1 <= DECK_SIZE);

        return chosenTiles;
    }

    private Move addToGridAndDraw() {
        Tile tile = null;
        try {
            tile = getTileFromUser("Choose a tile. Use the format shown: ", true);
        } catch (StopEnteringException ignored) {
        }
        if (tile == null) {
            //TODO: Communicate with user.
            return addToGridAndDraw();
        }
        return new Move(MoveType.ADD_TILE_AND_DRAW_NEW, tile);
    }

    private Move addMultipleToGrid() {
        try {
            return new Move(MoveType.ADD_MULTIPLE_TILES, getMultipleTilesFromUser(true));
        } catch (InvalidMoveTypeWithArgumentsException e) {
            handleMoveException(e);
            return determineMove();
        }
    }

    private Move tradeTiles() {
        try {
            return new Move(MoveType.TRADE_TILES, getMultipleTilesFromUser(false));
        } catch (InvalidMoveTypeWithArgumentsException e) {
            handleMoveException(e);
            return determineMove();
        }
    }

    private Move parseChoice(int choice) {
        switch (choice) {
            case 1:
                return addToGridAndDraw();
            case 2:
                return addMultipleToGrid();
            case 3:
                return tradeTiles();
            default:
                game.printScores();
                return null;
        }
    }

    protected Tile readTile(String prompt) throws StopEnteringException {
        System.out.println(deck);

        for (Tile.Shape shape : Tile.Shape.values()) {
            System.out.println(shape.printable + ": " + shape.user);
        }

        String input = "";
        boolean tileRead = false;
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine())) {
                if (scannerLine.hasNext()) {
                    input = scannerLine.next();
                    if (input.matches("[BGOPRY][ABCDEF]")) {
                        tileRead = true;
                    } else if (input.equals("-")) {
                        throw new StopEnteringException();
                    }
                }
            }
        } while (!tileRead);
        return parseTileString(new String[] {input});
    }

    protected Tile parseTileString(String[] input) {
        char[] chars = input[0].toCharArray();
        Tile.Color color = null;
        Tile.Shape shape = null;
        for (Tile.Color color1 : Tile.Color.values()) {
            if (chars[0] == color1.user) {
                color = color1;
            }
        }

        for (Tile.Shape shape1 : Tile.Shape.values()) {
            if (chars[1] == shape1.user) {
                shape = shape1;
            }
        }

        if (color == null || shape == null) {
            return null;
        }

        if (input.length > 1) {
            int x = Integer.parseInt(input[1]);
            int y = Integer.parseInt(input[2]);
            return new Tile (x, y, color, shape);
        }

        return new Tile(color, shape);
    }

    protected int readInt(String prompt) {
        int value = 0;
        boolean intRead = false;
        @SuppressWarnings("resource")
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine())) {
                if (scannerLine.hasNextInt()) {
                    intRead = true;
                    value = scannerLine.nextInt();
                }
            }
        } while (!intRead);
        return value;
    }
}
