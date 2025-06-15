/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

/**
 * package-name options
 */
data class PackageNames(
    /**
     * Java base package name of the generated source files. Sames 'package-name'.
     */
    val base: String? = null,

    /**
     * Java base package name of location-based generated source files
     */
    val location: String? = null
)


