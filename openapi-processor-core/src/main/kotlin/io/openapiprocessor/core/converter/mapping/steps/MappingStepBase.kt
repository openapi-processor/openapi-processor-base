/*
 * Copyright 2024 https://github.com/openapi-processor-base/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

import org.slf4j.LoggerFactory

abstract class MappingStepBase: MappingStep {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun log(message: String, vararg args: Any?) {
        // would like to log this as debug or info, but gradle can only globally enable/disable log levels.
        log.warn(message, *args)
    }
}
