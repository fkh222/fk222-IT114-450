package M3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
Challenge 3: Mad Libs Generator (Randomized Stories)
-----------------------------------------------------
- Load a **random** story from the "stories" folder
- Extract **each line** into a collection (i.e., ArrayList)
- Prompts user for each placeholder (i.e., <adjective>) 
    - Any word the user types is acceptable, no need to verify if it matches the placeholder type
    - Any placeholder with underscores should display with spaces instead
- Replace placeholders with user input (assign back to original slot in collection)
*/

public class MadLibsGenerator extends BaseClass {
    private static final String STORIES_FOLDER = "M3/stories";
    private static String ucid = "fk222"; // <-- change to your ucid

    public static void main(String[] args) {
        printHeader(ucid, 3,
                "Objective: Implement a Mad Libs generator that replaces placeholders dynamically.");

        Scanner scanner = new Scanner(System.in);
        File folder = new File(STORIES_FOLDER);

        if (!folder.exists() || !folder.isDirectory() || folder.listFiles().length == 0) {
            System.out.println("Error: No stories found in the 'stories' folder.");
            printFooter(ucid, 3);
            scanner.close();
            return;
        }
        List<String> lines = new ArrayList<>();
        // Start edits

        // load a random story file
        File[] stories = folder.listFiles();
        int randomNum = (int)(Math.random()*stories.length);
        File story = stories[randomNum];
        // parse the story lines
        try (Scanner fileScanner = new Scanner(story)){
            while (fileScanner.hasNextLine()){
                lines.add(fileScanner.nextLine());
            }
        } catch (Exception e){
            System.out.println("File could not be read");
            return;
        }
        // iterate through the lines
        for (int i=0; i<lines.size(); i++){
            String line = lines.get(i);
            String newLine = "";
            int start = 0;
            while(line.indexOf("<", start) != -1 && line.indexOf(">", start) != -1){ //checks beginning and end of placeholder
                int placeholderStart = line.indexOf("<", start);
                int placeholderEnd = line.indexOf(">", placeholderStart);

                newLine += line.substring(start, placeholderStart); //this adds the previous text

                String placeholder = line.substring(placeholderStart + 1, placeholderEnd);
                placeholder = placeholder.replace("_", " ");
                System.out.println("Enter -" + placeholder + ": ");
                String userInput = scanner.nextLine();

                newLine += userInput; //update line with user's input

                start = placeholderEnd + 1; // update start index to after current word
            }
            newLine += line.substring(start); //add rest of line back in
            lines.set(i, newLine); // finally update line with new version.
        }
        // prompt the user for each placeholder (note: there may be more than one
        // placeholder in a line)

        // apply the update to the same collection slot

        // End edits
        System.out.println("\nYour Completed Mad Libs Story:\n");
        StringBuilder finalStory = new StringBuilder();
        for (String line : lines) {
            finalStory.append(line).append("\n");
        }
        System.out.println(finalStory.toString());

        printFooter(ucid, 3);
        scanner.close();
    }
}
