package test;

/**
 * The UnionFind class implements the union-find data structure, also known as disjoint-set.
 * It supports union and find operations, along with tracking the size of each component.
 * The UnionFind class is used in the world generation tests.
 */
public class UnionFind {
    private int[] id;  // Array to hold the parent links
    private int[] size;  // Array to hold the size of each component

    /**
     * Initializes an empty union-find data structure with N elements.
     * Initially, each element is in its own set.
     *
     * @param N the number of elements
     */
    public UnionFind(int N) {
        id = new int[N];
        size = new int[N];
        for (int i = 0; i < N; i++) id[i] = i;  // Each element is its own parent
        for (int i = 0; i < N; i++) size[i] = 1;  // Each component is initially of size 1
    }

    /**
     * Returns the component identifier for the component containing element i.
     * Uses path compression to flatten the structure, ensuring logarithmic time complexity.
     *
     * @param i the element
     * @return the component identifier for the component containing element i
     */
    public int find(int i) {
        while (i != id[i]) i = id[i];  // Follow links to find the root
        return i;
    }

    /**
     * Merges the set containing element p with the set containing element q.
     * Uses union by size to ensure logarithmic time complexity.
     *
     * @param p one element
     * @param q the other element
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;  // Elements are already in the same set

        // Make smaller root point to larger one to keep tree flat
        if (size[rootP] < size[rootQ]) {
            id[rootP] = rootQ;
            size[rootQ] += size[rootP];
        } else {
            id[rootQ] = rootP;
            size[rootP] += size[rootQ];
        }
    }
}