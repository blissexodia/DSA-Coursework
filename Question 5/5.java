import java.util.*;

public class NetworkOptimizer {
    // Edge class to represent a network connection
    static class Edge {
        int src, dest, cost; // Source node, destination node, connection cost
        double bandwidth;    // Bandwidth (used as inverse weight for latency)

        Edge(int src, int dest, int cost, double bandwidth) {
            this.src = src;
            this.dest = dest;
            this.cost = cost;
            this.bandwidth = bandwidth;
        }
    }

    // Union-Find for MST
    static class UnionFind {
        int[] parent, rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) parent[rootX] = rootY;
                else if (rank[rootX] > rank[rootY]) parent[rootY] = rootX;
                else { parent[rootY] = rootX; rank[rootX]++; }
            }
        }
    }

    // Graph representation
    static int n; // Number of nodes
    static List<Edge> edges; // All possible connections
    static List<Edge>[] adjList; // Adjacency list for shortest path

    // Initialize the network
    public static void initNetwork(int nodes, List<Edge> connections) {
        n = nodes;
        edges = new ArrayList<>(connections);
        adjList = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }
        for (Edge e : edges) {
            adjList[e.src].add(e);
            adjList[e.dest].add(new Edge(e.dest, e.src, e.cost, e.bandwidth)); // Bidirectional
        }
    }

    // Find Minimum Spanning Tree (minimizes cost)
    public static List<Edge> getMST() {
        List<Edge> mst = new ArrayList<>();
        edges.sort((a, b) -> a.cost - b.cost); // Sort by cost
        UnionFind uf = new UnionFind(n);

        for (Edge e : edges) {
            if (uf.find(e.src) != uf.find(e.dest)) {
                uf.union(e.src, e.dest);
                mst.add(e);
            }
        }
        return mst;
    }

    // Dijkstra's algorithm for shortest path (minimizes latency using bandwidth)
    public static List<Integer> shortestPath(int start, int end, List<Edge> topology) {
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Double.compare(a[1], b[1]));
        pq.offer(new int[]{start, 0}); // [node, distance]

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];
            if (u == end) break;

            for (Edge e : adjList[u]) {
                if (!topology.contains(e) && !topology.contains(new Edge(e.dest, e.src, e.cost, e.bandwidth))) {
                    continue; // Only use edges in the selected topology
                }
                double latency = 1.0 / e.bandwidth; // Latency inverse of bandwidth
                if (dist[u] + latency < dist[e.dest]) {
                    dist[e.dest] = dist[u] + latency;
                    prev[e.dest] = u;
                    pq.offer(new int[]{e.dest, (int)dist[e.dest]});
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path.size() > 1 && path.get(0) == start ? path : new ArrayList<>();
    }

    // Calculate total cost of a topology
    public static int getTotalCost(List<Edge> topology) {
        int total = 0;
        for (Edge e : topology) {
            total += e.cost;
        }
        return total;
    }

    // Main simulation
    public static void main(String[] args) {
        // Example input
        int nodes = 4;
        List<Edge> connections = new ArrayList<>();
        connections.add(new Edge(0, 1, 10, 100)); // Node 0-1: cost 10, bandwidth 100
        connections.add(new Edge(0, 2, 20, 50));
        connections.add(new Edge(1, 2, 15, 80));
        connections.add(new Edge(1, 3, 25, 60));
        connections.add(new Edge(2, 3, 30, 40));

        // Initialize network
        initNetwork(nodes, connections);

        // Get MST for cost optimization
        List<Edge> topology = getMST();
        int totalCost = getTotalCost(topology);

        // Display topology (GUI would visualize this)
        System.out.println("Optimal Topology (Min Cost):");
        for (Edge e : topology) {
            System.out.println("Node " + e.src + " - Node " + e.dest + ": Cost=" + e.cost + ", Bandwidth=" + e.bandwidth);
        }
        System.out.println("Total Cost: " + totalCost);

        // Example shortest path (latency optimized)
        int start = 0, end = 3;
        List<Integer> path = shortestPath(start, end, topology);
        System.out.println("Shortest Path from " + start + " to " + end + ": " + path);
    }
}