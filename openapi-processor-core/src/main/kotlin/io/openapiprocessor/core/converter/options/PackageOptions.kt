/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.options

class PackageOptions {
    /**
     * Java base package name of the generated source files. Sames 'package-name'.
     */
    var base: String? = null

    /**
     * Java base package name of location-based generated source files
     */
    var location: String? = null

    val fromLocation: Boolean
        get() = location != null
}
