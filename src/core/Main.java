package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Main {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;

    /**
     * The main method initializes the display and starts the game in either interactive or batch mode.
     *
     * @param args command-line arguments, where the first argument can be a seed for batch mode
     */
    public static void main(String[] args) {
        initializeDisplay();
        System.out.println("args=" + args.length);
        if (args.length == 0) {
            interactive();
            System.out.println("Returned from Interactive");
        } else {
            GameState gs = new GameState(true, Long.parseLong(args[0]));
            gs.start(true);
        }
    }

    /**
     * Generates a world using the testing API with the given seed.
     *
     * @param seed the seed used for world generation
     * @return a 2D array of TETile representing the generated world
     */
    public static TETile[][] getWorldFromTestingAPI(long seed) {
        GameState gamestate = new GameState(false, seed);
        gamestate.start(false);
        return gamestate.getTiles();
    }

    /**
     * Runs the game in interactive mode, showing the main menu and handling user input.
     */
    static void interactive() {
        showMainMenu();
        GameState gameState = handleMainMenuInput();
        gameState.start(true);
        System.exit(0);
    }

    /**
     * Initializes the display settings for the game.
     */
    private static void initializeDisplay() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    /**
     * Displays the main menu on the screen.
     */
    private static void showMainMenu() {
        StdDraw.clear(Color.BLACK);
        Font titleFont = new Font("Monaco", Font.BOLD, 30);
        Font menuFont = new Font("Monaco", Font.PLAIN, 20);

        StdDraw.setFont(titleFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT - 5, "Isometric Island");
        StdDraw.text(WIDTH / 2, HEIGHT - 8, "Neil Pattanaik");
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "UC Berkeley");

        StdDraw.setFont(menuFont);
        StdDraw.text(WIDTH / 2, HEIGHT - 14, "N - New World");
        StdDraw.text(WIDTH / 2, HEIGHT - 16, "L - Load World");
        StdDraw.text(WIDTH / 2, HEIGHT - 18, "C - Create World with Custom Settings");
        StdDraw.text(WIDTH / 2, HEIGHT - 20, "Q - Quit");
        StdDraw.show();
    }

    /**
     * Handles user input from the main menu and returns the corresponding GameState.
     *
     * @return the GameState based on user input
     */
    private static GameState handleMainMenuInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (key) {
                    case 'N':
                    case 'n':
                        return promptForSeed();
                    case 'L':
                    case 'l':
                        System.out.println("Load world");
                        return new GameState(true);
                    case 'C':
                    case 'c':
                        return promptForCustomSettings();
                    case 'Q':
                    case 'q':
                        System.exit(0);
                        return null;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Prompts the user to enter a seed for generating a new world.
     *
     * @return the GameState initialized with the entered seed
     */
    private static GameState promptForSeed() {
        StringBuilder seed = new StringBuilder();
        while (true) {
            StdDraw.clear(Color.BLACK);
            Font font = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(font);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Enter a random seed:");
            StdDraw.text(WIDTH / 2, HEIGHT / 2, seed.toString());
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "Press S to start");
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (Character.isDigit(key)) {
                    seed.append(key);
                } else if (key == 'S' || key == 's') {
                    long seedValue;
                    try {
                        seedValue = Long.parseLong(seed.toString());
                    } catch (NumberFormatException e) {
                        seedValue = 0;
                    }
                    System.out.println("New world with seed: " + seedValue);
                    return new GameState(true, seedValue);
                }
            }
        }
    }

    /**
     * Prompts the user to enter custom settings for generating a new world.
     *
     * @return the GameState initialized with the entered custom settings
     */
    private static GameState promptForCustomSettings() {
        String shape = promptForOption("Shape (rectangular, circular, cubic)", "rectangular").toLowerCase();
        String spread = promptForOption("Spread (packed, scattered)", "packed").toLowerCase();
        int width = promptForInt("Width", 150);
        int height = promptForInt("Height", 75);
        String straightness = promptForOption("Straightness (Straight, Semi-Straight, Arbitrary, Custom)", "Straight").toLowerCase();
        int straightnessPercent = 0;
        if (straightness.equals("custom")) {
            straightnessPercent = promptForInt("Straightness Percent (1-100)", 50);
        }
        int minRoomSize = promptForInt("Min Room Size", 5);
        int maxRoomSize = promptForInt("Max Room Size", 12);
        long seed = promptForSeedValue();

        System.out.println("Custom world with settings:");
        System.out.println("Shape: " + shape);
        System.out.println("Spread: " + spread);
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Straightness: " + straightness);
        if (straightness.equalsIgnoreCase("Custom")) {
            System.out.println("Straightness Percent: " + straightnessPercent);
        }
        System.out.println("Min Room Size: " + minRoomSize);
        System.out.println("Max Room Size: " + maxRoomSize);
        System.out.println("Seed: " + seed);

        return new GameState(true, seed, height, width,
                spread, shape, minRoomSize, maxRoomSize, straightness, straightnessPercent);
    }

    /**
     * Prompts the user to enter an option with a default value.
     *
     * @param prompt the prompt message
     * @param defaultValue the default value if the user does not enter anything
     * @return the entered option or the default value
     */
    private static String promptForOption(String prompt, String defaultValue) {
        StringBuilder input = new StringBuilder();
        while (true) {
            StdDraw.clear(Color.BLACK);
            Font font = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(font);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, prompt);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, input.toString());
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "Press Enter to confirm, leave empty for default (" + defaultValue + ")");
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == KeyEvent.VK_ENTER) {
                    if (input.length() == 0) {
                        return defaultValue;
                    } else {
                        return input.toString();
                    }
                } else {
                    input.append(key);
                }
            }
        }
    }

    /**
     * Prompts the user to enter an integer with a default value.
     *
     * @param prompt the prompt message
     * @param defaultValue the default value if the user does not enter anything
     * @return the entered integer or the default value
     */
    private static int promptForInt(String prompt, int defaultValue) {
        StringBuilder input = new StringBuilder();
        while (true) {
            StdDraw.clear(Color.BLACK);
            Font font = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(font);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, prompt);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, input.toString());
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "Press Enter to confirm, leave empty for default (" + defaultValue + ")");
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == KeyEvent.VK_ENTER) {
                    if (input.isEmpty()) {
                        return defaultValue;
                    } else {
                        try {
                            return Integer.parseInt(input.toString());
                        } catch (NumberFormatException e) {
                            input.setLength(0);
                        }
                    }
                } else if (Character.isDigit(key)) {
                    input.append(key);
                }
            }
        }
    }

    /**
     * Prompts the user to enter a seed value.
     *
     * @return the entered seed value
     */
    private static long promptForSeedValue() {
        StringBuilder seed = new StringBuilder();
        while (true) {
            StdDraw.clear(Color.BLACK);
            Font font = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(font);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Enter a random seed:");
            StdDraw.text(WIDTH / 2, HEIGHT / 2, seed.toString());
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "Press S to start");
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (Character.isDigit(key)) {
                    seed.append(key);
                } else if (key == 'S' || key == 's') {
                    try {
                        return Long.parseLong(seed.toString());
                    } catch (NumberFormatException e) {
                        seed.setLength(0);
                    }
                }
            }
        }
    }
}