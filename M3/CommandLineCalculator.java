package M3;

/*
Challenge 1: Command-Line Calculator
------------------------------------
- Accept two numbers and an operator as command-line arguments
- Supports addition (+) and subtraction (-)
- Allow integer and floating-point numbers
- Ensures correct decimal places in output based on input (e.g., 0.1 + 0.2 → 1 decimal place)
- Display an error for invalid inputs or unsupported operators
- Capture 5 variations of tests
*/

public class CommandLineCalculator extends BaseClass {
    private static String ucid = "fk222"; // <-- change to your ucid

    public static void main(String[] args) {
        printHeader(ucid, 1, "Objective: Implement a calculator using command-line arguments.");

        if (args.length != 3) {
            System.out.println("Usage: java M3.CommandLineCalculator <num1> <operator> <num2>");
            printFooter(ucid, 1);
            return;
        }

        try {
            System.out.println("Calculating result...");
            // fk222 6/18/25

            // extract the equation (format is <num1> <operator> <num2>)
            String first = args[0];
            String operator = args[1];
            String second = args[2];

            // count amount of decimal places in arguments

            int decimalCount1 = first.contains(".") ? first.length() - first.indexOf('.') - 1 : 0;    
            int decimalCount2 = second.contains(".") ? second.length() - second.indexOf('.') - 1 : 0;    
            int resultDecimalCount = Math.max(decimalCount1, decimalCount2);

            // check if operator is addition or subtraction
            if (!operator.equals("+") && !operator.equals("-")){
                System.out.println("Invalid operator. Program only supports addition or subtraction for now.");
                printFooter(ucid, 1);
                return;
            }
            // check the type of each number and choose appropriate parsing
            Number num1 = first.contains(".") ? Double.parseDouble(first) : Integer.parseInt(first);
            Number num2 = second.contains(".") ? Double.parseDouble(second) : Integer.parseInt(second);
            
            // generate the equation result (Important: ensure decimals display as the
            // longest decimal passed)
            // i.e., 0.1 + 0.2 would show as one decimal place (0.3), 0.11 + 0.2 would shows
            // as two (0.31), etc      
            double n1 = num1.doubleValue();
            double n2 = num2.doubleValue();
            double result = operator.equals("+") ? n1 + n2 : n1 - n2;

            String format = "%." + resultDecimalCount + "f";
            System.out.printf("= " + format, result);
            System.out.println();

        } catch (Exception e) {
            System.out.println("Invalid input. Please ensure correct format and valid numbers.");
        }

        printFooter(ucid, 1);
    }
}
