import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class WebCrawler {
    // Data structure for crawled data (URL -> content)
    private Map<String, String> crawledData;
    // Queue for URLs to crawl (thread-safe)
    private Queue<String> urlQueue;
    // Thread pool for crawling tasks
    private ExecutorService executor;
    // Lock for synchronizing data access
    private ReentrantLock dataLock;
    // Set to track visited URLs (thread-safe)
    private Set<String> visited;
    // Max URLs to crawl (for simulation)
    private int maxUrls;
    // Counter for crawled URLs
    private volatile int crawledCount;

    // Constructor to initialize the crawler
    public WebCrawler(int threadCount, int maxUrls) {
        this.crawledData = new ConcurrentHashMap<>(); // Thread-safe map for data
        this.urlQueue = new LinkedList<>();           // Queue for URLs
        this.executor = Executors.newFixedThreadPool(threadCount); // Fixed thread pool
        this.dataLock = new ReentrantLock();          // Lock for queue and visited
        this.visited = ConcurrentHashMap.newKeySet(); // Thread-safe set
        this.maxUrls = maxUrls;                       // Limit for crawling
        this.crawledCount = 0;                        // Track progress
    }

    // Task to crawl a single URL
    class CrawlTask implements Runnable {
        private String url;

        CrawlTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                // Simulate fetching the web page
                String content = fetchPage(url);
                // Process and store the content
                processPage(url, content);
                // Extract new URLs (simulated)
                List<String> newUrls = extractUrls(content);

                // Add new URLs to queue
                dataLock.lock();
                try {
                    for (String newUrl : newUrls) {
                        if (!visited.contains(newUrl) && crawledCount < maxUrls) {
                            urlQueue.offer(newUrl);
                            visited.add(newUrl);
                            executor.submit(new CrawlTask(newUrl)); // Submit new task
                        }
                    }
                } finally {
                    dataLock.unlock();
                }
            } catch (Exception e) {
                System.out.println("Error crawling " + url + ": " + e.getMessage());
            }
        }

        // Simulate fetching a page (replace with real HTTP call in practice)
        private String fetchPage(String url) throws InterruptedException {
            Thread.sleep(100); // Simulate network delay
            return "Content of " + url + " with links: [http://example.com/" + (url.hashCode() % 10) + "]";
        }

        // Process the fetched content
        private void processPage(String url, String content) {
            dataLock.lock();
            try {
                crawledData.put(url, content);
                crawledCount++;
                System.out.println("Crawled: " + url + " | Content: " + content);
            } finally {
                dataLock.unlock();
            }
        }

        // Extract new URLs from content (simulated)
        private List<String> extractUrls(String content) {
            List<String> urls = new ArrayList<>();
            int idx = content.indexOf("http");
            if (idx != -1) {
                String newUrl = content.substring(idx, content.indexOf("]", idx));
                urls.add(newUrl);
            }
            return urls;
        }
    }

    // Start the crawling process
    public void startCrawling(List<String> seedUrls) {
        // Add seed URLs to queue and visited set
        dataLock.lock();
        try {
            for (String url : seedUrls) {
                if (!visited.contains(url) && crawledCount < maxUrls) {
                    urlQueue.offer(url);
                    visited.add(url);
                    executor.submit(new CrawlTask(url)); // Submit initial tasks
                }
            }
        } finally {
            dataLock.unlock();
        }

        // Monitor completion (simplified)
        while (crawledCount < maxUrls && !urlQueue.isEmpty()) {
            try {
                Thread.sleep(500); // Wait and check progress
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Shutdown the executor
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Get crawled data
    public Map<String, String> getCrawledData() {
        return crawledData;
    }

    public static void main(String[] args) {
        // Example usage
        List<String> seedUrls = Arrays.asList(
            "http://6b.com/1",
            "http://6b.com/2",
            "http://6b.com/3"
        );
        WebCrawler crawler = new WebCrawler(3, 10); // 3 threads, max 10 URLs
        crawler.startCrawling(seedUrls);

        // Print results
        System.out.println("Crawling complete. Data:");
        for (Map.Entry<String, String> entry : crawler.getCrawledData().entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}