package examples.util;

import java.util.Map;

public final class PromoCodeUtil {

    private static final Map<String, Double> CODES = Map.of(
            "SAVE10", 0.10,
            "FLY500", 0.05,
            "WELCOME", 0.15
    );

    private PromoCodeUtil() {}

    public static double discountFor(String code) {

        if (code == null || code.isBlank()) {
            return 0;
        }

        return CODES.getOrDefault(code.trim().toUpperCase(), 0.0);
    }
}