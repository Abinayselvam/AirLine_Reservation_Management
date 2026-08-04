package examples.util;

import java.util.Scanner;

public class EnumUtil {

    private EnumUtil() {
        // Prevent object creation
    }

    public static <T extends Enum<T>> T chooseEnum(Class<T> enumClass, Scanner scanner) {

        T[] values = enumClass.getEnumConstants();

        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i]);
        }

        while (true) {

            System.out.print("Enter Choice: ");

            try {

                int choice = Integer.parseInt(scanner.nextLine());

                if (choice >= 1 && choice <= values.length) {
                    return values[choice - 1];
                }

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid number.");
            }

            System.out.println("Invalid choice. Try again.");
        }
    }
}