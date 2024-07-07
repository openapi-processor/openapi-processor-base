/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.AnnotationNameMapping
import io.openapiprocessor.core.converter.mapping.ExtensionMapping
import io.openapiprocessor.core.converter.mapping.Mapping

/**
 * find mappings in the type mapping list.
 */
class MappingFinder(private val typeMappings: List<Mapping> = emptyList()) {

    fun findExtensionAnnotations(key: String, values: List<String>): List<AnnotationNameMapping> {
        val extMappings = findExtensionMappings(typeMappings, key)
        if (extMappings == null)
            return emptyList()

        return extMappings.mappings
            .filterIsInstance<AnnotationNameMapping>()
            .filter{ values.contains(it.name) }
    }

    fun findExtensionAnnotations(key: String, vararg values: String): List<AnnotationNameMapping> {
        return findExtensionAnnotations(key, values.asList())
    }

    private fun findExtensionMappings(typeMappings: List<Mapping>, extensionName: String): ExtensionMapping? {
        return typeMappings
            .filterIsInstance<ExtensionMapping>()
            .firstOrNull {
                extensionName == it.extension
            }
    }
}
