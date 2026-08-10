package examples.util;

import examples.exception.InvalidInputException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class InputValidator {

    private InputValidator() {}

    public static int readInt(Scanner sc, String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = sc.nextLine();

            try {

                return Integer.parseInt(input.trim());

            } catch (NumberFormatException e) {

                System.out.println("Please enter a whole number");
            }
        }
    }

    public static int readIntInRange(Scanner sc, String prompt, int min, int max) {

        while (true) {

            int value = readInt(sc, prompt);

            if (value < min || value > max) {

                System.out.println("Enter a number between " + min + " and " + max);

                continue;
            }

            return value;
        }
    }

    public static LocalDate readDate(Scanner sc, String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = sc.nextLine();

            try {

                return LocalDate.parse(input.trim());

            } catch (DateTimeParseException e) {

                System.out.println("Invalid date format - use yyyy-MM-dd (e.g. 2026-08-25)");
            }
        }
    }

    /** Returns null on a blank line, so callers can treat "no change" separately from "invalid". */
    public static LocalDate readOptionalDate(Scanner sc, String prompt) {

        System.out.print(prompt);

        String input = sc.nextLine();

        if (input.isBlank()) {
            return null;
        }

        try {

            return LocalDate.parse(input.trim());

        } catch (DateTimeParseException e) {

            throw new InvalidInputException("Date", "expected yyyy-MM-dd, got '" + input + "'");
        }
    }

    public static String readAirportCode(Scanner sc, String prompt) {

        while (true) {

            System.out.print(prompt);

            String code = sc.nextLine().trim().toUpperCase();

            if (!code.matches("[A-Z]{3,4}")) {

                System.out.println("Airport code should be 3-4 letters (e.g. DEL, BLR)");

                continue;
            }

            if (examples.util.AirportIndex.getByCode(code) == null) {

                System.out.println("No airport found with code " + code + " - check spelling or add it via Airport Management");

                continue;
            }

            return code;
        }
    }

    public static String readNonBlank(Scanner sc, String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = sc.nextLine();

            if (!input.isBlank()) {
                return input;
            }

            System.out.println("This field cannot be blank");
        }
    }
}