/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.logging.Logger
import org.slf4j.event.Level

/**
 * remember logging calls.
 */
class TestLogger: Logger {
    data class Message(val level: Level, val message: String?, val arguments: Any?)

    private val messages = mutableListOf<Message>()

    override fun error(message: String?) {
        messages.add(Message(Level.ERROR, message, null))
    }

    override fun error(format: String, vararg arguments: Any?) {
        messages.add(Message(Level.ERROR, format, arguments))
    }

    override fun warn(format: String, vararg arguments: Any?) {
        messages.add(Message(Level.WARN, format, arguments))
    }

    override fun info(message: String?) {
        messages.add(Message(Level.INFO, message, null))
    }

    override fun debug(message: String?) {
        messages.add(Message(Level.DEBUG, message, null))
    }

    override fun debug(format: String, vararg arguments: Any?) {
        messages.add(Message(Level.DEBUG, format, arguments))
    }

    fun getMessages(): List<Message> {
        return messages
    }
}
