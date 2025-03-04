import java.util.*;

public class TetrisGame {
    // Block class to represent a Tetris piece
    static class Block {
        int[][] shape; // 2D array for block shape (1 = filled, 0 = empty)
        int x, y;      // Position on the board (top-left corner)
        String color;  // Color for display (simplified as a string)

        Block(int[][] shape, String color) {
            this.shape = shape;
            this.x = 0;       // Start at top
            this.y = 0;       // Start at left
            this.color = color;
        }
    }

    // Game variables
    static int ROWS = 20;   // Height of the game board
    static int COLS = 10;   // Width of the game board
    static int[][] board;   // 2D array for the game board (0 = empty, 1 = filled)
    static Queue<Block> blockQueue; // Queue for upcoming blocks
    static Stack<int[][]> boardStates; // Stack for board states (for simplicity)
    static Block currentBlock; // The falling block
    static int score;       // Player's score
    static Random rand = new Random();

    // Initialize the game
    public static void initGame() {
        board = new int[ROWS][COLS]; // Create empty board
        blockQueue = new LinkedList<>(); // Initialize block queue
        boardStates = new Stack<>(); // Initialize stack for board states
        score = 0; // Start score at 0
        // Push initial empty board state
        boardStates.push(new int[ROWS][COLS]);
        // Generate and enqueue first block
        enqueueRandomBlock();
        currentBlock = blockQueue.poll(); // Dequeue to start
    }

    // Generate a random block and add to queue
    public static void enqueueRandomBlock() {
        // Simple L-shape block for demo (could expand to all Tetris shapes)
        int[][] shape = {{1, 0}, {1, 0}, {1, 1}};
        String[] colors = {"Red", "Blue", "Green", "Yellow"};
        Block block = new Block(shape, colors[rand.nextInt(colors.length)]);
        blockQueue.offer(block);
    }

    // Check if block can move to new position
    public static boolean canMove(int newX, int newY, int[][] shape) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (shape[i][j] == 1) {
                    int boardX = newX + i;
                    int boardY = newY + j;
                    // Check boundaries and collisions
                    if (boardX < 0 || boardX >= ROWS || boardY < 0 || boardY >= COLS || board[boardX][boardY] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Rotate the block (90 degrees clockwise)
    public static int[][] rotate(int[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = shape[i][j];
            }
        }
        return rotated;
    }

    // Place the block on the board
    public static void placeBlock() {
        for (int i = 0; i < currentBlock.shape.length; i++) {
            for (int j = 0; j < currentBlock.shape[0].length; j++) {
                if (currentBlock.shape[i][j] == 1) {
                    board[currentBlock.x + i][currentBlock.y + j] = 1;
                }
            }
        }
    }

    // Check and clear completed rows
    public static void checkAndClearRows() {
        for (int i = ROWS - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                // Shift rows down
                for (int r = i; r > 0; r--) {
                    board[r] = board[r - 1].clone();
                }
                board[0] = new int[COLS]; // New empty row at top
                score += 10; // Add score for clearing a row
                i++; // Recheck this row after shifting
            }
        }
    }

    // Main game loop
    public static void gameLoop() {
        initGame();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Check game over: top row filled
            for (int j = 0; j < COLS; j++) {
                if (board[0][j] == 1) {
                    System.out.println("Game Over! Score: " + score);
                    return;
                }
            }

            // Display game state (simplified console output)
            System.out.println("Score: " + score);
            System.out.println("Next block: " + blockQueue.peek().color);
            for (int[] row : board) {
                System.out.println(Arrays.toString(row));
            }

            // Handle user input
            System.out.print("Move (L/R/Rotate/Down): ");
            String input = scanner.nextLine().toUpperCase();
            if (input.equals("L") && canMove(currentBlock.x, currentBlock.y - 1, currentBlock.shape)) {
                currentBlock.y--;
            } else if (input.equals("R") && canMove(currentBlock.x, currentBlock.y + 1, currentBlock.shape)) {
                currentBlock.y++;
            } else if (input.equals("ROTATE")) {
                int[][] rotated = rotate(currentBlock.shape);
                if (canMove(currentBlock.x, currentBlock.y, rotated)) {
                    currentBlock.shape = rotated;
                }
            }

            // Move block down
            if (canMove(currentBlock.x + 1, currentBlock.y, currentBlock.shape)) {
                currentBlock.x++;
            } else {
                placeBlock(); // Place block if it can't move down
                checkAndClearRows(); // Check for completed rows
                boardStates.push(board.clone()); // Save current state
                enqueueRandomBlock(); // Generate new block
                currentBlock = blockQueue.poll(); // Get next block
            }
        }
    }

    public static void main(String[] args) {
        gameLoop();
    }
}