package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


/** Abstract class used to represent a non-static animated TEtile, intended to be moddular enough to
 * support any type of animation (i.e., not just player movement). Much of the logic for this class' implementation
 * (and that of the helper classes Frame and SpriteSheet) was motivated by the StackOverflow post here:
 * /**
 *  * @source Motivated by
 *  * https://gamedev.stackexchange.com/questions/53705/how-can-i-make-a-sprite-sheet-based-animation-system
 *  */
public abstract class AnimatedTETile extends TETile {
    private final SpriteSheet spriteSheet;
    private final int delay;
    private final ArrayList<Frame> frames;
    private final int maxCycles;
    private int totalFrames;
    private int currentFrame;
    private int frameCounter;
    private boolean stopped;
    private int cycleCounter;
    private TETile prevTile;
    private Point pos;

    public AnimatedTETile(char character, Color textColor, Color backgroundColor, String description, int id, String tilesetFilepath,
                          int[][] tileSetCoords, int delay, int tileWidth, int tileHeight, int maxCycles) {
        super(character, textColor, backgroundColor, description, null, id);
        this.spriteSheet = new SpriteSheet(tilesetFilepath, tileWidth, tileHeight);

        this.frames = new ArrayList<>();
        this.frameCounter = 0;
        this.currentFrame = 0;
        this.cycleCounter = 0;
        this.stopped = true;
        this.delay = delay;
        this.maxCycles = maxCycles;

        for (int i = 0; i < tileSetCoords.length; i++) {
            BufferedImage spr = spriteSheet.getSpriteBufferedImage(tileSetCoords[i][0], tileSetCoords[i][1]);
            addFrame(spr);
        }
    }

    /* @Source Chat-GPT was queried and wrote a sizable portion of this method */
    public static BufferedImage overlayImages(TETile tile, BufferedImage overlayImage) {
        // Create a combined image with the same dimensions as the tile's sprite
        BufferedImage combined = new BufferedImage(tile.getSpriteWidth(), tile.getSpriteHeight(), BufferedImage.TYPE_INT_ARGB);

        // Get the graphics context of the combined image
        Graphics2D g = combined.createGraphics();

        // Draw the base tile sprite
        g.drawImage(tile.getSprite(), 0, 0, null);

        // Calculate the center position for the overlay image
        int xOffset = (tile.getSpriteWidth() - overlayImage.getWidth()) / 2;
        int yOffset = (tile.getSpriteHeight() - overlayImage.getHeight()) / 2;

        // Draw the overlay image at the calculated position
        g.drawImage(overlayImage, xOffset, yOffset, null);

        // Dispose of the graphics context
        g.dispose();

        // Return the combined image
        return combined;
    }

    public void start() {
        if (!stopped || frames.isEmpty()) {
            return;
        }
        this.stopped = false;
    }

    public void reset() {
        this.stopped = true;
        this.frameCounter = 0;
        this.currentFrame = 0;
        this.cycleCounter = 0;
    }

    public void switchAnimation(int[][] newAnimation) {
        reset();
        this.frames.clear();
        for (int i = 0; i < newAnimation.length; i++) {
            BufferedImage spr = spriteSheet.getSpriteBufferedImage(newAnimation[i][0], newAnimation[i][1]);
            addFrame(spr);
        }
    }

    private void addFrame(BufferedImage frame) {
        this.frames.add(new Frame(frame));
        this.currentFrame = 0;
        this.totalFrames = this.frames.size();
    }

    @Override
    public BufferedImage getSprite() {
        return frames.get(currentFrame).getFrame();
    }


    // Implementation of this method was heavily influenced by the StackOverflow post linked in the initial class doc.
    public void update() {
        if (!stopped) {
            frameCounter++;

            if (cycleCounter > maxCycles) {
                reset();
            }

            if (frameCounter > delay) {
                frameCounter = 0;
                currentFrame += 1;

                if (currentFrame > totalFrames - 1) {
                    currentFrame = 0;
                    cycleCounter += 1;
                }
            }
        }

    }

    @Override
    public void draw(double x, double y) {
        if (spriteSheet.isValidSpriteSheet()) {
            BufferedImage overlaid = overlayImages(prevTile, getSprite());
            StdDraw.picture(x + 0.5, y + 0.5, spriteSheet.convertSpriteToFilePath(overlaid));
            return;
        }
        super.draw(x, y); // Draw char with standard TETile method if we can't render images
    }

    public TETile getPrevTile() {
        return this.prevTile;
    }

    public Point getPos() {
        return pos;
    }

    public TETile setPos(Point pos, TETile newTile) {
        this.pos = pos;
        this.prevTile = newTile;
        return this.prevTile;
    }

    public void move(Direction dir, TETile[][] world) {
        Point newPos = new Point(pos.x + Direction.getDx(dir), pos.y + Direction.getDy(dir));
        if (canMoveTo(newPos, world)) {
            world[pos.x][pos.y] = prevTile;
            setPos(newPos, world[newPos.x][newPos.y]);
            world[newPos.x][newPos.y] = this;
        }
    }

    private boolean canMoveTo(Point newPos, TETile[][] world) {
        int x = newPos.x;
        int y = newPos.y;
        return x >= 0 && y >= 0 && x < world.length && y < world[0].length && !world[x][y].isWall();
    }

}
