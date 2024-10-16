/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.StringStep

class ExtensionMappings(val extensions: Map<String /* extension value */, List<AnnotationNameMapping>>) {

    fun get(extensionValue: String, step: MappingStep): List<AnnotationNameMapping> {
        val values = extensions[extensionValue] ?: return emptyList()
        values.forEach { value ->
            step.add(StringStep("${value.name} @ ${value.annotation}", true))
        }
        return values
    }
}
