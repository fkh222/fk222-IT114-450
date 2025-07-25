package module2;

public class Problem3 extends BaseClass {
    private static Integer[] array1 = {42, -17, 89, -256, 1024, -4096, 50000, -123456};
    private static Double[] array2 = {3.14159265358979, -2.718281828459, 1.61803398875, -0.5772156649, 0.0000001, -1000000.0};
    private static Float[] array3 = {1.1f, -2.2f, 3.3f, -4.4f, 5.5f, -6.6f, 7.7f, -8.8f};
    private static String[] array4 = {"123", "-456", "789.01", "-234.56", "0.00001", "-99999999"};
    private static Object[] array5 = {-1, 1, 2.0f, -2.0d, "3", "-3.0"};
    private static void bePositive(Object[] arr, int arrayNumber) {
        // Only make edits between the designated "Start" and "End" comments
        printArrayInfo(arr, arrayNumber);

        // Challenge 1: Make each value positive
        // Challenge 2: Convert the values back to their original data type and assign it to the proper slot of the `output` array
        // Step 1: sketch out plan using comments (include ucid and date)
        // Step 2: Add/commit your outline of comments (required for full credit)
        // Step 3: Add code to solve the problem (add/commit as needed)
        Object[] output = new Object[arr.length];
        // Start Solution Edits

        // fk222 06/16/25

        // 1. Loop through each value of the given array
        // 2. Use "instance of" in if statement to check the value's original object type
        // 3. Convert the value to positive number
        // 4. Convert the value back to its original type
        // 5. Add it to output array in correct spot

        for (int i=0; i<arr.length; i++){
            Object x = arr[i];

            if (x instanceof Integer){
                output[i]=Math.abs((Integer) x);
            }
            else if (x instanceof Double) {
                output[i]=Math.abs((Double) x);
            }
            else if (x instanceof Float){
                output[i]=Math.abs((Float) x);
            }
            else if (x instanceof String){
                // checks for decimal point
                // .parseDouble or .parseInt convert string to respective object type
                // String.valueOf turns value back to string
                if(((String) x).contains(".")) {
                    output[i]=String.valueOf(Math.abs(Double.parseDouble((String) x)));
                }
                // no decimal, then it is integer
                else {
                    output[i]=String.valueOf(Math.abs(Integer.parseInt((String) x)));

                } 
            }   

        }
        

        // End Solution Edits
        System.out.println("Output: ");
        printOutputWithType(output);
        System.out.println("");
        System.out.println("______________________________________");
    }

    public static void main(String[] args) {
        final String ucid = "fk222"; // <-- change to your UCID
        // no edits below this line
        printHeader(ucid, 3);
        bePositive(array1, 1);
        bePositive(array2, 2);
        bePositive(array3, 3);
        bePositive(array4, 4);
        bePositive(array5, 5);
        printFooter(ucid, 3);

    }
}