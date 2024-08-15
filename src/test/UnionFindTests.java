package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class contains unit tests for the UnionFind data structure.
 */
public class UnionFindTests {

    /**
     * Tests the initialization of the UnionFind data structure.
     * Ensures that each element is its own parent initially.
     */
    @Test
    void unionFindInitialization() {
        UnionFind uf = new UnionFind(10);
        for (int i = 0; i < 10; i++) {
            assertEquals(i, uf.find(i), "Each element should be its own parent initially");
        }
    }

    /**
     * Tests the union operation of the UnionFind data structure.
     * Ensures that two elements are connected after a union operation.
     */
    @Test
    void unionFindUnion() {
        UnionFind uf = new UnionFind(10);
        uf.union(1, 2);
        assertEquals(uf.find(1), uf.find(2), "Elements 1 and 2 should be connected");
    }

    /**
     * Tests multiple union operations of the UnionFind data structure.
     * Ensures that a chain of elements are connected after multiple union operations.
     */
    @Test
    void unionFindMultipleUnions() {
        UnionFind uf = new UnionFind(10);
        uf.union(1, 2);
        uf.union(2, 3);
        assertEquals(uf.find(1), uf.find(3), "Elements 1, 2, and 3 should be connected");
    }

    /**
     * Tests the union operation with different sizes of sets in the UnionFind data structure.
     * Ensures that elements from different sets are connected after union operations.
     */
    @Test
    void unionFindUnionWithDifferentSizes() {
        UnionFind uf = new UnionFind(10);
        uf.union(1, 2);
        uf.union(3, 4);
        uf.union(1, 4);
        assertEquals(uf.find(2), uf.find(3), "Elements 1, 2, 3, and 4 should be connected");
    }

    /**
     * Tests the union operation with the same element in the UnionFind data structure.
     * Ensures that a union with itself does not change the parent.
     */
    @Test
    void unionFindSelfUnion() {
        UnionFind uf = new UnionFind(10);
        uf.union(1, 1);
        assertEquals(1, uf.find(1), "Union with itself should not change the parent");
    }

    /**
     * Tests the union operation on already connected elements in the UnionFind data structure.
     * Ensures that elements remain connected after redundant union operations.
     */
    @Test
    void unionFindAlreadyConnected() {
        UnionFind uf = new UnionFind(10);
        uf.union(1, 2);
        uf.union(2, 3);
        uf.union(1, 3);
        assertEquals(uf.find(1), uf.find(3), "Elements 1, 2, and 3 should remain connected");
    }

    /**
     * Tests a large number of union operations in the UnionFind data structure.
     * Ensures that all elements are connected in a large union.
     */
    @Test
    void unionFindLargeUnion() {
        UnionFind uf = new UnionFind(1000);
        for (int i = 0; i < 999; i++) {
            uf.union(i, i + 1);
        }
        assertEquals(uf.find(0), uf.find(999), "All elements should be connected in a large union");
    }
}