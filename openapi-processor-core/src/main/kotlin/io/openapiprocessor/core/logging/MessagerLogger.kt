/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.logging

import org.slf4j.event.Level
import org.slf4j.helpers.MessageFormatter
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * [Messager] logger implementation. Used when running as annotation processor.
 */
class MessagerLogger(private val messager: Messager, private val name: String) : Logger {
    private var config = MessagerLoggerConfiguration()

    init {
        config.init()
    }

    override fun error(message: String?) {
        log(Level.ERROR, message)
    }

    override fun error(format: String, vararg arguments: Any?) {
        log(Level.ERROR, format, arguments)
    }

    override fun warn(format: String, vararg arguments: Any?) {
        log(Level.WARN, format, arguments)
    }

    override fun info(message: String?) {
        log(Level.INFO, message)
    }

    override fun debug(message: String?) {
        log(Level.DEBUG, message)
    }

    override fun debug(format: String, vararg arguments: Any?) {
        log(Level.DEBUG, format, arguments)
    }

    private fun log(level: Level, format: String?, vararg arguments: Any?) {
        if (isLevelEnabled(level)) {
            val (args, throwable) = extractThrowable(arguments)
            val formatted = MessageFormatter.arrayFormat (format, args, throwable)
            messager.printMessage(mapToKind(level), formatted.message)
        }
    }

    private fun extractThrowable (vararg arguments: Any?): Pair<Array<out Any?>, Throwable?> {
        val last = arguments.last()
        return if (last is Throwable) {
            Pair(arguments.copyOfRange(0, arguments.size - 1), last)
        } else {
            Pair(arguments, null)
        }
    }

    private fun mapToKind(level: Level): Diagnostic.Kind {
        return when (level) {
            Level.ERROR -> Diagnostic.Kind.ERROR
            Level.WARN -> Diagnostic.Kind.WARNING
            Level.INFO -> Diagnostic.Kind.NOTE
            Level.DEBUG, Level.TRACE -> Diagnostic.Kind.OTHER
            else -> Diagnostic.Kind.NOTE
        }
    }

    fun isErrorEnabled(): Boolean {
        return isLevelEnabled(Level.ERROR)
    }

    fun isWarnEnabled(): Boolean {
        return isLevelEnabled(Level.WARN)
    }

    fun isInfoEnabled(): Boolean {
        return isLevelEnabled(Level.INFO)
    }

    fun isDebugEnabled(): Boolean {
        return isLevelEnabled(Level.DEBUG)
    }

    fun isTraceEnabled(): Boolean {
        return isLevelEnabled(Level.TRACE)
    }

    private fun isLevelEnabled(logLevel: Level): Boolean {
        return logLevel.toInt() >= config.logLevel.toInt()
    }
}
