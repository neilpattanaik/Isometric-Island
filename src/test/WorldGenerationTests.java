package test;

import core.Main;
import org.junit.jupiter.api.Test;
import tileengine.TETile;

import java.util.Random;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * World generation tests.
 */
public class WorldGenerationTests {

    /**
     * Tests the connectivity and encapsulation of walkable tiles in the generated world.
     *
     * @param seed the seed used for world generation
     */
    static void connectivityAndEncapsulationTest(long seed) {
        TETile[][] world = Main.getWorldFromTestingAPI(seed);
        int width = world.length;
        int height = world[0].length;

        UnionFind uf = TestingHelpers.getUnionFind(width, height, world);
        int walkableGroup = TestingHelpers.getFirstWalkableGroup(world, uf);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int group = uf.find(i * height + j);
                if (world[i][j].isWalkable()) {
                    assertWithMessage("All walkable tiles should be connected! Seed: " + seed)
                            .that(group)
                            .isEqualTo(walkableGroup);
                }

                if (world[i][j].isWalkable()) {
                    TestingHelpers.checkEncapsulation(world, i, j, seed);
                }
            }
        }
    }

    /**
     * Runs the connectivity and encapsulation test 1000 times with different seeds.
     */
    @Test
    void runConnectivityAndEncapsulationTest() {
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; i++) {
            connectivityAndEncapsulationTest(rand.nextLong());
        }
    }

    /**
     * Checks for dead-end hallways in the generated world.
     */
    @Test
    void checkForDeadEndHallways() {
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; i++) {
            long seed = rand.nextLong();
            TETile[][] world = Main.getWorldFromTestingAPI(seed);
            int width = world.length;
            int height = world[0].length;

            int[][] numExits = new int[width][height];

            for (int i1 = 0; i1 < width; i1++) {
                for (int j = 0; j < height; j++) {
                    if (world[i1][j].isWalkable()) {
                        int numWalkableNeighbors = TestingHelpers.getNumWalkableNeighbors(world, i1, j);
                        numExits[i1][j] = numWalkableNeighbors;
                    }
                }
            }

            for (int i1 = 0; i1 < width; i1++) {
                for (int j = 0; j < height; j++) {
                    if (world[i1][j].isWalkable() && numExits[i1][j] <= 1) {
                        assertWithMessage("Dead end hallway found! Seed: " + seed)
                                .that(numExits[i1][j])
                                .isNotEqualTo(1);
                    }
                }
            }
        }
    }
}