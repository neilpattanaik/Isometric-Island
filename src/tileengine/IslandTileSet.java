package tileengine;

import core.AnimatedTETile;
import core.Direction;

import java.awt.*;
import java.io.File;
import java.util.Random;

/* @Source. TileSets taken from Itch.io, specifically:
* "https://merchant-shade.itch.io/16x16-puny-world.
* https://route1rodent.itch.io/isometric-sandbox-pixel-world-32x32
* https://scrabling.itch.io/pixel-isometric-tiles
*
* All used solely for educational purposes under variations of the MIT and Creative Commons Licenses!
* */
public class IslandTileSet {
    private static final String ISLAND_TILESET = new File("assets/tiles").getAbsolutePath() + "/";
    private static final String ISLAND_TILESET_ISO = new File("assets/tilesIso").getAbsolutePath() + "/";
    private String base_path = ISLAND_TILESET;

    private boolean isometric = false;

    /* TEMPS */
    public TETile getTemporaryDoorway() {
        return new TETile('≈', Color.blue, Color.black, "Sand Path", base_path + "hallway.png", 8);
    }

    public TETile getTemporaryHallway() {
        return new TETile('≈', Color.blue, Color.black, "Sand Path", base_path + "hallway.png", 3);
    }

    public TETile getTemporaryWall() {
        return new TETile('#', Color.darkGray, Color.black, "Nature Wall", base_path + "wall1.png", 4);
    }

    /* Floors */
    public TETile getFloorTopLeftCorner() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_top_left_corner.png", 2);
    }

    public TETile getFloorLeftMiddleEdge() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_left_middle_edge.png", 2);
    }

    
    public TETile getFloorBottomLeftCorner() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_bottom_left_corner.png", 2);
    }

    
    public TETile getFloorMiddleBottomEdge() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_middle_bottom_edge.png", 2);
    }

    
    public TETile getFloorRightBottomCorner() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_right_bottom_corner.png", 2);
    }

    
    public TETile getFloorRightMiddleEdge() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_right_middle_edge.png", 2);
    }

    
    public TETile getFloorRightTopCorner() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_right_top_corner.png", 2);
    }

    
    public TETile getFloorTopMiddleEdge() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_top_middle_edge.png", 2);
    }

    
    public TETile getFloorCenter() {
        return new TETile('.', new Color(128, 192, 128), Color.black, "Island Floor", base_path + "floor_center.png", 2);
    }
    public TETile getFloor() {
        return getFloorCenter(); // Generic Floor
    }

    /* Walls */
    public TETile getWall(Random rand) {
        int k = rand.nextInt(1, 5);
        return new TETile('#', Color.darkGray, Color.black, "Nature Wall", base_path + "wall" + String.valueOf(k) + ".png", 4);
    }

    /* Nothing Tiles */
    public TETile getUnused() {
        return new TETile('U', Color.black, Color.black, "Water (Nothing)", base_path + "background.png", 0);
    }
    
    public TETile getNothing(Random rand) {
        int k = rand.nextInt(1, 5);
        return new TETile(' ', Color.black, Color.black, "Water (Nothing)", base_path + "nothing" + String.valueOf(k) + ".png", 1);
    }

    /* Bridges and Staircases */
    public TETile getBridge(Direction dir) {
        String desc;
        desc = "Island Entryway";
        if (dir.equals(Direction.LEFT)) {
            return new TETile('<', Color.blue, Color.black, desc, base_path + "hallDoorLeft.png", 3);
        } else if (dir.equals(Direction.RIGHT)) {
            return new TETile('>', Color.blue, Color.black, desc, base_path + "hallDoorRight.png", 3);
        } else if (dir.equals(Direction.DOWN)) {
            return new TETile('_', Color.blue, Color.black, desc, base_path + "hallDoorDown.png", 3);
        } else {
            return new TETile('^', Color.blue, Color.black, desc, base_path + "hallDoorUp.png", 3);
        }
    }

    /* Pathway Tiles */

    public TETile getDoorwayPath(int[] dir) {
        return new TETile('≈', Color.blue, Color.black, "Sand Path", base_path + "hallway.png", 8);
    }



    /* Switch to Isometric TileSet */
    public void switchTileImages(boolean isometric, TETile[][] worldTiles) {
        this.isometric = isometric;

        if (isometric) {
            this.base_path = ISLAND_TILESET_ISO;
        } else {
            this.base_path = ISLAND_TILESET;
        }

        for (int i = 0; i < worldTiles.length; i++) {
            for (int j = 0; j < worldTiles[0].length; j++) {
                worldTiles[i][j] = updateTileImage(worldTiles[i][j]);
            }
        }
    }

    /* Player Tiles handle their own images. Returns the path to the player SpriteSheet */
    public String getPlayerSpriteSheetPath() {
        return base_path + "player.png";
    }

    /** Replace image of a TETile. Used when switching to/from isometric rendering.
     */
    private TETile updateTileImage(TETile tile) {
        if (tile instanceof AnimatedTETile) {
            tile = ((AnimatedTETile) tile).getPrevTile();
        }
        String path = tile.getFilePath();
        if (path != null) {
            String[] directories = path.split(System.getProperty("file.separator"));
            tile.changeFilePath(base_path + directories[directories.length - 1]);
        }
        return tile;
    }
}


