/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */
package io.openapiprocessor.core.logging

import org.slf4j.event.Level
import java.io.IOException
import java.util.*

/**
 * log leve can be configured with `messagerlogger.properties`. It does allow a single property:
 * ```
 * log.level=error|warn|info|debug|trace
 * ```
 */
class MessagerLoggerConfiguration {
    private var initialized = false
    private val properties = Properties()

    var logLevel = Level.INFO

    fun init() {
        if (initialized) {
            return
        }
        loadProperties()
        initLogLevel()
        initialized = true
    }

    private fun initLogLevel() {
        val ll = properties.getProperty("log.level") ?: return

        when (ll.uppercase(Locale.getDefault())) {
            "ERROR" -> logLevel = Level.ERROR
            "WARN" -> logLevel = Level.WARN
            "INFO" -> logLevel = Level.INFO
            "DEBUG" -> logLevel = Level.DEBUG
            "TRACE" -> logLevel = Level.TRACE
        }
    }

    private fun loadProperties() {
        try {
            val classLoader = Thread.currentThread().contextClassLoader
            properties.load(classLoader.getResourceAsStream(CONFIG_FILE_NAME))
        } catch (ignored: IOException) {
            // ignore, use defaults
        }
    }

    companion object {
        private const val CONFIG_FILE_NAME = "messagerlogger.properties"
    }
}
