package Module3;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.lang.String;

public class HigherOrLower {
    private int hiddenNumber = 0;
    private int streak = 0;
    private int bestStreak = 0;

    private void lose() {
        System.out.println("Incorrect!");
        if (streak > bestStreak) {
            System.out.println(String.format("You beat your best streak %s vs %s", bestStreak, streak));
            bestStreak = streak;
        }
        streak = 0;
    }

    private void win() {
        streak++;
        System.out.println(String.format("Correct! Current streak: %s", streak));
    }

    private void clearTerminal() {
        // Delay before clearing so the player can see the result
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // Ignored: Clearing screen isn't critical
        }
        // Special ASCII sequence to clear the terminal
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void run() {
        /*
         ⚠ Important: Using try-with-resources ensures Scanner is closed automatically.
         However, since Scanner wraps System.in, closing it will close System.in permanently.
         This is fine in this case because the game ends after this method.
        */
        try (Scanner scanner = new Scanner(System.in)) {
            boolean isRunning = true;
            while (isRunning) {

                hiddenNumber = (int) ((Math.random() * 9) + 1);
                int visibleNumber = (int) ((Math.random() * 9) + 1);
                String message = String.format("I picked a number between 1-10. Is it higher or lower than %s",
                        visibleNumber);
                System.out.println(message);
                // try commenting out this if condition and see if ctrl+c works as expected
                if (scanner.hasNext()) {
                    // Q1: Is there anything to consider for smoother user input?
                    String userInput = scanner.nextLine().toLowerCase(); // adjusts user input to needed format if necessary
                    userInput = userInput.trim();
                    // treat equal as a win
                    switch (userInput) {
                        case "higher":
                            // Can we adust the logic to eliminate the else and still work?
                            if (hiddenNumber >= visibleNumber) {
                                win();
                            } else {
                                lose();
                            }
                            break;
                        case "lower":
                            // Can we adust the logic to eliminate the else and still work?
                            if (hiddenNumber <= visibleNumber) {
                                win();
                            } else {
                                lose();
                            }
                            break;
                        case "quit":
                            isRunning = false;
                            break;
                        default:
                            break;
                    }
                }
                // uncomment this to clear the terminal after each phase
                // Note: Having this uncommented hides a potenial problem
                clearTerminal();
            }
        } catch (NoSuchElementException e) {
            System.out.println("Game interrupted. Exiting...");
        }
    }

    public static void main(String[] args) {
        new HigherOrLower().run();
    }
}