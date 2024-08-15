package core;

import tileengine.IslandTileSet;
import tileengine.TETile;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * Represents a World with rooms and tiles.
 */

public class World extends Rectangle {
    private static final int MAX_TRIES = 1000;
    private static final int MAX_ROOMS = 50;
    private static final int BOUNDARY_BUFFER = 4;
    private final IslandTileSet islandTileSet;


    private final Rectangle innerBounds;
    private final ArrayList<Rectangle> roomList = new ArrayList<>();
    private final Random rand;
    private final String spread;
    private final String mapShape;
    private final int minRoomDim;
    private int numRooms = 0;
    private int maxRoomDim;
    private int continuePercentage;
    private TETile[][] tiles;


    public World(long seed, int rows, int columns, String spread, String shape, int minRoomDim, int maxRoomDim, String continuation, int continuePercentage, IslandTileSet tileset) {
        super(new Dimension(columns, rows));
        this.rand = new Random(seed);
        this.islandTileSet = tileset;

        this.spread = (spread != null) ? spread : (rand.nextBoolean() ? "packed" : "scattered");
        this.mapShape = (shape != null) ? shape : (rand.nextBoolean() ? "rectangular" : (rand.nextBoolean() ? "circular" : "cube"));

        this.innerBounds = this.getBounds();
        innerBounds.grow(-BOUNDARY_BUFFER, -BOUNDARY_BUFFER);

        this.minRoomDim = (minRoomDim != -1) ? minRoomDim : rand.nextInt(5, 8);
        this.maxRoomDim = (maxRoomDim != -1) ? maxRoomDim : this.minRoomDim + rand.nextInt(4, 8);

        if (continuation != null) {
            switch (continuation) {
                case "straight" -> this.continuePercentage = 90;
                case "semi-straight" -> this.continuePercentage = 50;
                case "arbitrary" -> this.continuePercentage = 10;
                case "custom" -> this.continuePercentage = continuePercentage;
            }
        } else {
            this.continuePercentage = rand.nextInt(50, 90);
        }

        initializeTiles();
        setUpRooms();
        placeDoors();
        createPathways();
        removeDeadEnds();
        fixDoors();
        wallOffPaths();
        setAppropriateFloorTiles();
        roomCleaner();
        wallOffPaths();
    }

    /**
     * Initializes the tiles array and applies a mask based on the map shape.
     * <p>
     * This method performs the following steps:
     * 1. Initializes the `tiles` array with dimensions `width` x `height`.
     * 2. Fills each row of the `tiles` array with the "nothing" tile from the `islandTileSet`.
     * 3. Applies a mask to the `tiles` array based on the `mapShape` value.
     */
    private void initializeTiles() {
        // Initialize the tiles array with the specified width and height
        this.tiles = new TETile[this.width][this.height];

        // Fill each row of the tiles array with the "nothing" tile
        for (int i = 0; i < this.width; i++) {
            Arrays.fill(this.tiles[i], islandTileSet.getNothing(rand));
        }

        // Apply a mask to the tiles array based on the map shape
        switch (mapShape) {
            case "rectangular" -> maskRectangular();
            case "circular" -> maskCircular();
            case "cube" -> maskCube();
        }
    }

    /**
     * Masks the tiles array to create a rectangular map shape.
     * <p>
     * This method iterates over the entire tiles array and sets tiles outside the inner bounds
     * to the "unused" tile from the island tile set.
     */
    private void maskRectangular() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                if (!innerBounds.contains(i, j)) {
                    this.tiles[i][j] = islandTileSet.getUnused();
                }
            }
        }
    }

    /**
     * Masks the tiles array to create a circular map shape.
     * <p>
     * This method calculates a radius based on the smaller dimension of the map and sets tiles
     * outside this radius to the "unused" tile from the island tile set.
     */
    private void maskCircular() {
        int radius = Math.min(this.height, this.width) / 2 - 2;

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                if (Point.distance(i, j, getCenterX(), getCenterY()) >= radius) {
                    this.tiles[i][j] = islandTileSet.getUnused();
                }
            }
        }
    }

    /**
     * Masks the tiles array to create a cubic map shape.
     * <p>
     * This method creates a cubic boundary and sets tiles outside this boundary
     * to the "unused" tile from the island tile set.
     */
    private void maskCube() {
        Rectangle cubicBounds = new Rectangle(height - 6, height - 6);
        cubicBounds.setLocation((int) getCenterX() - height / 2, 4);

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                if (!cubicBounds.contains(i, j)) {
                    this.tiles[i][j] = islandTileSet.getUnused();
                }
            }
        }
    }

    /**
     * Sets up the rooms in the world based on the spread type.
     * <p>
     * This method determines the room arrangement strategy based on the spread value.
     * It either scatters the rooms or packs them closely together.
     */
    private void setUpRooms() {
        switch (this.spread) {
            case "scattered" -> scatterRooms();
            case "packed" -> packRooms();
        }
    }

    /**
     * Packs rooms into the world.
     * <p>
     * This method attempts to place new rooms into the world until the maximum number of rooms (MAXROOMS)
     * or the maximum number of tries (MAXTRIES) is reached. If the maximum room dimension (maxRoomDim)
     * is greater than the minimum room dimension (minRoomDim) plus one, it reduces the maxRoomDim by one
     * and recursively calls itself to pack more rooms.
     */
    private void packRooms() {
        for (int count = 0; numRooms < MAX_ROOMS && count < MAX_TRIES; count++) {
            if (!placeNewRoom()) {
                count++;
            }
        }

        if (maxRoomDim > minRoomDim + 1) {
            maxRoomDim -= 1;
            packRooms();
        }
    }

    /**
     * Scatters rooms into the world.
     * <p>
     * This method attempts to place new rooms into the world until the maximum number of rooms (MAXROOMS)
     * or the maximum number of tries (MAXTRIES) is reached. If a room cannot be placed, the count is incremented.
     */
    private void scatterRooms() {
        for (int count = 0; numRooms < MAX_ROOMS && count < MAX_TRIES; count++) {
            if (!placeNewRoom()) {
                count++;
            }
        }
    }

    /**
     * Places a new room into the world.
     * <p>
     * This method creates a new room and checks if it can be placed in the world. If the room can be placed,
     * it fills the room with floor tiles and adds it to the room list. The number of rooms is incremented and
     * the method returns true. If the room cannot be placed, the method returns false.
     *
     * @return true if the room was placed, false otherwise
     */
    private Boolean placeNewRoom() {
        Rectangle room = roomBuilder();

        if (!canPlace(room)) {
            return false;
        }

        int minX = (int) room.getMinX();
        int minY = (int) room.getMinY();
        int maxX = (int) room.getMaxX();
        int maxY = (int) room.getMaxY();

        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                this.tiles[i][j] = (i == minX || i == maxX || j == minY || j == maxY) ? islandTileSet.getWall(rand) : islandTileSet.getFloor();
            }
        }
        roomList.add(room);
        this.numRooms++;
        return true;
    }

    /**
     * Builds a new room with random dimensions and location within the inner bounds of the world.
     * <p>
     * This method creates a new Room, assigns it random dimensions within the specified
     * minimum and maximum room dimensions, and places it at a random location within the inner bounds
     * of the world, ensuring that hallways can span from any side of the room while meeting the boundary requirements.
     *
     * @return A Room  with random dimensions and location.
     */
    private Rectangle roomBuilder() {
        Rectangle room = new Rectangle();
        Dimension roomDim = new Dimension();
        roomDim.height = rand.nextInt(this.minRoomDim, this.maxRoomDim + 1);
        roomDim.width = rand.nextInt(this.minRoomDim, this.maxRoomDim + 1);
        room.setSize(roomDim);

        Rectangle innerBounds = this.innerBounds.getBounds();
        innerBounds.grow(-2, -2); // allow hallways to span from any side of the room while meeting the boundary req
        room.setLocation(rand.nextInt((int) innerBounds.getMaxX() - roomDim.width), rand.nextInt((int) innerBounds.getMaxY() - roomDim.height));
        return room;
    }


    /**
     * Places doors in the world.
     * <p>
     * This method iterates over each room in the room list and finds potential door positions for each room.
     * It shuffles the list of door positions and randomly places doors in the world. Attempts to place
     * between 1 and 4 doors in each room.
     */
    private void placeDoors() {
        for (Rectangle room : roomList) {
            List<Object[]> doorPositions = findDoorPositions(room);
            Collections.shuffle(doorPositions, rand);

            int doorsToPlace = rand.nextInt(1, 2);
            Set<Direction> existingDirections = new HashSet<>();

            for (int i = 0; i < doorsToPlace && !doorPositions.isEmpty(); ) {
                Object[] doorAndDirection = doorPositions.removeFirst();
                Point doorwayPos = (Point) doorAndDirection[0];
                Direction dir = (Direction) doorAndDirection[1];

                if (!existingDirections.contains(dir)) {
                    tiles[doorwayPos.x][doorwayPos.y] = islandTileSet.getTemporaryDoorway(); // TODO: update tile

                    Point nPos = Direction.translate(doorwayPos, dir);
                    tiles[nPos.x][nPos.y] = islandTileSet.getBridge(dir.opposite());
                    existingDirections.add(dir);
                }
            }
        }
    }


    /**
     * Finds potential door positions for a given room.
     * <p>
     * This method checks the north, south, east, and west walls of the room to find positions
     * where doors can be placed. It avoids placing doors on the corners by offsetting the checks.
     *
     * @param room The room for which to find door positions.
     * @return A list of points and directions, where the points represent the DOORWAY positions,
     * and the directions represent the direction to backtrack in order to align with the room wall tiles (for bridge
     * placement).
     */
    private List<Object[]> findDoorPositions(Rectangle room) {
        List<Object[]> doorPositions = new LinkedList<>();

        int minX = (int) room.getMinX();
        int minY = (int) room.getMinY();
        int maxX = (int) room.getMaxX();
        int maxY = (int) room.getMaxY();

        // Check north and south walls. Offset by 2 to avoid placing doors on corners.
        for (int i = minX + 2; i < maxX - 2; i++) {
            boolean added = false;
            if (tiles[i][minY - 1].isNothing()) {
                doorPositions.add(new Object[]{new Point(i, minY - 1), Direction.UP}); // We have to backtrack to correctly place the bridge
                added = true;
            }
            if (tiles[i][maxY + 1].isNothing()) {
                doorPositions.add(new Object[]{new Point(i, maxY + 1), Direction.DOWN});
                added = true;
            }
            if (added) i++;
        }

        // Check east and west walls
        for (int j = minY + 2; j < maxY - 2; j++) {
            boolean added = false;
            if (tiles[minX - 1][j].isNothing()) {
                doorPositions.add(new Object[]{new Point(minX - 1, j), Direction.RIGHT});
                added = true;
            }
            if (tiles[maxX + 1][j].isNothing()) {
                doorPositions.add(new Object[]{new Point(maxX + 1, j), Direction.LEFT});
                added = true;
            }
            if (added) j++;
        }
        return doorPositions;
    }

    /**
     * Creates pathways in the world.
     * <p>
     * This method iterates over the tiles array and starts tunneling from positions that are either empty or doors.
     * It uses a step of 2 to ensure that pathways are created at every other tile.
     */
    private void createPathways() {
        for (int i = 1; i < this.width - 1; i += 2) {
            for (int j = 1; j < this.height - 1; j += 2) {
                if (tiles[i][j].isNothing() || tiles[i][j].isDoor()) {
                    tunnel(i, j);
                }
            }
        }
    }

    /**
     * Creates tunnels in the world starting from a given position.
     * <p>
     * This method uses a stack-based depth-first search (DFS) algorithm to create tunnels.
     * It randomly selects directions to tunnel and ensures that the tunnels do not overlap
     * with existing structures or go out of bounds.
     *
     * @param startX The starting x-coordinate for the tunnel.
     * @param startY The starting y-coordinate for the tunnel.
     */
    private void tunnel(int startX, int startY) {
        Stack<Object[]> nextToVisit = new Stack<>();
        nextToVisit.push(new Object[]{new Point(startX, startY), null});
        boolean biasNextVisit = false;

        while (!nextToVisit.isEmpty()) {
            Point pos = (Point) nextToVisit.peek()[0];
            Direction lastDir = (Direction) nextToVisit.pop()[1];

            for (Direction dir : Direction.getShuffledDirections(rand)) {
                if (biasNextVisit && dir.equals(lastDir)) continue;

                Point nPos = Direction.translate(pos, dir, 2);

                if (canTunnel(nPos)) {
                    tiles[pos.x][pos.y].visit();
                    makePath(pos, dir, 2);
                    nextToVisit.add(new Object[]{nPos, dir});
                }
            }

            if (biasNextVisit) {
                Point nPos = Direction.translate(pos, lastDir, 2);

                if (canTunnel(nPos)) {
                    tiles[pos.x][pos.y].visit();
                    makePath(pos, lastDir, 2);
                    nextToVisit.add(new Object[]{nPos, lastDir});
                }
            }

            biasNextVisit = rand.nextInt(100) < continuePercentage;
        }
    }

    /**
     * Checks if a tunnel can be created at the given position.
     * <p>
     * This method ensures that the position is within bounds and that the tile at the position
     * is either a door that has not been visited or an empty tile that has not been visited.
     *
     * @param pos The position to check.
     * @return true if a tunnel can be created at the position, false otherwise.
     */
    private boolean canTunnel(Point pos) {
        if (!innerBounds.contains(pos)) return false;

        if (tiles[pos.x][pos.y].isDoor() && !tiles[pos.x][pos.y].beenVisited()) {
            return true;
        }

        return tiles[pos.x][pos.y].isNothing() && !tiles[pos.x][pos.y].beenVisited();
    }

    /**
     * Creates a path of a specified length in a given direction from a starting position.
     * <p>
     * This method updates the tiles along the path to be temporary hallways, except for door tiles.
     *
     * @param pos    The starting position for the path.
     * @param dir    The direction in which to create the path.
     * @param length The length of the path to create.
     */
    private void makePath(Point pos, Direction dir, int length) {
        while (length > 0) {
            pos = Direction.translate(pos, dir);
            if (!tiles[pos.x][pos.y].isDoor()) {
                tiles[pos.x][pos.y] = islandTileSet.getTemporaryHallway();
            }
            length--;
        }
    }

    private void hallFixer() {
        removeDeadEnds();
        fixDoors();
    }

    /**
     * Checks if a room can be placed in the world.
     * <p>
     * This method verifies if the given room can be placed within the world boundaries and does not overlap
     * with existing rooms or unused tiles.
     *
     * @param room The room to check for placement.
     * @return true if the room can be placed, false otherwise.
     */
    private Boolean canPlace(Rectangle room) {
        Rectangle outBounds = room.getBounds();
        outBounds.grow(BOUNDARY_BUFFER, BOUNDARY_BUFFER);

        if (!this.contains(outBounds)) {
            return false;
        }

        for (Rectangle o : roomList) {
            Rectangle outerBounds = o.getBounds();
            outerBounds.grow(BOUNDARY_BUFFER, BOUNDARY_BUFFER);
            if (room.intersects(outerBounds)) {
                return false;
            }
        }

        for (int i = (int) room.getMinX(); i <= room.getMaxX(); i++) {
            for (int j = (int) room.getMinY(); j <= room.getMaxY(); j++) {
                if (this.tiles[i][j].isUnused()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Removes a door and the associated bridge.
     * <p>
     * This method removes a door tile at the specified coordinates and replaces it with a "nothing" tile.
     * It also updates adjacent hallway tiles to be wall tiles.
     *
     * @param x The x-coordinate of the door to remove.
     * @param y The y-coordinate of the door to remove.
     */
    private void removeDoor(int x, int y) {
        assert tiles[x][y].isDoor();
        tiles[x][y] = islandTileSet.getNothing(rand);
        Point pos = new Point(x, y);
        for (Direction dir : Direction.values()) {
            Point nPos = Direction.translate(pos, dir);
            if (tiles[nPos.x][nPos.y].isHallway()) {
                tiles[nPos.x][nPos.y] = islandTileSet.getWall(rand);
            }
        }
    }

    /**
     * Removes dead-end hallways from the world.
     * <p>
     * This method iterates over the tiles array and removes dead-end hallways and doors.
     * It continues to remove dead-ends until no more changes are made.
     */
    private void removeDeadEnds() {
        boolean changed;
        do {
            changed = false;
            for (int i = 1; i < width - 1; i++) {
                for (int j = 1; j < height - 1; j++) {
                    if ((tiles[i][j].isHallway() || tiles[i][j].isDoor()) && isDeadEnd(i, j)) {
                        if (tiles[i][j].isDoor()) {
                            removeDoor(i, j);
                        } else {
                            tiles[i][j] = islandTileSet.getNothing(rand);
                        }
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    /**
     * Checks if a tile is a dead-end.
     * <p>
     * This method determines if a tile at the given coordinates is a dead-end by counting the number of exits.
     *
     * @param x The x-coordinate of the tile to check.
     * @param y The y-coordinate of the tile to check.
     * @return true if the tile is a dead-end, false otherwise.
     */
    private boolean isDeadEnd(int x, int y) {
        int exits = 0;
        Point currentPos = new Point(x, y);
        for (Direction dir : Direction.values()) {
            Point nPos = Direction.translate(currentPos, dir);
            if (tiles[nPos.x][nPos.y].isHallway() || tiles[nPos.x][nPos.y].isFloor() || tiles[nPos.x][nPos.y].isDoor()) {
                exits++;
            }
        }
        return exits <= 1;
    }

    /**
     * Fixes door tiles in the world.
     * <p>
     * This method iterates over each room and updates door tiles based on their adjacency to hallways.
     * If a door is not adjacent to a hallway, it is replaced with a wall tile.
     */
    private void fixDoors() {
        for (Rectangle room : roomList) {
            for (Object[] positionObj : findDoorPositions(room)) {
                Point pos = (Point) positionObj[0];
                Direction dir = (Direction) positionObj[1];
                if (tiles[pos.x][pos.y].isDoor()) {
                    if (isHallwayAdjacent(pos.x, pos.y)) {
                        tiles[pos.x][pos.y] = islandTileSet.getDoorwayPath(null);
                    } else {
                        tiles[pos.x][pos.y] = islandTileSet.getWall(rand);
                    }
                }
            }
        }
    }

    /**
     * Checks if a hallway is adjacent to a given tile.
     * <p>
     * This method determines if any of the tiles adjacent to the given coordinates are hallways.
     *
     * @param x The x-coordinate of the tile to check.
     * @param y The y-coordinate of the tile to check.
     * @return true if a hallway is adjacent to the tile, false otherwise.
     */
    private boolean isHallwayAdjacent(int x, int y) {
        for (Direction dir : Direction.values()) {
            Point nPos = Direction.translate(new Point(x, y), dir);
            if (tiles[nPos.x][nPos.y].isHallway()) {
                return true;
            }
        }
        return false;
    }

    /**
 * Helper method to determine if a tile should be walled off.
 * <p>
 * This method checks if a tile at the given coordinates should be converted to a wall tile.
 * It ensures the tile is within bounds, not a hallway, floor, or door, and is adjacent to a hallway or within a room.
 *
 * @param x The x-coordinate of the tile to check.
 * @param y The y-coordinate of the tile to check.
 * @return true if the tile should be walled off, false otherwise.
 */
private boolean wallOffHelper(int x, int y) {
    Rectangle outerBounds = this.getBounds();
    outerBounds.grow(-1, -1);
    if (!outerBounds.contains(x, y) || tiles[x][y].isHallway() || tiles[x][y].isFloor() || tiles[x][y].isDoor()) {
        return false;
    }

    for (Rectangle room : roomList) {
        if (room.getBounds().contains(x, y)) {
            return true;
        }
    }

    for (int i = x - 1; i <= x + 1; i++) {
        for (int j = y - 1; j <= y + 1; j++) {
            if (tiles[i][j].isHallway()) {
                return true;
            }
        }
    }
    return false;
}

/**
 * Walls off paths in the world.
 * <p>
 * This method iterates over the tiles array and converts tiles that should be walled off
 * to temporary wall tiles.
 */
public void wallOffPaths() {
    for (int i = 1; i < width; i++) {
        for (int j = 1; j < height; j++) {
            if (wallOffHelper(i, j)) {
                tiles[i][j] = islandTileSet.getTemporaryWall();
            }
        }
    }
}

/**
 * Sets appropriate floor tiles in the world.
 * <p>
 * This method iterates over the tiles array and updates floor tiles to their appropriate types
 * based on their adjacency to walls and hallways.
 */
public void setAppropriateFloorTiles() {
    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            if (tiles[x][y].isFloor()) {
                tiles[x][y] = getFloorTileType(x, y);
            }
        }
    }
}

    public Point getRandomRoomCoords() {
        Rectangle rm = roomList.get(rand.nextInt(0, roomList.size()));
        return new Point(rand.nextInt((int) rm.getMinX() + 2,
                (int) rm.getMaxX() - 1),
                rand.nextInt((int) rm.getMinY() + 2,
                        (int) rm.getMaxY() - 1));
        // Avoid corners
    }

    private void roomCleaner() {
        Queue<Rectangle> deleteQueue = new LinkedList<>();
        for (Rectangle room : roomList) {
            boolean door = false;

            int minX = (int) room.getMinX();
            int minY = (int) room.getMinY();
            int maxX = (int) room.getMaxX();
            int maxY = (int) room.getMaxY();

            // Check north and south walls
            for (int x = minX + 1; x < maxX; x++) {
                if (tiles[x][minY - 1].isDoor()) {
                    door = true;
                    break;
                }
                if (tiles[x][maxY + 1].isDoor()) {
                    door = true;
                    break;
                }
            }

            // Check east and west walls
            for (int y = minY + 1; y < maxY; y++) {
                if (tiles[minX - 1][y].isDoor()) {
                    door = true;
                    break;
                }
                if (tiles[maxX + 1][y].isDoor()) {
                    door = true;
                    break;
                }
            }
            // Door got cutoff because it was placed in exterior boundary.
            if (!door) {
                for (int i = minX; i < maxX + 1; i++) {
                    for (int j = minY; j < maxY + 1; j++) {
                        tiles[i][j] = islandTileSet.getNothing(rand);
                    }
                }
                deleteQueue.add(room);
            }
        }
        for (Rectangle room : deleteQueue) {
            roomList.remove(room);
        }
    }

    private TETile getFloorTileType(int x, int y) {
        boolean top = y + 1 < height && (tiles[x][y + 1].isWall() || tiles[x][y + 1].isHallway());
        boolean bottom = y - 1 >= 0 && (tiles[x][y - 1].isWall() || tiles[x][y - 1].isHallway());
        boolean left = x - 1 >= 0 && (tiles[x - 1][y].isWall() || tiles[x - 1][y].isHallway());
        boolean right = x + 1 < width && (tiles[x + 1][y].isWall() || tiles[x + 1][y].isHallway());

        if (top && left) {
            return islandTileSet.getFloorTopLeftCorner();
        } else if (bottom && left) {
            return islandTileSet.getFloorBottomLeftCorner();
        } else if (bottom && right) {
            return islandTileSet.getFloorRightBottomCorner();
        } else if (top && right) {
            return islandTileSet.getFloorRightTopCorner();
        } else if (top) {
            return islandTileSet.getFloorTopMiddleEdge();
        } else if (bottom) {
            return islandTileSet.getFloorMiddleBottomEdge();
        } else if (left) {
            return islandTileSet.getFloorLeftMiddleEdge();
        } else if (right) {
            return islandTileSet.getFloorRightMiddleEdge();
        } else {
            return islandTileSet.getFloorCenter();
        }
    }

    public void switchViews(boolean isometric) {
        this.islandTileSet.switchTileImages(isometric, tiles);
    }

    public TETile[][] getTiles() {
        return tiles;
    }

}