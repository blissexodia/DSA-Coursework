import java.util.concurrent.locks.*;

// Simulated NumberPrinter class (assumed provided)
class NumberPrinter {
    public void printZero(int n) { System.out.print(n); }
    public void printEven(int n) { System.out.print(n); }
    public void printOdd(int n) { System.out.print(n); }
}

public class ThreadController {
    private int n;                  // Upper limit for the sequence
    private int currentNumber = 0;  // Tracks the next number to print
    private Lock lock = new ReentrantLock(); // Lock for thread synchronization
    private Condition zeroCond = lock.newCondition(); // Condition for zero thread
    private Condition evenCond = lock.newCondition(); // Condition for even thread
    private Condition oddCond = lock.newCondition();  // Condition for odd thread

    // Constructor to set the limit
    public ThreadController(int n) {
        this.n = n;
    }

    // Zero thread logic
    public void zero() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= n) {
                // Wait if it’s not time for zero (even or odd turn)
                while (currentNumber % 2 != 0 && currentNumber <= n) {
                    zeroCond.await();
                }
                if (currentNumber <= n) {
                    new NumberPrinter().printZero(0); // Print 0
                    currentNumber++;
                    // Signal even thread since 0 is followed by odd/even
                    if (currentNumber % 2 == 0) evenCond.signal();
                    else oddCond.signal();
                }
            }
            // Signal others to finish when done
            evenCond.signal();
            oddCond.signal();
        } finally {
            lock.unlock();
        }
    }

    // Even thread logic
    public void even() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= n) {
                // Wait if it’s not an even number’s turn
                while (currentNumber % 2 != 0 || currentNumber == 0 || currentNumber > n) {
                    evenCond.await();
                }
                if (currentNumber <= n) {
                    new NumberPrinter().printEven(currentNumber); // Print even number
                    currentNumber++;
                    zeroCond.signal(); // Signal zero thread next
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // Odd thread logic
    public void odd() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= n) {
                // Wait if it’s not an odd number’s turn
                while (currentNumber % 2 == 0 || currentNumber > n) {
                    oddCond.await();
                }
                if (currentNumber <= n) {
                    new NumberPrinter().printOdd(currentNumber); // Print odd number
                    currentNumber++;
                    zeroCond.signal(); // Signal zero thread next
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        int n = 5; // Example input
        ThreadController controller = new ThreadController(n);

        // Create threads
        Thread zeroThread = new Thread(() -> {
            try {
                controller.zero();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread evenThread = new Thread(() -> {
            try {
                controller.even();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread oddThread = new Thread(() -> {
            try {
                controller.odd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Start threads
        zeroThread.start();
        evenThread.start();
        oddThread.start();

        // Wait for threads to finish
        try {
            zeroThread.join();
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(); // New line after output
    }
}