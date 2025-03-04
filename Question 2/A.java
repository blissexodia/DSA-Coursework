public class MinRewards {
    public static int minRewards(int[] ratings) {
        // Create an array to store the rewards for each employee, initialized to 1 (minimum reward)
        int[] rewards = new int[ratings.length];
        for (int i = 0; i < ratings.length; i++) {
            rewards[i] = 1;
        }

        // First pass: Left to right - ensure higher ratings get more rewards than left neighbor
        for (int i = 1; i < ratings.length; i++) {
            // If current rating is higher than previous, give more rewards than previous
            if (ratings[i] > ratings[i - 1]) {
                rewards[i] = rewards[i - 1] + 1;
            }
        }

        // Second pass: Right to left - ensure higher ratings get more rewards than right neighbor
        for (int i = ratings.length - 2; i >= 0; i--) {
            // If current rating is higher than next, adjust rewards to be at least one more than next
            if (ratings[i] > ratings[i + 1]) {
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }

        // Calculate the total sum of rewards
        int totalRewards = 0;
        for (int reward : rewards) {
            totalRewards += reward;
        }

        // Return the minimum total rewards needed
        return totalRewards;
    }

    public static void main(String[] args) {
        // Test case 1: ratings = [1, 0, 2]
        int[] ratings1 = {1, 0, 2};
        // Print the minimum rewards for this case
        System.out.println("ratings=[1,0,2]: " + minRewards(ratings1));

        // Test case 2: ratings = [1, 2, 2]
        int[] ratings2 = {1, 2, 2};
        // Print the minimum rewards for this case
        System.out.println("ratings=[1,2,2]: " + minRewards(ratings2));
    }
}