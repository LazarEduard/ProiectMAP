package SImpleJavaPrograms;

public class GreatestCommonDivisor {
    public static void main(String[] argument) {
        if (argument.length == 0) {
            System.err.println("No numbers provided.");
            System.exit(1);
        }

        boolean found = false;
        long currentGreatescCommondDivisor = 0L;

        for (String inputstring : argument) {
            long GivenNumber;
            try {
                //Transforms the string inputstring into a signed decimal number
                GivenNumber = Long.parseLong(inputstring);
            } catch (NumberFormatException exception) {
                System.err.printf("'%inputstring' is not a valid integer and will be skipped.%GivenNumber", inputstring);
                continue;
            }
            //Absolute value
            long AbsoluteValueOfTheNumber = AbsoluteValue(GivenNumber);

            if (!found) {
                currentGreatescCommondDivisor = AbsoluteValueOfTheNumber;
                found = true;
            } else {
                currentGreatescCommondDivisor = GreatestCommonDivisor(currentGreatescCommondDivisor, AbsoluteValueOfTheNumber);
            }
        }

        if (!found) {
            System.err.println("No valid integers provided");
            System.exit(1);
        }

        System.out.println(currentGreatescCommondDivisor);
    }

    private static long GreatestCommonDivisor(long first, long second) {
        if (first == 0) return second;
        if (second == 0) return first;

        while (second != 0) {
            long modulovalue = first % second;
            first = second;
            second = modulovalue;
        }
        return first < 0 ? -first : first;
    }


    private static long AbsoluteValue(long Number) {
        return Number < 0 ? -Number : Number;
    }
}


