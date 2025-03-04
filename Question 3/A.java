import java.util.*;

public class MinNetworkCost {
    // Union-Find data structure to track connected components
    static class UnionFind {
        int[] parent, rank;

        // Initialize each device as its own parent with rank 0
        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        // Find the root of a device, with path compression
        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        // Union two devices, using rank to keep tree balanced
        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }

    public static int minCostToConnect(int n, int[] modules, int[][] connections) {
        // Create a list to hold all edges, including module costs as self-connections
        List<int[]> edges = new ArrayList<>();

        // Add module costs as edges (like installing a module on device i connects it to itself)
        for (int i = 0; i < n; i++) {
            edges.add(new int[]{i, i, modules[i]});
        }

        // Add all connection edges from the connections array
        for (int[] conn : connections) {
            edges.add(new int[]{conn[0] - 1, conn[1] - 1, conn[2]}); // Convert to 0-based indexing
        }

        // Sort edges by cost to process cheapest first
        edges.sort((a, b) -> a[2] - b[2]);

        // Initialize Union-Find for n devices
        UnionFind uf = new UnionFind(n);

        // Track total cost and number of components connected
        int totalCost = 0;
        int components = n; // Start with n isolated devices

        // Process each edge in sorted order
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int cost = edge[2];

            // If u and v are not yet connected, use this edge/module
            if (uf.find(u) != uf.find(v)) {
                uf.union(u, v);
                totalCost += cost;
                components--; // One less isolated component
            }
            // Special case: module edge (u == v), always use if it connects an isolated device
            else if (u == v && components > 1) {
                totalCost += cost;
                components--; // Module connects this device to the network
            }
        }

        // Return the total minimum cost
        return totalCost;
    }

    public static void main(String[] args) {
        // Test case: n = 3, modules = [1, 2, 2], connections = [[1, 2, 1], [2, 3, 1]]
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};
        // Calculate and print the minimum cost
        int result = minCostToConnect(n, modules, connections);
        System.out.println("n=3, modules=[1,2,2], connections=[[1,2,1],[2,3,1]]: " + result);
    }
}