package core;

import tileengine.TETile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * The GameState class manages the state of the game, including world generation, saving, and loading.
 */
public class GameState {
    private static final String FILENAME = "save.txt";
    private final boolean render;
    private String creationParams;
    private GameEngine engine;

    /**
     * Constructs a GameState with default world dimensions and random settings.
     *
     * @param render whether to render the game
     * @param seed the seed for world generation
     */
    public GameState(boolean render, long seed) {
        this(render, seed, 75, 150, null, null, -1, -1, null, -1);
    }

    /**
     * Constructs a GameState with specified parameters.
     *
     * @param render whether to render the game
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
    public GameState(boolean render, long seed, int height, int width, String spread, String shape, int minRoomDim, int maxRoomDim, String continuation, int continuePercentage) {
        this.render = render;
        creationParams = seed + "," + height + "," + width + "," + spread + "," + shape + "," + minRoomDim + "," + maxRoomDim + "," + continuation + "," + continuePercentage;
        engine = engineFromParams(creationParams);
        engine.initializeGameBoard();
        if (render) {
            engine.initializeTERenderer();
        }
    }

    /**
     * Constructs a GameState by loading from a save file.
     *
     * @param render whether to render the game
     */
    public GameState(boolean render) {
        this.render = render;
        String saveString = "";
        try {
            saveString = Files.readString(new File(FILENAME).toPath());
        } catch (IOException ex) {
            System.exit(0);
        }
        engine = engineFromSaveString(saveString, render);
    }

    /**
     * Returns the save string for the current game state.
     *
     * @param engine the game engine
     * @return the save string
     */
    public String getSaveString(GameEngine engine) {
        String worldState = engine.toString();
        return creationParams + ";" + worldState;
    }

    /**
     * Creates a GameEngine from the specified creation parameters.
     *
     * @param creationParams the creation parameters
     * @return the created GameEngine
     */
    private GameEngine engineFromParams(String creationParams) {
        String[] creationArgs = creationParams.split(",");
        return engine = new GameEngine(
                Long.parseLong(creationArgs[0]),
                Integer.parseInt(creationArgs[1]),
                Integer.parseInt(creationArgs[2]),
                creationArgs[3].equals("null") ? null : creationArgs[3],
                creationArgs[4].equals("null") ? null : creationArgs[4],
                Integer.parseInt(creationArgs[5]),
                Integer.parseInt(creationArgs[6]),
                creationArgs[7].equals("null") ? null : creationArgs[7],
                Integer.parseInt(creationArgs[8])
        );
    }

    /**
     * Creates a GameEngine from the specified save string.
     *
     * @param saveString the save string
     * @param render whether to render the game
     * @return the created GameEngine
     */
    private GameEngine engineFromSaveString(String saveString, boolean render) {
        String[] paramSplit = saveString.split(";");
        creationParams = paramSplit[0];
        GameEngine engine = engineFromParams(creationParams);
        String[] worldState = paramSplit[1].split(",");
        engine.initializeGameBoard(
                new Point(Integer.parseInt(worldState[1]), Integer.parseInt(worldState[2])),
                Boolean.parseBoolean(worldState[0]),
                Direction.fromString(worldState[3])
        );
        if (render) {
            engine.initializeTERenderer();
        }
        return engine;
    }

    /**
     * Saves the current game state to a file.
     */
    private void save() {
        try {
            Files.writeString(new File(FILENAME).toPath(), getSaveString(engine));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Starts the game loop.
     *
     * @param cycle whether to cycle through game ticks
     */
    public void start(boolean cycle) {
        if (!engine.tick(render, cycle)) {
            save();
        }
    }

    /**
     * Returns the tiles of the current game world.
     *
     * @return a 2D array of TETile representing the game world
     */
    public TETile[][] getTiles() {
        return engine.getTiles();
    }
}