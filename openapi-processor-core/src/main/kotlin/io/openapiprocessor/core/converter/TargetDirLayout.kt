/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

enum class TargetDirLayout {
    CLASSIC,  // targetDir/{packages}
    STANDARD;  // targetDir/java/{packages}, targetDir/resources

    companion object {
        fun isStandard(layout: String): Boolean {
            return STANDARD.name.lowercase() == layout
        }
    }
}
