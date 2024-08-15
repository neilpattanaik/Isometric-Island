package tileengine;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TERenderer {
    private static final int TILE_SIZE = 16;

    protected int tileWidth = TILE_SIZE;
    protected int tileHeight = TILE_SIZE;

    protected final int CANVAS_WIDTH = 1440;
    protected final int CANVAS_HEIGHT = 752;

    protected int x_Offset;
    protected int y_Offset;

    protected int numXTiles;
    protected int numYTiles;

    protected boolean centered = true;

    public void initialize(int w, int h) {
        this.numXTiles = w;
        this.numYTiles = h;

        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        resetFont();
        StdDraw.setXscale(0, CANVAS_WIDTH);
        StdDraw.setYscale(0, CANVAS_HEIGHT);

        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
    }

    public void renderFrame(TETile[][] world, Point playerPos) {
        handlePanning();
        if (centered) { centerOnPlayer(playerPos); }

        ensureWithinBounds();

        StdDraw.clear(new Color(0, 0, 0));
        drawTiles(world);
        findMouseHoveredTile(world);

        StdDraw.show();
    }

    private void centerOnPlayer(Point playerPos) {
        x_Offset = CANVAS_WIDTH / 2 - (playerPos.x * TILE_SIZE);
        y_Offset = CANVAS_HEIGHT / 2 - (playerPos.y * TILE_SIZE);
    }

    public void drawTiles(TETile[][] world) {
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x * TILE_SIZE + x_Offset, y * TILE_SIZE + y_Offset);
            }
        }
    }

    /* @Source ChatGPT wrote the boilerplate code for this method */
    protected void handlePanning() {
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
            x_Offset += 10;
            centered = false;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
            x_Offset -= 10;
            centered = false;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
            y_Offset -= 10;
            centered = false;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
            y_Offset += 10;
            centered = false;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_P)) {
            centered = true;
        }

    }

    /* @Source ChatGPT was consulted for advice while writing this method  */
    private void ensureWithinBounds() {
        int maxX = Math.max(0, (this.numXTiles * tileWidth) - CANVAS_WIDTH);
        int maxY = Math.max(0, (this.numYTiles * tileHeight) - CANVAS_HEIGHT);

        if (x_Offset < -maxX) x_Offset = -maxX;
        if (x_Offset > 0) x_Offset = 0;

        if (y_Offset < -maxY) y_Offset = -maxY;
        if (y_Offset > 0) y_Offset = 0;
    }

    private int[] screenToTileGrid(int screenX, int screenY) {
        int tileX = (screenX - x_Offset) / TILE_SIZE;
        int tileY = (screenY - y_Offset) / TILE_SIZE;

        return new int[]{tileX, tileY};
    }

    private void findMouseHoveredTile(TETile[][] world) {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        int[] tilePos = screenToTileGrid(mouseX, mouseY);

        int tileX = tilePos[0];
        int tileY = tilePos[1];

        if (tileX >= 0 && tileX < world.length && tileY >= 0 && tileY < world[0].length) {
            TETile tile = world[tileX][tileY];
            displayHoveredTileDescription(tile.getDescription());
        }
    }

    /* @Source ChatGPT wrote most of this method  */
    protected void displayHoveredTileDescription(String desc) {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(60, CANVAS_HEIGHT - 20, Math.ceilDiv((desc.length() * TILE_SIZE), 2) + 5, 14);

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(10, CANVAS_HEIGHT - 20, desc);
    }

    public void resetFont() {
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE);
        StdDraw.setFont(font);
    }
}
