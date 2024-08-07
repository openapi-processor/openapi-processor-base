/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.OptionsConverter

fun parseOptions(mappingYaml: String): ApiOptions {
    return OptionsConverter().convertOptions(mapOf("mapping" to mappingYaml))
}

fun parseOptions(
    version: String =
        """
        |openapi-processor-mapping: v8
        |
        """,
    options: String =
        """
        |options:
        |  package-name: pkg
        |
        """,
    mapping: String
): ApiOptions {
    val merged = (
        version.trimMargin()
      + options.trimMargin()
      + mapping.trimMargin()
    )
    return OptionsConverter().convertOptions(mapOf("mapping" to merged))
}

/** groovy support */

fun parseOptionsMapping(mapping: String): ApiOptions {
    return parseOptions(mapping = mapping)
}
