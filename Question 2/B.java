public class ClosestPoints {
    public static int[] findClosestPair(int[] x_coords, int[] y_coords) {
        // Initialize variables to store the smallest distance found (set to infinity initially)
        int minDistance = Integer.MAX_VALUE;

        // Array to store the result indices [i, j]
        int[] result = new int[2];

        // Loop through all possible pairs of points
        for (int i = 0; i < x_coords.length; i++) {
            for (int j = 0; j < x_coords.length; j++) {
                // Skip if i equals j (no distance between the same point)
                if (i == j) {
                    continue;
                }

                // Calculate Manhattan distance: |x[i] - x[j]| + |y[i] - y[j]|
                int distance = Math.abs(x_coords[i] - x_coords[j]) + Math.abs(y_coords[i] - y_coords[j]);

                // If this distance is smaller than minDistance, update minDistance and result
                if (distance < minDistance) {
                    minDistance = distance;
                    result[0] = i;
                    result[1] = j;
                }
                // If distance equals minDistance, update only if lexicographically smaller
                else if (distance == minDistance) {
                    // Check if current i is smaller OR i equals but j is smaller
                    if (i < result[0] || (i == result[0] && j < result[1])) {
                        result[0] = i;
                        result[1] = j;
                    }
                }
            }
        }

        // Return the indices of the closest pair
        return result;
    }

    public static void main(String[] args) {
        // Test case: x_coords = [1, 2, 3, 2, 4], y_coords = [2, 3, 1, 2, 3]
        int[] x_coords = {1, 2, 3, 2, 4};
        int[] y_coords = {2, 3, 1, 2, 3};
        // Find the closest pair
        int[] result = findClosestPair(x_coords, y_coords);
        // Print the result
        System.out.print("x_coords=[1,2,3,2,4], y_coords=[2,3,1,2,3]: [");
        System.out.println(result[0] + ", " + result[1] + "]");
    }
}