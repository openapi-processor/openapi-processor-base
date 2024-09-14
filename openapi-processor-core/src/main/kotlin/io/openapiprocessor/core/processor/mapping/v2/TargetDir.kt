/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

data class TargetDir(
    /**
     * enable/disable clearing of targetDir (optional).
     */
    val clear: Boolean? = null,

    /**
     * layout of targetDir.
     * classic: targetDir/packages
     * standard: targetDir/java/packages & targetDir/resources
     */
    val layout: String = "classic"
)
