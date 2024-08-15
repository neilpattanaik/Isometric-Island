package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.IslandTileSet;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.*;
import java.util.ArrayList;

/**
 * The GameEngine class manages the core game logic, including world generation, rendering, and player interactions.
 */
public class GameEngine {
    private final IslandTileSet tileSet = new IslandTileSet();
    private final World world;
    private final int height;
    private final int width;
    private final ArrayList<AnimatedTETile> animatedTiles;
    private Player player;
    private TERenderer ter;
    private boolean isometric;
    private boolean prevPressColon;

    /**
     * Constructs a GameEngine with the specified parameters.
     *
     * @param seed the seed for world generation
     * @param height the height of the world
     * @param width the width of the world
     * @param spread the spread type
     * @param shape the shape type
     * @param minRoomDim the minimum room dimension
     * @param maxRoomDim the maximum room dimension
     * @param continuation the continuation type
     * @param continuePercentage the continuation percentage
     */
    public GameEngine(long seed,
                      int height,
                      int width,
                      String spread,
                      String shape,
                      int minRoomDim,
                      int maxRoomDim,
                      String continuation,
                      int continuePercentage) {
        this.height = height;
        this.width = width;
        this.world = new World(seed, height, width, spread, shape, minRoomDim, maxRoomDim, continuation, continuePercentage, tileSet);
        this.animatedTiles = new ArrayList<>();
    }

    /**
     * Initializes the TERenderer based on the current view mode (isometric or standard).
     */
    public void initializeTERenderer() {
        if (isometric) {
            this.ter = new IsometricTERenderer();
        } else {
            this.ter = new TERenderer();
        }

        ter.initialize(width, height);
    }

    /**
     * Initializes the game board with the specified player position, view mode, and direction.
     *
     * @param pos the initial position of the player
     * @param isometric whether the view mode is isometric
     * @param dir the initial direction the player is facing
     */
    public void initializeGameBoard(Point pos, boolean isometric, Direction dir) {
        this.player = new Player(dir, tileSet);
        animatedTiles.add(player);

        TETile[][] worldTiles = world.getTiles();
        player.setPos(pos, worldTiles[pos.x][pos.y]);
        if (isometric) {
            switchViews();
        }
    }

    /**
     * Initializes the game board with default settings.
     */
    public void initializeGameBoard() {
        initializeGameBoard(world.getRandomRoomCoords(), false, Direction.DOWN);
    }

    /**
     * Switches between isometric and standard views.
     */
    public void switchViews() {
        this.isometric = (!isometric);
        world.switchViews(isometric);

        Direction dir = player.getLastDir();
        Point pos = player.getPos();
        animatedTiles.remove(player);

        if (isometric) {
            player = new IsometricPlayer(dir, tileSet);
        } else {
            player = new Player(dir, tileSet);
        }

        animatedTiles.add(player);
        player.setPos(pos, world.getTiles()[pos.x][pos.y]);
        updateTiles();
        initializeTERenderer();
    }

    /**
     * Paints the current game state, rendering if specified.
     *
     * @param render whether to render the game
     */
    private void paint(boolean render) {
        updateTiles();
        if (render) {
            ter.renderFrame(world.getTiles(), player.getPos());
        }
        SpriteSheet.clear();
    }

    /**
     * Executes a game tick, handling key presses and rendering if specified.
     *
     * @param render whether to render the game
     * @param cycle whether to cycle through game ticks
     * @return false if the game should exit, true otherwise
     */
    public boolean tick(boolean render, boolean cycle) {
        do {
            if (!handleKeyPress()) {
                return false;
            }
            paint(render);

        } while (cycle);
        return true;
    }

    /**
     * Updates the tiles in the game world based on the current state of animated tiles.
     */
    private void updateTiles() {
        TETile[][] worldTiles = world.getTiles();
        for (AnimatedTETile tile : animatedTiles) {
            Point tilePos = tile.getPos();
            worldTiles[tilePos.x][tilePos.y] = tile.getPrevTile();

            tile.update();
            tilePos = tile.getPos();
            worldTiles[tilePos.x][tilePos.y] = tile;
        }
    }

    /**
     * Returns the tiles of the current game world.
     *
     * @return a 2D array of TETile representing the game world
     */
    public TETile[][] getTiles() {
        return world.getTiles();
    }

    /**
     * Handles key presses, updating the game state accordingly.
     *
     * @return false if the game should exit, true otherwise
     */
    private boolean handleKeyPress() {
        // if ter is null, we are not rendering, so we don't need to handle key presses
        if (ter == null) {
            return false;
        }

        if (StdDraw.hasNextKeyTyped()) {
            return handleKeyPress(StdDraw.nextKeyTyped());
        }
        return true;
    }

    /**
     * Handles a specific key press, updating the game state accordingly.
     *
     * @param key the key that was pressed
     * @return false if the game should exit, true otherwise
     */
    public boolean handleKeyPress(char key) {
        switch (key) {
            case ':':
                prevPressColon = true;
                return true;
            case 'w':
            case 'W':
                player.move(Direction.UP, world.getTiles());
                break;
            case 'a':
            case 'A':
                player.move(Direction.LEFT, world.getTiles());
                break;
            case 's':
            case 'S':
                player.move(Direction.DOWN, world.getTiles());
                break;
            case 'd':
            case 'D':
                player.move(Direction.RIGHT, world.getTiles());
                break;
            case 'k':
            case 'K':
                switchViews();
                break; // other render-related key presses handled directly by renderer for fluidity
            case 'q':
            case 'Q':
                if (prevPressColon) {
                    return false;
                }
        }
        prevPressColon = false;
        return true;
    }

    /**
     * Returns a string representation of the current game state.
     *
     * @return a string representing the current game state
     */
    @Override
    public String toString() {
        Point playerPos = player.getPos();
        Direction lastDir = player.getLastDir();

        return
                isometric
                        + "," + playerPos.x + "," + playerPos.y
                        + "," + lastDir;
    }
}