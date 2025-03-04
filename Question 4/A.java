import java.util.*;

public class TopHashtags {
    public static List<String[]> getTopHashtags(String[][] tweets) {
        // HashMap to track hashtag counts
        Map<String, Integer> hashtagCounts = new HashMap<>();

        // Loop through each tweet in the 2D array
        for (String[] tweet : tweets) {
            String tweetText = tweet[2]; // Grab the tweet text (column 2)
            String[] words = tweetText.split("\\s+"); // Split by whitespace into words

            // Scan each word for hashtags
            for (String word : words) {
                if (word.startsWith("#") && word.length() > 1) { // Check for valid hashtag
                    hashtagCounts.put(word, hashtagCounts.getOrDefault(word, 0) + 1); // Count it
                }
            }
        }

        // Convert HashMap to list of [hashtag, count] pairs for sorting
        List<String[]> hashtagList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : hashtagCounts.entrySet()) {
            hashtagList.add(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
        }

        // Sort: descending by count, ascending by hashtag name if tied
        hashtagList.sort((a, b) -> {
            int countA = Integer.parseInt(a[1]);
            int countB = Integer.parseInt(b[1]);
            if (countA != countB) {
                return countB - countA; // Higher count first
            }
            return a[0].compareTo(b[0]); // Alphabetically if counts equal
        });

        // Take top 3 (or less if fewer hashtags exist)
        return hashtagList.subList(0, Math.min(3, hashtagList.size()));
    }

    public static void main(String[] args) {
        // Sample tweet data
        String[][] tweets = {
            {"135", "13", "Enjoying a great start to the day. #HappyDay #MorningVibes", "2024-02-01"},
            {"136", "14", "Another #HappyDay with good vibes! #FeelGood", "2024-02-03"},
            {"137", "15", "Productivity peaks! #WorkLife #ProductiveDay", "2024-02-04"},
            {"138", "16", "Exploring new tech frontiers. #TechLife #Innovation", "2024-02-04"},
            {"139", "17", "Gratitude for today’s moments. #HappyDay #Thankful", "2024-02-05"},
            {"140", "18", "Innovation drives us. #TechLife #FutureTech", "2024-02-07"},
            {"141", "19", "Connecting with nature’s serenity. #Nature #Peaceful", "2024-02-09"}
        };

        // Get top 3 hashtags
        List<String[]> topHashtags = getTopHashtags(tweets);

        // Print results in a dope format
        System.out.println("Top 3 Hashtags:");
        System.out.println("+-------------+---------+");
        System.out.println("|   HASHTAG   |  COUNT  |");
        System.out.println("+-------------+---------+");
        for (String[] hashtag : topHashtags) {
            System.out.printf("| %-11s | %-7s |\n", hashtag[0], hashtag[1]);
        }
        System.out.println("+-------------+---------+");
    }
}