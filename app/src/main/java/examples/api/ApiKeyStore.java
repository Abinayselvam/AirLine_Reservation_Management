package examples.api;

import java.util.Set;

public final class ApiKeyStore {

    // Demo keys only - a real system loads these from a secrets manager, never hardcoded.
    private static final Set<String> VALID_KEYS = Set.of("demo-key-123", "test-key-456");

    private ApiKeyStore() {}

    public static boolean isValid(String key) {
        return key != null && VALID_KEYS.contains(key);
    }
}