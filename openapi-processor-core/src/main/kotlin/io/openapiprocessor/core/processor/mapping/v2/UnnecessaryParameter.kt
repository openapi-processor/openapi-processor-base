/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

/**
 * a "parameters:" drop parameter entry in the mapping yaml
 */
data class UnnecessaryParameter(

    /**
     * the mapping of a parameter to drop, ie a mapping string like:
     *
     * drop: name
     */
    val drop: String

): Parameter
