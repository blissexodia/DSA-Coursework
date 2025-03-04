public class CriticalTempFinder {
    public static int minMeasurements(int k, int n) {
        // Base case: if we only have 1 sample, we need to test each level one-by-one
        if (k == 1) return n;

        // Create a 2D DP table where dp[i][j] is the max number of temp levels we can test
        // with i samples and j measurements
        int[][] dp = new int[k + 1][n + 1];

        // Initialize moves counter - we'll increment this until we can cover n levels
        int moves = 0;

        // Keep going until we can test at least n levels with k samples
        while (dp[k][moves] < n) {
            // Increment the number of measurements we're allowed to use
            moves++;

            // Loop through each number of samples from 1 to k
            for (int i = 1; i <= k; i++) {
                // For i samples and 'moves' measurements, calculate max levels testable:
                // - 1: the current temp we test
                // - dp[i-1][moves-1]: if sample reacts, we lose it and handle lower levels
                // - dp[i][moves-1]: if sample doesn’t react, we reuse it for upper levels
                dp[i][moves] = 1 + dp[i - 1][moves - 1] + dp[i][moves - 1];
            }
        }

        // Once dp[k][moves] >= n, we’ve found the smallest number of moves needed
        return moves;
    }

    public static void main(String[] args) {
        // Test case 1: k = 1, n = 2
        int k1 = 1, n1 = 2;
        // Print the result for k=1, n=2, expecting 2 measurements
        System.out.println("k=" + k1 + ", n=" + n1 + ": " + minMeasurements(k1, n1));

        // Test case 2: k = 2, n = 6
        int k2 = 2, n2 = 6;
        // Print the result for k=2, n=6, expecting 3 measurements
        System.out.println("k=" + k2 + ", n=" + n2 + ": " + minMeasurements(k2, n2));

        // Test case 3: k = 3, n = 14
        int k3 = 3, n3 = 14;
        // Print the result for k=3, n=14, expecting 4 measurements
        System.out.println("k=" + k3 + ", n=" + n3 + ": " + minMeasurements(k3, n3));
    }
}