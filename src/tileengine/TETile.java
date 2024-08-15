package tileengine;

import edu.princeton.cs.algs4.StdDraw;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * The TETile object is used to represent a single tile in your world. A 2D array of tiles make up a
 * board, and can be drawn to the screen using the TERenderer class.
 *
 * All TETile objects must have a character, textcolor, and background color to be used to represent
 * the tile when drawn to the screen. You can also optionally provide a path to an image file of an
 * appropriate size (16x16) to be drawn in place of the unicode representation. If the image path
 * provided cannot be found, draw will fallback to using the provided character and color
 * representation, so you are free to use image tiles on your own computer.
 *
 * The provided TETile is immutable, i.e. none of its instance variables can change. You are welcome
 * to make your TETile class mutable, if you prefer.
 */

public class TETile implements Serializable {
    private final char character; // Do not rename character or the autograder will break.
    private final Color textColor;
    private final Color backgroundColor;
    private final String description;
    private String filepath;
    private final int id;
    private boolean visited = false;

    /**
     * Full constructor for TETile objects.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     * @param filepath Full path to image to be used for this tile. Must be correct size (16x16)
     */
    public TETile(char character, Color textColor, Color backgroundColor, String description,
                  String filepath, int id) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = filepath;
        this.id = id;
    }

    public TETile(char character, Color textColor, Color backgroundColor, String description,
                  String filepath, int id, int subID) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = filepath;
        this.id = id;
    }


    /**
     * Draws the tile to the screen at location x, y. If a valid filepath is provided,
     * we draw the image located at that filepath to the screen. Otherwise, we fall
     * back to the character and color representation for the tile.
     *
     * Note that the image provided must be of the right size (16x16). It will not be
     * automatically resized or truncated.
     * @param x x coordinate
     * @param y y coordinate
     */
    public void draw(double x, double y) {
        if (filepath != null) {
            try {
                StdDraw.picture(x + 0.5, y + 0.5, filepath);
                return;
            } catch (IllegalArgumentException e) {
                // Exception happens because the file can't be found. In this case, fail silently
                // and just use the character and background color for the tile.
            }
        }

        StdDraw.setPenColor(backgroundColor);
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
        StdDraw.setPenColor(textColor);
        StdDraw.text(x + 0.5, y + 0.5, Character.toString(character()));
    }

    /** Character representation of the tile. Used for drawing in text mode.
     * @return character representation
     */
    public char character() {
        return character;
    }

    /**
     * Description of the tile. Useful for displaying mouseover text or
     * testing that two tiles represent the same type of thing.
     * @return description of the tile
     */
    public String getDescription() {
        return description;
    }

    public BufferedImage getSprite() {
        try {
            return ImageIO.read(new File(filepath));
        } catch (IOException e) {
            return null;
        }

    }

    /**
     * Checks if two tiles are equal by comparing their IDs.
     * @param o object to compare with
     * @return boolean representing equality
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof TETile otherTile && otherTile.id == this.id);
    }

    public boolean isUnused() {
        return id == 0;
    }

    public boolean isDoor() {return id == 8;}


    public boolean isNothing() {
        return id == 1;
    }

    public boolean isHallway() {
        return id == 3;
    }

    public boolean isFloor() {
        return id == 2;
    }

    public boolean isWall() {
        return id == 4;
    }

    public int getSpriteWidth() {
        BufferedImage sprite = getSprite();
        if (sprite != null) {
            return sprite.getWidth();
        }
        return 16; // Standard, non-Isometric tiles are 16x16. If the tile is Isometrically rendered, this won't matter as sprite cannot be null
    }

    public int getSpriteHeight() {
         BufferedImage sprite = getSprite();
         if (sprite != null) {
             return sprite.getHeight();
         }
         return 16;
    }

    public String getFilePath() {
        return filepath;
    }

    public void changeFilePath(String filepath) {
        this.filepath = filepath;
    }

    public boolean beenVisited() {
        return visited;
    }

    public void visit() {
        visited = true;
    }

    public boolean isPlayer() {
        return id == 15;
    }

    public boolean isWalkable() {

        return isDoor() || isHallway() || isFloor() || isPlayer();

    }

    public boolean cannotTouchWalkable() {
        return isNothing() || isUnused();
    }

}
