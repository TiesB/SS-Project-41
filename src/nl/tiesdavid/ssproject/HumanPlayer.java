/**
 * Created by Ties on 21-12-2015.
 */
package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.MoveType;
import nl.tiesdavid.ssproject.exceptions.InvalidMoveTypeWithArgumentsException;

import java.util.ArrayList;
import java.util.Scanner;

public class HumanPlayer extends Player {
    public HumanPlayer(String name, Game game) {
        super(name, game);
    }

    @Override
    public Move determineMove() {
        Board board = game.getBoard();
        System.out.println(board);
        System.out.println(getName() +
                ", what kind of move do you want to make? (Type the corresponding number)");
        System.out.println("1 - Add a tile to the grid and draw one.");
        System.out.println("2 - Add multiple tiles to the grid.");
        if (game.hasTilesLeft()) {
            System.out.println("3 - Trade one or more tiles from your deck.");
        } else {
            System.out.println("It is no longer possible to trade tiles since the bag is empty.");
        }
        int choice = readInt("Choice: ");
        return parseChoice(choice);
    }

    private Tile getTileFromUser(String string, boolean readXY) {
        Tile tile = readTile(string);

        if (tile == null) {
            //TODO: Communicate with user.
            return null;
        }
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
    }

    private ArrayList<Tile> getMultipleTilesFromUser(boolean readXY) {
        ArrayList<Tile> chosenTiles = new ArrayList<>();

        do {
            //I hate the >100 character line CheckStyle check. I think it's stupid.
            Tile tile = getTileFromUser(
                    "Choose a tile. Use the format shown. Type '-' to stop entering.", readXY);
            if (tile == null) {
                break;
            }
            tile = findTile(tile);

            if (!deck.contains(tile)) {
                System.out.println("You don't have this tile in your deck.");
            } else if (chosenTiles.contains(tile)) {
                System.out.println("You have already chosen this tile.");
            } else {
                chosenTiles.add(tile);
                //TODO: Remove tile (temporarily?) from deck.
            }
        } while (chosenTiles.size() + 1 <= DECK_SIZE);

        return chosenTiles;
    }

    private Move addToGridAndDraw() {
        Tile tile = getTileFromUser("Choose a tile. Use the format shown: ", true);
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
            return handleMoveException(e);
        }
    }

    private Move tradeTiles() {
        try {
            return new Move(MoveType.TRADE_TILES, getMultipleTilesFromUser(false));
        } catch (InvalidMoveTypeWithArgumentsException e) {
            return handleMoveException(e);
        }
    }

    private Move parseChoice(int choice) {
        switch (choice) {
            case 1:
                return addToGridAndDraw();
            case 2:
                return addMultipleToGrid();
            case 3:
                if (game.hasTilesLeft()) {
                    //TODO: Communicate with user.
                    return tradeTiles();
                } else {
                    return determineMove();
                }
            default:
                return null;
        }
    }

    private Tile readTile(String prompt) {
        System.out.println(deck);
        String input = "";
        boolean tileRead = false;
        @SuppressWarnings("resource")
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine())) {
                if (scannerLine.hasNext()) {
                    input = scannerLine.next();
                    if (input.matches("[BGOPRY][O#+*@X]") || input.equals("-")) {
                        tileRead = true;
                    } else if (input.equals("-")) {
                        return null;
                    }
                }
            }
        } while (!tileRead);
        return parseTileString(input);
    }

    private Tile parseTileString(String input) {
        char[] chars = input.toCharArray();
        Tile.Color color;
        Tile.Shape shape;
        switch (chars[0]) {
            case 'B':
                color = Tile.Color.BLUE;
                break;
            case 'G':
                color = Tile.Color.GREEN;
                break;
            case 'O':
                color = Tile.Color.ORANGE;
                break;
            case 'P':
                color = Tile.Color.PURPLE;
                break;
            case 'R':
                color = Tile.Color.RED;
                break;
            case 'Y':
                color = Tile.Color.YELLOW;
                break;
            default:
                return null;
        }

        switch (chars[1]) {
            case 'O':
                shape = Tile.Shape.CIRCLE;
                break;
            case '#':
                shape = Tile.Shape.DIAMOND;
                break;
            case '+':
                shape = Tile.Shape.PLUS;
                break;
            case '*':
                shape = Tile.Shape.STAR;
                break;
            case '@':
                shape = Tile.Shape.SQUARE;
                break;
            case 'X':
                shape = Tile.Shape.X;
                break;
            default:
                return null;
        }

        return new Tile(color, shape);
    }

    private int readInt(String prompt) {
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
