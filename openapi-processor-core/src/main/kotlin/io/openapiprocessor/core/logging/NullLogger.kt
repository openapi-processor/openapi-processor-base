/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.logging

/**
 * eats logging calls.
 */
class NullLogger: Logger {

    override fun error(message: String?) {}

    override fun error(format: String, vararg arguments: Any?) {}

    override fun warn(format: String, vararg arguments: Any?) {}

    override fun info(message: String?) {}

    override fun debug(message: String?) {}

    override fun debug(format: String, vararg arguments: Any?) {}
}
