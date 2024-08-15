package core;

import tileengine.IslandTileSet;
import tileengine.TETile;

/**
 * The IsometricPlayer class represents a player character in an isometric view.
 * It extends the Player class to provide isometric-specific animations and movement.
 */
public class IsometricPlayer extends Player {
    private static final int[][] walkWestIso = new int[][]{{5, 2}, {5, 3}};
    private static final int[][] walkSouthIso = new int[][]{{2, 1}, {2, 2}, {2, 3}};
    private static final int[][] walkEastIso = new int[][]{{1, 1}, {1, 2}, {1, 3}};
    private static final int[][] walkNorthIso = new int[][]{{7, 1}, {7, 2}, {7, 3}};

    /**
     * Constructs an IsometricPlayer with the specified IslandTileSet.
     *
     * @param tileSet the IslandTileSet used for the player's sprite sheet
     */
    public IsometricPlayer(IslandTileSet tileSet) {
        super(tileSet.getPlayerSpriteSheetPath(), walkSouthIso, 0, 16, 24, 1);
    }

    /**
     * Constructs an IsometricPlayer with the specified direction and IslandTileSet.
     *
     * @param dir the initial direction the player is facing
     * @param tileSet the IslandTileSet used for the player's sprite sheet
     */
    public IsometricPlayer(Direction dir, IslandTileSet tileSet) {
        this(tileSet);
        switchAnimation(getAnimation(dir));
    }

    /**
     * Moves the player in the specified direction and updates the animation.
     *
     * @param dir the direction to move the player
     * @param world the 2D array representing the game world
     */
    @Override
    public void move(Direction dir, TETile[][] world) {
        switchAnimation(getAnimation(dir));
        super.start();
        super.moveLocation(dir, world);
    }

    /**
     * Returns the animation frames for the specified direction.
     *
     * @param dir the direction the player is facing
     * @return the animation frames for the specified direction
     */
    private int[][] getAnimation(Direction dir) {
        lastDir = dir;
        if (dir.equals(Direction.UP)) {
            return walkNorthIso;
        } else if (dir.equals(Direction.DOWN)) {
            return walkSouthIso;
        } else if (dir.equals(Direction.RIGHT)) {
            return walkEastIso;
        } else if (dir.equals(Direction.LEFT)) {
            return walkWestIso;
        }
        return null; // never reached
    }
}