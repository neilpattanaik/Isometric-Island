package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

/**
 * The SpriteSheet class manages game graphics and is primarily responsible for taking tilesets
 * and extracting individual tiles/animations. Created to serve as a helper class for using Princeton's STDraw
 * library, which only supports drawing tiles from filepaths (and not from BufferedImage objects).
 */
public class SpriteSheet {
    private static final Stack<File> deletionQueue = new Stack<>();
    private final File dir;
    private final int TILE_WIDTH;
    private final int TILE_HEIGHT;
    private BufferedImage sprites;
    private boolean validSpriteSheet;

    /**
     * Constructs a SpriteSheet with the specified file path and tile dimensions.
     *
     * @param filepath the path to the sprite sheet image file
     * @param tileWidth the width of each tile in the sprite sheet
     * @param tileHeight the height of each tile in the sprite sheet
     */
    public SpriteSheet(String filepath, int tileWidth, int tileHeight) {
        this.TILE_HEIGHT = tileHeight;
        this.TILE_WIDTH = tileWidth;

        try {
            this.sprites = ImageIO.read(new File(filepath));
            this.validSpriteSheet = true;
        } catch (IOException e) {
            this.validSpriteSheet = false;
        }
        this.dir = new File("assets", "temps");
        this.dir.mkdirs();
        this.dir.deleteOnExit();
    }

    /**
     * Clears the deletion queue by deleting all files in the queue.
     */
    public static void clear() {
        while (!deletionQueue.isEmpty()) {
            deletionQueue.pop().delete();
        }
    }

    /**
     * Returns a subimage from the sprite sheet at the specified tile coordinates.
     *
     * @param nx the x-coordinate of the tile
     * @param ny the y-coordinate of the tile
     * @return the subimage representing the tile, or null if the sprite sheet is invalid
     */
    public BufferedImage getSpriteBufferedImage(int nx, int ny) {
        if (!this.validSpriteSheet) {
            return null;
        }
        return sprites.getSubimage(nx * TILE_WIDTH, ny * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
    }

    /**
     * Converts a sprite (subimage) to a file path by saving it as a PNG file.
     *
     * @param sprite the BufferedImage representing the sprite
     * @return the file path of the saved sprite, or null if the sprite sheet is invalid
     * @source ChatGPT helped with this method
     */
    public String convertSpriteToFilePath(BufferedImage sprite) {
        if (!this.validSpriteSheet) {
            return null;
        }

        File spr = new File(this.dir, String.valueOf(sprite.hashCode()));
        try {
            ImageIO.write(sprite, "png", spr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        deletionQueue.add(spr);
        return spr.getAbsolutePath();
    }

    /**
     * Checks if the sprite sheet is valid.
     *
     * @return true if the sprite sheet is valid, false otherwise
     */
    public boolean isValidSpriteSheet() {
        return this.validSpriteSheet;
    }
}