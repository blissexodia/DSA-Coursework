import java.util.*;

public class PackageCollector {
    public static int minRoads(int[] packages, int[][] roads) {
        int n = packages.length; // Number of nodes (locations)
        
        // Build adjacency list for the graph
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] road : roads) {
            graph.get(road[0]).add(road[1]); // Add road from ai to bi
            graph.get(road[1]).add(road[0]); // Add reverse (bidirectional)
        }

        int minRoads = Integer.MAX_VALUE; // Track minimum roads needed

        // Try starting at each node
        for (int start = 0; start < n; start++) {
            // BFS state: [current node, roads traversed, packages collected (bitmask)]
            Queue<int[]> queue = new LinkedList<>();
            // Track visited states: [node, bitmask] -> min roads to reach
            Map<String, Integer> visited = new HashMap<>();
            
            int initialMask = getPackagesWithinDistance(start, packages, graph); // Initial collection
            queue.offer(new int[]{start, 0, initialMask}); // Start with 0 roads
            visited.put(start + "-" + initialMask, 0);

            while (!queue.isEmpty()) {
                int[] state = queue.poll();
                int curr = state[0];     // Current node
                int roadsUsed = state[1]; // Roads traversed so far
                int mask = state[2];     // Packages collected (bitmask)

                // Check if all packages are collected
                if (Integer.bitCount(mask) == Integer.bitCount(getAllPackages(packages))) {
                    // Add roads back to start
                    int roadsBack = shortestPath(curr, start, graph);
                    if (roadsBack != -1) { // Ensure path exists
                        minRoads = Math.min(minRoads, roadsUsed + roadsBack);
                    }
                    continue;
                }

                // Move to adjacent nodes
                for (int next : graph.get(curr)) {
                    int newRoads = roadsUsed + 1; // One more road traversed
                    int newMask = mask | getPackagesWithinDistance(next, packages, graph); // Collect at new spot
                    String key = next + "-" + newMask;

                    // If unvisited or fewer roads, explore this state
                    if (!visited.containsKey(key) || newRoads < visited.get(key)) {
                        visited.put(key, newRoads);
                        queue.offer(new int[]{next, newRoads, newMask});
                    }
                }
            }
        }

        return minRoads == Integer.MAX_VALUE ? -1 : minRoads; // Return -1 if impossible
    }

    // Get bitmask of packages within distance 2 from a node
    private static int getPackagesWithinDistance(int node, int[] packages, List<List<Integer>> graph) {
        int mask = 0;
        // BFS to find nodes within distance 2
        Queue<int[]> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(new int[]{node, 0}); // [node, distance]
        visited.add(node);

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int loc = curr[0];
            int dist = curr[1];

            if (dist <= 2) { // Collect packages within distance 2
                if (packages[loc] == 1) {
                    mask |= (1 << loc); // Set bit for this location
                }
                for (int neighbor : graph.get(loc)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(new int[]{neighbor, dist + 1});
                    }
                }
            }
        }
        return mask;
    }

    // Get bitmask of all packages
    private static int getAllPackages(int[] packages) {
        int mask = 0;
        for (int i = 0; i < packages.length; i++) {
            if (packages[i] == 1) {
                mask |= (1 << i);
            }
        }
        return mask;
    }

    // Shortest path between two nodes (BFS)
    private static int shortestPath(int start, int end, List<List<Integer>> graph) {
        if (start == end) return 0;
        Queue<int[]> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(new int[]{start, 0}); // [node, distance]
        visited.add(start);

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int node = curr[0];
            int dist = curr[1];

            if (node == end) return dist;
            for (int next : graph.get(node)) {
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.offer(new int[]{next, dist + 1});
                }
            }
        }
        return -1; // No path found
    }

    public static void main(String[] args) {
        // Test case 1
        int[] packages1 = {1, 0, 0, 0, 0, 1};
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
        System.out.println("Test 1: " + minRoads(packages1, roads1)); // Expect 2

        // Test case 2
        int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1};
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
        System.out.println("Test 2: " + minRoads(packages2, roads2)); // Expect 2
    }
}