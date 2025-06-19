package Module3;

import java.util.Random;
import java.util.Scanner;

public class NumberGuesser2 {
    public static void main(String[] args) {
        /*
         ⚠ Important: Using try-with-resources ensures Scanner is closed automatically.
         However, when wrapping System.in, closing Scanner also closes System.in, 
         making further input impossible. This is fine here since the program terminates.
        */
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Welcome to NumberGuesser2.0");
            System.out.println("To exit, type the word 'quit'.");

            // Game variables
            int level = 1; // Tracks current level
            int strikes = 0; // Number of incorrect guesses
            final int maxStrikes = 5; // Maximum incorrect guesses before level drops
            int number = -1; // Stores the randomly generated number
            boolean pickNewRandom = true; // Flag to determine when to pick a new number

            do {
                // Pick a new random number at the start of a level
                if (pickNewRandom) {
                    int range = 9 + ((level - 1) * 5); // Expands range as level increases
                    System.out.println("Welcome to level " + level);
                    System.out.println("I picked a random number between 1-" + (range + 1) + ", let's see if you can guess.");
                    number = new Random().nextInt(range) + 1; // Generate number between 1 and range
                    pickNewRandom = false; // Prevents picking a new number until needed
                }

                System.out.println("Type a number and press enter");
                String message = input.nextLine();

                // Early termination check
                if (message.equalsIgnoreCase("quit")) {
                    System.out.println("Tired of playing? No problem, see you next time.");
                    break;
                }

                int guess = -1; // Default value for invalid input
                try {
                    guess = Integer.parseInt(message); // Attempt to convert input to an integer
                } catch (NumberFormatException e) { // Catches non-numeric input
                    System.out.println("You didn't enter a number, please try again.");
                }

                if (guess > -1) { // Only proceed if a valid number was entered
                    System.out.println("You guessed " + guess);

                    if (guess == number) {
                        System.out.println("That's right! Leveling up...");
                        level++; // Increase level on a correct guess
                        strikes = 0; // Reset strikes on success
                        pickNewRandom = true; // Pick a new number for the next round
                    } else {
                        System.out.println("That's wrong.");
                        strikes++; // Increment strike count

                        if (strikes >= maxStrikes) { // Check if max strikes reached
                            System.out.println("Uh oh, looks like you need more practice.");
                            strikes = 0;
                            level = Math.max(1, level - 1); // Prevent level from dropping below 1
                            pickNewRandom = true;
                        }
                    }
                }
            } while (true); // Infinite loop until user quits
        } catch (Exception e) {
            System.out.println("An unexpected error occurred. Goodbye.");
            e.printStackTrace(); // Debugging purposes (remove in production)
        }

        System.out.println("Thanks for playing!");
    }
}