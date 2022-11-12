/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.logging

interface LoggerFactory {
    companion object {
        var factory: LoggerFactory? = null

        fun getLogger(name: String): Logger {
            return factory?.getLogger(name) ?: NullLogger()
        }
    }

    fun getLogger(name: String): Logger
}
