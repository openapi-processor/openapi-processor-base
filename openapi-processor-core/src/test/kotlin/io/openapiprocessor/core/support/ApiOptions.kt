/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.OptionsConverter

fun parseOptions(mappingYaml: String): ApiOptions {
    return OptionsConverter().convertOptions(mapOf(
        "targetDir" to "defaultTargetDir",
        "mapping" to mappingYaml
    ))
}

fun parseOptions(
    targetDir: String = "defaultTargetDir",
    version: String =
        """
        |openapi-processor-mapping: v16
        |
        """,
    options: String =
        """
        |options:
        |  package-name: pkg
        |
        """,
    mapping: String = "",
): ApiOptions {
    val merged = (
        version.trimMargin()
      + options.trimMargin()
      + mapping.trimMargin()
    )
    return OptionsConverter().convertOptions(mapOf(
        "targetDir" to targetDir,
        "mapping" to merged
    ))
}

/** groovy support */

fun parseOptionsMapping(mapping: String): ApiOptions {
    return parseOptions(mapping = mapping)
}
