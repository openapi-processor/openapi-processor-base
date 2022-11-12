/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.logging

interface Logger {
    fun error(message: String?)
    fun error(format: String, vararg arguments: Any?)

    fun warn(format: String, vararg arguments: Any?)

    fun info(message: String?)

    fun debug(message: String?)
    fun debug(format: String, vararg arguments: Any?)
}
