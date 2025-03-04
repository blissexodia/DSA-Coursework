import java.util.PriorityQueue;

public class KthLowestReturn {
    public static int findKthLowest(int[] returns1, int[] returns2, int k) {
        // Create a min-heap to store pairs of indices and their product
        // The heap will order by product value (smallest first)
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        // Start with the smallest possible product: returns1[0] * returns2[0]
        // Store [product, index1, index2] in the heap
        minHeap.offer(new int[]{returns1[0] * returns2[0], 0, 0});

        // Keep track of visited pairs to avoid duplicates
        // Using a string "i-j" to mark indices we've seen
        java.util.HashSet<String> visited = new java.util.HashSet<>();

        // Add the initial pair to visited
        visited.add("0-0");

        // Counter for how many products we've popped off
        int count = 0;

        // Variable to store the current product
        int result = 0;

        // Loop until we find the k-th smallest product
        while (count < k) {
            // Pop the smallest product from the heap
            int[] current = minHeap.poll();

            // Store the product as our potential result
            result = current[0];

            // Get the indices used for this product
            int i = current[1];
            int j = current[2];

            // Increment count since we’ve processed one product
            count++;

            // If we’re not at the end of returns1, add the next pair from returns1
            if (i + 1 < returns1.length) {
                String nextPair1 = (i + 1) + "-" + j;
                // Only add if we haven’t seen this pair before
                if (!visited.contains(nextPair1)) {
                    minHeap.offer(new int[]{returns1[i + 1] * returns2[j], i + 1, j});
                    visited.add(nextPair1);
                }
            }

            // If we’re not at the end of returns2, add the next pair from returns2
            if (j + 1 < returns2.length) {
                String nextPair2 = i + "-" + (j + 1);
                // Only add if we haven’t seen this pair before
                if (!visited.contains(nextPair2)) {
                    minHeap.offer(new int[]{returns1[i] * returns2[j + 1], i, j + 1});
                    visited.add(nextPair2);
                }
            }
        }

        // After k iterations, result is the k-th smallest product
        return result;
    }

    public static void main(String[] args) {
        // Test case 1: returns1 = [2,5], returns2 = [3,4], k = 2
        int[] returns1a = {2, 5};
        int[] returns2a = {3, 4};
        int k1 = 2;
        // Print the k-th lowest product for this case
        System.out.println("returns1=[2,5], returns2=[3,4], k=2: " + findKthLowest(returns1a, returns2a, k1));

        // Test case 2: returns1 = [-4,-2,0,3], returns2 = [2,4], k = 6
        int[] returns1b = {-4, -2, 0, 3};
        int[] returns2b = {2, 4};
        int k2 = 6;
        // Print the k-th lowest product for this case
        System.out.println("returns1=[-4,-2,0,3], returns2=[2,4], k=6: " + findKthLowest(returns1b, returns2b, k2));
    }
}