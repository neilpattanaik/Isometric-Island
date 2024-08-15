package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The IsometricTERenderer class extends the TERenderer class to provide isometric rendering capabilities.
 * It includes methods for handling zooming, panning, and converting between cartesian and isometric coordinates.
 */
public class IsometricTERenderer extends TERenderer {
    private final int ORIGINAL_TILE_HEIGHT = 32;
    private final int ORIGINAL_TILE_WIDTH = 32;
    private int tileWidth;
    private int tileHeight;
    private double zoomFactor = 1.0;

    /**
     * Constructs an IsometricTERenderer with default settings.
     */
    public IsometricTERenderer() {
        super();
    }

    /**
     * Initializes the renderer with the specified width and height.
     *
     * @param w the width of the canvas
     * @param h the height of the canvas
     */
    @Override
    public void initialize(int w, int h) {
        tileWidth = ORIGINAL_TILE_WIDTH;
        tileHeight = ORIGINAL_TILE_HEIGHT;

        super.initialize(w, h);
    }

    /**
     * Renders a frame of the world, centered on the player position.
     * Handles zooming and panning based on user input.
     *
     * @param world the 2D array representing the world
     * @param playerPos the position of the player
     */
    @Override
    public void renderFrame(TETile[][] world, Point playerPos) {
        if (StdDraw.isKeyPressed(KeyEvent.VK_PLUS) || StdDraw.isKeyPressed(KeyEvent.VK_EQUALS)
                || StdDraw.isKeyPressed(KeyEvent.VK_MINUS) || StdDraw.isKeyPressed(KeyEvent.VK_Z)) {
            handleZooming();
        }

        handlePanning();
        if (centered) {
            centerOnPlayer(playerPos);
        }

        verifyOffsets();

        StdDraw.clear(new Color(0, 0, 0));
        drawTiles(world);
        handleCursor(world);
        StdDraw.show();
    }

    /**
     * Draws the tiles of the world in isometric view.
     *
     * @param world the 2D array representing the world
     */
    @Override
    public void drawTiles(TETile[][] world) {
        for (int x = 0; x < numXTiles; x++) {
            for (int y = 0; y < numYTiles; y++) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                int[] isoCoords = cartesianToIsometric(new int[]{x, y});
                world[x][y].draw(isoCoords[0] + x_Offset, isoCoords[1] + y_Offset);
            }
        }
    }

    /**
     * Converts cartesian coordinates to isometric coordinates.
     *
     * @param cartesianCoords the cartesian coordinates
     * @return the isometric coordinates
     */
    private int[] cartesianToIsometric(int[] cartesianCoords) {
        int x = cartesianCoords[0];
        int y = cartesianCoords[1];

        int isoX = (x - y) * tileWidth / 2;
        int isoY = (x + y) * tileHeight / 4;
        return new int[]{isoX, isoY};
    }

    /**
     * Centers the view on the player position.
     *
     * @param playerPos the position of the player
     */
    private void centerOnPlayer(Point playerPos) {
        int[] isoCoords = cartesianToIsometric(new int[]{playerPos.x, playerPos.y});
        x_Offset = CANVAS_WIDTH / 2 - isoCoords[0];
        y_Offset = CANVAS_HEIGHT / 2 - isoCoords[1];
    }

    /**
     * Converts isometric screen coordinates to grid coordinates.
     *
     * @param screenX the x-coordinate on the screen
     * @param screenY the y-coordinate on the screen
     * @return the grid coordinates
     */
    public int[] isometricScreenToGrid(int screenX, int screenY) {
        int isoX = screenX - x_Offset;
        int isoY = screenY - y_Offset;

        int cartX = Math.ceilDiv((isoX / (tileWidth / 2) + isoY / (tileHeight / 4)), 2);
        int cartY = Math.floorDiv((isoY / (tileHeight / 4) - isoX / (tileWidth / 2)), 2);

        return new int[]{cartX, cartY};
    }

    /**
     * Handles the cursor interaction with the world.
     *
     * @param world the 2D array representing the world
     */
    private void handleCursor(TETile[][] world) {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        int[] tilePos = isometricScreenToGrid(mouseX, mouseY);

        int tileX = tilePos[0];
        int tileY = tilePos[1];

        if (tileX >= 0 && tileX < world.length && tileY >= 0 && tileY < world[0].length) {
            TETile tile = world[tileX][tileY];
            displayHoveredTileDescription(tile.getDescription());
        }
    }

    /**
     * Handles zooming based on user input.
     */
    private void handleZooming() {
        if (StdDraw.isKeyPressed(KeyEvent.VK_PLUS) || StdDraw.isKeyPressed(KeyEvent.VK_EQUALS)) { // '+' key
            zoomFactor = Math.min(1.27, zoomFactor * 1.05);
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_MINUS)) { // '-' key
            zoomFactor = Math.max(0.75, zoomFactor / 1.05);
        } else {
            zoomFactor = 1;
        }
        tileWidth = (int) (ORIGINAL_TILE_WIDTH * zoomFactor);
        tileHeight = (int) (ORIGINAL_TILE_HEIGHT * zoomFactor);
        updateOffsets();
    }

    /**
     * Updates the offsets for rendering based on the current zoom factor.
     */
    private void updateOffsets() {
        x_Offset = (CANVAS_WIDTH - numXTiles * tileWidth) / 2;
        y_Offset = (CANVAS_HEIGHT - numYTiles * tileHeight) / 2;
    }

    /**
     * Verifies and adjusts the offsets to ensure the view is within bounds.
     */
    private void verifyOffsets() {
        // Calculate the isometric bounds
        int maxX = (numXTiles - 1) * 32 / 2 - CANVAS_WIDTH / 2;
        int minX = (numYTiles - 1) * 64 / 2 - CANVAS_WIDTH / 4;
        int minY = (numXTiles - 1) * 64 / 4 - CANVAS_HEIGHT / 2;
        int maxY = (numYTiles - 1) * 32 / 4 - CANVAS_HEIGHT;

        if (x_Offset < -maxX) x_Offset = -maxX;
        if (x_Offset > minX) x_Offset = minX;
        if (y_Offset < -minY) y_Offset = -minY;
        if (y_Offset > -maxY) y_Offset = -maxY;
    }
}