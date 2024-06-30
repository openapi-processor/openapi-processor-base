/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

class ExtensionMappings(val extensions: Map<String /* extension value */, List<AnnotationNameMapping>>) {

    fun get(extensionValue: String): List<AnnotationNameMapping> {
        return extensions[extensionValue] ?: emptyList()
    }
}
