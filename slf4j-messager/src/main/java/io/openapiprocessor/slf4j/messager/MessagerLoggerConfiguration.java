/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.slf4j.messager;

import org.slf4j.event.Level;

import java.io.IOException;
import java.util.Properties;

public class MessagerLoggerConfiguration {
    private static final String CONFIG_FILE_NAME = "messagerlogger.properties";

    private boolean initialized = false;

    private final Properties properties = new Properties();

    private Level logLevel = Level.INFO;


    void init() {
        if (initialized) {
            return;
        }

        loadProperties ();
        initLogLevel ();
        initialized = true;
    }

    Level getLogLevel() {
        return logLevel;
    }

    private void initLogLevel () {
        String ll = properties.getProperty ("log.level");
        if (ll == null) {
            return;
        }

        switch (ll.toUpperCase ()) {
            case "ERROR":
                logLevel = Level.ERROR;
                break;
            case "WARN":
                logLevel = Level.WARN;
                break;
            case "INFO":
                logLevel = Level.INFO;
                break;
            case "DEBUG":
                logLevel = Level.DEBUG;
                break;
            case "TRACE":
                logLevel = Level.TRACE;
                break;
        }
    }

    private void loadProperties () {
        try {
            ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
            properties.load (classLoader.getResourceAsStream (CONFIG_FILE_NAME));
        } catch (IOException ignored) {
            // ignore, use defaults
        }
    }
}
