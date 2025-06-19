package M3;

/*
Challenge 2: Simple Slash Command Handler
-----------------------------------------
- Accept user input as slash commands
  - "/greet <name>" → Prints "Hello, <name>!"
  - "/roll <num>d<sides>" → Roll <num> dice with <sides> and returns a single outcome as "Rolled <num>d<sides> and got <result>!"
  - "/echo <message>" → Prints the message back
  - "/quit" → Exits the program
- Commands are case-insensitive
- Print an error for unrecognized commands
- Print errors for invalid command formats (when applicable)
- Capture 3 variations of each command except "/quit"
*/

import java.util.Scanner;

public class SlashCommandHandler extends BaseClass {
    private static String ucid = "fk222"; // <-- change to your UCID

    public static void main(String[] args) {
        printHeader(ucid, 2, "Objective: Implement a simple slash command parser.");

        Scanner scanner = new Scanner(System.in);

        // Can define any variables needed here

        while (true) {
            System.out.print("Enter command: ");
            // fk222 6/18/25
            // get entered text
            String input = scanner.nextLine().trim();
            // check if greet
            //// process greet
            if (input.toLowerCase().startsWith("/greet")){
                String[] greetCommand = input.split(" ");
                if (greetCommand.length < 2){ //checks if missing name after command
                    System.out.println("Missing name for /greet command");
                } else { //removes command to make array of only name -- this allows user to put full name
                    String[] name = new String[greetCommand.length - 1];
                    for (int i=1; i<greetCommand.length; i++){
                        name[i-1] = greetCommand[i];
                    }
                    System.out.println("Hello, " + String.join(" ", name) + "!"); //joins array into sentence structure
                }
            }

            // check if roll
            //// process roll
            //// handle invalid formats
            else if (input.toLowerCase().startsWith("/roll")){
                String[] rollCommand = input.split(" ", 2);
                if (rollCommand.length < 2){
                    System.out.println("Missing rolls and sides for /roll command. Use /roll <num>d<sides>");
                    continue;
                }
                try {
                    String[] dice = rollCommand[1].toLowerCase().split("d");
                    int num = Integer.parseInt(dice[0]);
                    int sides = Integer.parseInt(dice[1]);

                    if (num <= 0 || sides <= 0){
                        System.out.println("Number of dice and number of sides has to be positive. Try again!");
                        continue;
                    }

                    int result = 0;
                    for (int i=0; i < num; i++){ // for every die, roll and get random number out of random sides
                        result += (int)(Math.random()*sides) + 1;
                        // add +1 because Math.random()*sides casted to int will be range of 0 to sides-1, add 1 to make range actually 1-sides
                    }

                    System.out.println("Rolled " + rollCommand[1] + " and got " + result + "!");
                } catch (Exception e) {System.out.println("Invalid call for /roll! Please use /roll <num>d<sides>");}  
            }

            // check if echo
            //// process echo
            else if (input.toLowerCase().startsWith("/echo")){
                String[] echoCommand = input.split(" ", 2);
                if (echoCommand.length < 2){
                    System.out.println("Missing message for /echo. Please try again");
                } else {System.out.println(echoCommand[1]);}

            }

            // check if quit
            //// process quit
            else if (input.toLowerCase().equals("/quit")){
                System.out.println("Goodbye, see ya!");
                break;
            }

            // handle invalid commnads
            else{
                System.out.println("Invalid command. Here are the available commands:");
                System.out.println("/greet <name> \n/roll <num>d<sides> \n/echo <message> \n/quit");
            }
        }

        printFooter(ucid, 2);
        scanner.close();
    }
}
