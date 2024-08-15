package core;

import tileengine.IslandTileSet;
import tileengine.TETile;

import java.awt.*;

/**
 * The Player class represents a player character in the game.
 * It extends the AnimatedTETile class to provide animation capabilities.
 */
public class Player extends AnimatedTETile {
    private static final int[][] walkFacingSouthStd = new int[][]{new int[]{0, 0}, new int[]{1, 0}, new int[]{2, 0}, new int[]{3, 0}, new int[]{4, 0}};
    private static final int[][] walkFacingNorthStd = new int[][]{new int[]{0, 1}, new int[]{1, 1}, new int[]{2, 1}, new int[]{3, 1}, new int[]{4, 1}};
    private static final int[][] walkFacingEastStd = new int[][]{new int[]{0, 3}, new int[]{1, 3}, new int[]{2, 3}, new int[]{3, 3}, new int[]{4, 3}};
    private static final int[][] walkFacingWestStd = new int[][]{new int[]{0, 2}, new int[]{1, 2}, new int[]{2, 2}, new int[]{3, 2}, new int[]{4, 2}};
    protected Direction lastDir;

    /**
     * Constructs a Player with the specified IslandTileSet.
     *
     * @param tileSet the IslandTileSet used for the player's sprite sheet
     */
    public Player(IslandTileSet tileSet) {
        super('P', Color.pink, Color.black, "player", 15,
                tileSet.getPlayerSpriteSheetPath(),
                walkFacingSouthStd, 0, 16, 16, 1);
        lastDir = Direction.DOWN;
    }

    /**
     * Constructs a Player with the specified direction and IslandTileSet.
     *
     * @param dir the initial direction the player is facing
     * @param tileSet the IslandTileSet used for the player's sprite sheet
     */
    public Player(Direction dir, IslandTileSet tileSet) {
        this(tileSet);
        switchAnimation(getAnimation(dir));
    }

    /**
     * Constructs a Player with the specified parameters.
     *
     * @param tilesetFilepath the file path to the player's sprite sheet
     * @param defaultAnimation the default animation frames
     * @param delay the delay between animation frames
     * @param tileWidth the width of each tile in the sprite sheet
     * @param tileHeight the height of each tile in the sprite sheet
     * @param cycles the number of cycles for the animation
     */
    public Player(String tilesetFilepath, int[][] defaultAnimation, int delay, int tileWidth, int tileHeight, int cycles) {
        super('P', Color.pink, Color.black, "player", 1,
                tilesetFilepath,
                defaultAnimation, delay, tileWidth, tileHeight, cycles);
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
        moveLocation(dir, world);
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
            return walkFacingNorthStd;
        } else if (dir.equals(Direction.DOWN)) {
            return walkFacingSouthStd;
        } else if (dir.equals(Direction.RIGHT)) {
            return walkFacingEastStd;
        } else if (dir.equals(Direction.LEFT)) {
            return walkFacingWestStd;
        }
        return null; // never reached
    }

    /**
     * Moves the player's location in the specified direction.
     *
     * @param dir the direction to move the player
     * @param world the 2D array representing the game world
     */
    public void moveLocation(Direction dir, TETile[][] world) {
        super.move(dir, world);
    }

    /**
     * Returns the last direction the player moved.
     *
     * @return the last direction the player moved
     */
    public Direction getLastDir() {
        return lastDir;
    }
}