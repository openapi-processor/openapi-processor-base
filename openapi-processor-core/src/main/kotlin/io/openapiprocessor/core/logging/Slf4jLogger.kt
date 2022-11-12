/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.logging

/**
 * slf4j logger implementation. Used when running from gradle/maven plugin.
 */
class Slf4jLogger(private val logger: org.slf4j.Logger) : Logger {

    override fun error(message: String?) {
        logger.error(message)
    }

    override fun error(format: String, vararg arguments: Any?) {
        logger.error(format, arguments)
    }

    override fun warn(format: String, vararg arguments: Any?) {
        logger.warn(format, arguments)
    }

    override fun info(message: String?) {
        logger.info(message)
    }

    override fun debug(message: String?) {
        logger.debug(message)
    }

    override fun debug(format: String, vararg arguments: Any?) {
        logger.debug(format, arguments)
    }
}
