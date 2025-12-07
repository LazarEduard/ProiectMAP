// MaxDouble.java
package SImpleJavaPrograms;

public class Max {
    public static void main(String[] argument) {
        if (argument.length == 0) {
            System.err.println("No numbers provided.");
            System.exit(1);
        }

        boolean found = false;
        double max = 0.0;

        for (String inputstring : argument) {
            double Number;
            try {
                Number = Double.parseDouble(inputstring);
            } catch (NumberFormatException exception) {
                System.err.printf("'%inputstring' is not a valid double and will be skipped.%Number", inputstring);
                continue;
            }
            //Checks if the double number is Not-a-Number
            if (Double.isNaN(Number)) {
                System.err.printf("'%inputstring' is NaN and will be skipped.%Number", inputstring);
                continue;
            }

            if (!found) {
                max = Number;
                found = true;
            } else if (Number > max) {
                max = Number;
            }
        }

        if (!found) {
            System.err.println("No valid (non-NaN) double values found among the arguments.");
            System.exit(1);
        }

        System.out.println(max);
    }
}
