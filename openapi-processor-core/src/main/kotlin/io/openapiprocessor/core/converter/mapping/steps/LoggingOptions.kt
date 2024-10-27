/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping.steps

enum class Target {
    LOGGER, STDOUT
}

interface LoggingOptions {
    var mapping: Boolean
    var mappingTarget: Target
}
