package com.ynov.kiwi.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) { System.err.println("Erreur chargement config: " + e.getMessage()); }
    }

    public static String get(String key) { return props.getProperty(key); }
}

