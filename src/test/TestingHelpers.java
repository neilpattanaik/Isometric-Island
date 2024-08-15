package test;

import tileengine.TETile;

public class TestingHelpers {

    /**
     * Checks the encapsulation of a walkable tile in the world.
     * Ensures that a walkable tile is not adjacent to a tile that cannot touch walkable tiles.
     *
     * @param world the 2D array representing the world
     * @param i the x-coordinate of the tile
     * @param j the y-coordinate of the tile
     * @param seed the seed used for world generation
     */
    static void checkEncapsulation(TETile[][] world, int i, int j, long seed) {
        int width = world.length;
        int height = world[0].length;

        if (i > 0 && world[i - 1][j].cannotTouchWalkable()) {
            printError("below", i, j, seed, world[i][j], world[i - 1][j]);
        } else if (i < width - 1 && world[i + 1][j].cannotTouchWalkable()) {
            printError("to right", i, j, seed, world[i][j], world[i + 1][j]);
        } else if (j > 0 && world[i][j - 1].cannotTouchWalkable()) {
            printError("left", i, j, seed, world[i][j], world[i][j - 1]);
        } else if (j < height - 1 && world[i][j + 1].cannotTouchWalkable()) {
            printError("above", i, j, seed, world[i][j], world[i][j + 1]);
        }
    }

    /**
     * Prints an error message indicating a tile is not encapsulated.
     *
     * @param direction the direction of the adjacent tile
     * @param i the x-coordinate of the tile
     * @param j the y-coordinate of the tile
     * @param seed the seed used for world generation
     * @param currentTile the current tile
     * @param adjacentTile the adjacent tile that cannot touch walkable tiles
     */
    private static void printError(String direction, int i, int j, long seed, TETile currentTile, TETile adjacentTile) {
        System.out.printf("Tile at %d, %d is not encapsulated. Tile %s is cannotTouchWalkable.%n", i, j, direction);
        System.out.printf("Seed: %d%n", seed);
        System.out.printf("i,j is of type %s%n", currentTile.getDescription());
        System.out.printf("adjacent tile is of type %s%n", adjacentTile.getDescription());
        System.exit(0);
    }

    /**
     * Creates a UnionFind data structure for the walkable tiles in the world.
     *
     * @param width the width of the world
     * @param height the height of the world
     * @param world the 2D array representing the world
     * @return the UnionFind data structure representing the connectivity of walkable tiles
     */
    static UnionFind getUnionFind(int width, int height, TETile[][] world) {
        UnionFind uf = new UnionFind(width * height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int index = i * height + j;

                if (world[i][j].isWalkable()) {
                    if (i > 0 && world[i - 1][j].isWalkable()) {
                        uf.union(index, (i - 1) * height + j);
                    }
                    if (i < width - 1 && world[i + 1][j].isWalkable()) {
                        uf.union(index, (i + 1) * height + j);
                    }
                    if (j > 0 && world[i][j - 1].isWalkable()) {
                        uf.union(index, i * height + j - 1);
                    }
                    if (j < height - 1 && world[i][j + 1].isWalkable()) {
                        uf.union(index, i * height + j + 1);
                    }
                }
            }
        }
        return uf;
    }

    /**
     * Finds the first walkable group in the world using the UnionFind data structure.
     *
     * @param world the 2D array representing the world
     * @param uf the UnionFind data structure representing the connectivity of walkable tiles
     * @return the component identifier of the first walkable group
     */
    static int getFirstWalkableGroup(TETile[][] world, UnionFind uf) {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j].isWalkable()) {
                    return uf.find(i * world[0].length + j);
                }
            }
        }
        return 0;
    }

    /**
     * Gets the number of walkable neighbors for a given tile in the world.
     *
     * @param world the 2D array representing the world
     * @param i1 the x-coordinate of the tile
     * @param j the y-coordinate of the tile
     * @return the number of walkable neighbors
     */
    public static int getNumWalkableNeighbors(TETile[][] world, int i1, int j) {
        int width = world.length;
        int height = world[0].length;

        int numWalkableNeighbors = 0;
        if (i1 > 0 && world[i1 - 1][j].isWalkable()) {
            numWalkableNeighbors++;
        }
        if (i1 < width - 1 && world[i1 + 1][j].isWalkable()) {
            numWalkableNeighbors++;
        }
        if (j > 0 && world[i1][j - 1].isWalkable()) {
            numWalkableNeighbors++;
        }
        if (j < height - 1 && world[i1][j + 1].isWalkable()) {
            numWalkableNeighbors++;
        }
        return numWalkableNeighbors;
    }
}