package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Resolves config values in this order (first match wins):
 * 1. Environment variable   (userPassword -> USER_PASSWORD)
 * 2. JVM system property    (-DuserPassword=...)
 * 3. local.properties       (git-ignored, for local development secrets)
 * 4. testdata.properties    (committed, NON-secret values only)
 */
public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        load("src/test/resources/testdata.properties", true);
        load("local.properties", false);
    }

    private static void load(String path, boolean required) {
        try (FileInputStream in = new FileInputStream(path)) {
            properties.load(in);
        } catch (IOException e) {
            if (required) {
                throw new RuntimeException("Failed to load properties file: " + e.getMessage());
            }
        }
    }

    public static String getProperty(String key) {
        String envName = key.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase();
        String value = System.getenv(envName);
        if (isSet(value)) return value;

        value = System.getProperty(key);
        if (isSet(value)) return value;

        return properties.getProperty(key);
    }

    private static boolean isSet(String v) {
        return v != null && !v.isBlank();
    }
}
