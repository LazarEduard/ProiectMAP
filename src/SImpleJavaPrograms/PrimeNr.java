package SImpleJavaPrograms;


public class PrimeNr {

     static void main(String[] argument) {
         for (int index = 0; index < argument.length; index++)
         {String current = argument[index];
             int Number = Integer.parseInt(current);

            if (Number >= 1){
               boolean Prime = true;
             for (int i = 2; i <= Number-1; i++) {
                if (Number % i == 0){
                    Prime = false;
                     break;
            }}
             if (Prime)
                System.out.println(Number);
            }
        }
    }
}
