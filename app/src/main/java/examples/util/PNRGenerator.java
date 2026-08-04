package examples.util;

import java.util.Random;

public final class PNRGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final Random random = new Random();

    private PNRGenerator() {}

    public static String generate() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return sb.toString();
    }
}
