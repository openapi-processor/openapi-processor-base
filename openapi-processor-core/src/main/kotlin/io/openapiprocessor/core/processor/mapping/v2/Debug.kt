/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

data class Debug(
    /**
     * enable/disable tracing of mapping lookups (optional).
     */
    val mapping: Boolean = false
)
