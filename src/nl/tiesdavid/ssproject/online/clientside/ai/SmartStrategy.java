/**
 * Created by Ties on 26-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ai;

import javafx.collections.ObservableList;
import nl.tiesdavid.ssproject.game.Board;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.InvalidTilePlacementException;
import nl.tiesdavid.ssproject.online.clientside.ClientController;
import nl.tiesdavid.ssproject.online.clientside.ClientGame;

import java.util.ArrayList;
import java.util.Collections;

public class SmartStrategy implements Strategy {
    public enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

    private static ArrayList<Tile> findMatchingTiles(Board board, ArrayList<Tile> tiles) {
        System.out.println("Trying to find matching tiles for: " + tiles);
        ArrayList<Tile> result = new ArrayList<>();

        Tile firstTile = tiles.get(0);
        Tile.Color color = firstTile.getColor();
        Tile.Shape shape = firstTile.getShape();

        board.getTiles().stream().filter(tile -> Board.checkTile(tile, color, shape)).forEach(tile -> {
            System.out.println("Found: " + tile.toLongString());
            result.add(tile);
        });

        System.out.println("Found matching tiles: " + result);

        return result;
    }

    private ArrayList<Tile> generateFirstMove(ArrayList<Tile> tiles) {
        if (ClientController.DEBUG) {
            System.out.println("Generating first move with: " + tiles);
        }
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            tile.setX(i);
            tile.setY(0);
        }
        return tiles;
    }

    @Override
    public ArrayList<Tile> determinePlaceMove(ClientGame game, ArrayList<ArrayList<Tile>> previousPlaceMoves) {
        System.out.println("Trying to determine a place move.");
        for (ArrayList<Tile> previousPlaceMove : previousPlaceMoves) {
            System.out.println("Previous place move: " + previousPlaceMove);
        }
        Board board = game.getBoard();
        ObservableList deck = game.getDeck();

        System.out.println("Current state of deck: " + deck.toString());
        System.out.println("Current state of board: " + board.toString());
        System.out.println("Amount of tiles on the board: " + board.getTiles().size());

        ArrayList<ArrayList<Tile>> sets = AIUtils.findSets(new ArrayList<>(deck));

        if (ClientController.DEBUG) {
            System.out.println("      Found possible sets: " + sets);
        }

        ArrayList<Tile> setToRemove = null;

        if (previousPlaceMoves != null) {
            for (ArrayList<Tile> previousPlaceMove : previousPlaceMoves) {
                for (ArrayList<Tile> set : sets) {
                    if (set.size() == previousPlaceMove.size()) {
                        boolean isPreviousMove = true;
                        for (Tile tile : set) {
                            if (!previousPlaceMove.contains(tile)) {
                                isPreviousMove = false;
                                continue;
                            }
                            if (isPreviousMove) {
                                setToRemove = set;
                                break;
                            }
                        }
                    }
                }
                while (sets.contains(setToRemove)) {
                    System.out.println("Removing set: " + setToRemove);
                    System.out.println(sets.remove(setToRemove));
                    System.out.println("      Found possible sets: " + sets);
                }
            }
        }

        System.out.println("Found sets: " + sets);

        if (sets.size() == 0) {
            System.out.println("Sets size is 0.");
            return null;
        }

        Collections.sort(sets, (o1, o2) -> Integer.compare(o1.size(), o2.size()));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            //
        }

        int setsSize = sets.size();
        for (int i = setsSize - 1; i >= 0; i--) {
            ArrayList<Tile> tileList = sets.get(i);

            if (board.isEmpty()) {
                ArrayList<Tile> tiles = generateFirstMove(tileList);
                previousPlaceMoves.add(tiles);
                return tiles;
            }

            if (!previousPlaceMoves.contains(tileList)) {
                System.out.println("Adding to previous move: " + tileList);
                previousPlaceMoves.add(tileList);
            }

            ArrayList<Tile> matchingTiles = findMatchingTiles(board, tileList);
            for (Tile matchingTile : matchingTiles) {
                ArrayList<Tile> legalTilesWithXY = getLegalPlacement(board, matchingTile, tileList);
                if (legalTilesWithXY != null) {
                    return legalTilesWithXY;
                }
            }
        }
        return null;
    }

    public ArrayList<Tile> getLegalPlacement(Board board, Tile tile,
                                             ArrayList<Tile> tilesWithoutXY) {
        System.out.println("Trying: " + tile.toLongString());
        int y = tile.getY();
        int x = tile.getX();
        ArrayList<Tile> tilesWithXY;
        for (Direction direction : Direction.values()) {
            switch (direction) {
                case UP:
                    tilesWithXY = new ArrayList<>(tilesWithoutXY);
                    for (int i = 0; i < tilesWithXY.size(); i++) {
                        Tile tile1 = tilesWithXY.get(i);
                        tile1.setY(y + i + 1);
                        tile1.setX(x);
                    }
                    try {
                        board.tryPlace(tilesWithXY);
                        System.out.println("Up placement possible");
                        return tilesWithXY;
                    } catch (InvalidTilePlacementException e) {
                        System.out.println("Up placement not possible.");
                    }
                    break;
                case DOWN:
                    tilesWithXY = new ArrayList<>(tilesWithoutXY);
                    for (int i = 0; i < tilesWithXY.size(); i++) {
                        Tile tile1 = tilesWithXY.get(i);
                        tile1.setY(y - i - 1);
                        tile1.setX(x);
                    }
                    try {
                        board.tryPlace(tilesWithXY);
                        System.out.println("Down placement possible");
                        return tilesWithXY;
                    } catch (InvalidTilePlacementException e) {
                        System.out.println("Down placement not possible.");
                    }
                    break;
                case LEFT:
                    tilesWithXY = new ArrayList<>(tilesWithoutXY);
                    for (int i = 0; i < tilesWithXY.size(); i++) {
                        Tile tile1 = tilesWithXY.get(i);
                        tile1.setY(y);
                        tile1.setX(x - i - 1);
                    }
                    try {
                        board.tryPlace(tilesWithXY);
                        System.out.println("Left placement possible");
                        return tilesWithXY;
                    } catch (InvalidTilePlacementException e) {
                        System.out.println("Left placement not possible.");
                    }
                    break;
                case RIGHT:
                    tilesWithXY = new ArrayList<>(tilesWithoutXY);
                    for (int i = 0; i < tilesWithXY.size(); i++) {
                        Tile tile1 = tilesWithXY.get(i);
                        tile1.setY(y + i + 1);
                        tile1.setX(x);
                    }
                    try {
                        board.tryPlace(tilesWithXY);
                        System.out.println("Right placement possible");
                        return tilesWithXY;
                    } catch (InvalidTilePlacementException e) {
                        System.out.println("Right placement not possible.");
                    }
                    break;
            }
        }
        System.out.println("No placement possible for tile: " + tile.toLongString());
        return null;
    }
}
