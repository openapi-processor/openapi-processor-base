/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.parser.HttpMethod

/**
 * find mappings in the type mapping list.
 */
class MappingFinder(private val typeMappings: List<Mapping> = emptyList()) {

    fun findParameterTypeAnnotations(path: String, method: HttpMethod?, typeName: String): List<AnnotationTypeMapping> {
        val epMappings = findEndpointMappings(typeMappings, path, method)
        if (epMappings.isNotEmpty()) {
            val am = findParameterTypeAnnotations(epMappings, typeName)
            if (am.isNotEmpty())
                return am
        }

        return findParameterTypeAnnotations(typeMappings, typeName)
    }

    fun findParameterNameAnnotations(path: String, method: HttpMethod?, parameterName: String): List<AnnotationNameMapping> {
        val epMappings = findEndpointMappings(typeMappings, path, method)
        if (epMappings.isNotEmpty()) {
            val am = findParameterNameAnnotations(epMappings, parameterName)
            if (am.isNotEmpty())
                return am
        }

        return findParameterNameAnnotations(typeMappings, parameterName)
    }

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

    private fun findEndpointMappings(typeMappings: List<Mapping>, path: String, method: HttpMethod?): List<Mapping> {
        // find with method
        var epMappings = typeMappings
            .filterIsInstance<EndpointTypeMapping>()
            .filter { it.path == path && it.method == method }

        // find without method
        if (epMappings.isEmpty()) {
            epMappings = typeMappings
                .filterIsInstance<EndpointTypeMapping>()
                .filter { it.path == path && it.method == null }
        }

        return epMappings
            .map { it.getChildMappings() }
            .flatten()
    }

    private fun findTypeAnnotations(typeMappings: List<Mapping>, typeName: String, allowObject: Boolean = false)
        : List<AnnotationTypeMapping> {

        val (type, format) = splitTypeName(typeName)
        return typeMappings
            .filterIsInstance<AnnotationTypeMapping>()
            .filter {
                val matchObject = it.sourceTypeName == "object"
                val matchType = it.sourceTypeName == type
                val matchFormat = it.sourceTypeFormat == format

                (matchType && matchFormat) || (allowObject && matchObject)
            }
    }

    private fun findParameterTypeAnnotations(typeMappings: List<Mapping>, typeName: String): List<AnnotationTypeMapping> {
        val (type, format) = splitTypeName(typeName)
        return typeMappings
            .filterIsInstance<AnnotationTypeMapping>()
            .filter {
                val matchType = it.sourceTypeName == type
                val matchFormat = it.sourceTypeFormat == format
                matchType && matchFormat
            }
    }

    private fun findParameterNameAnnotations(typeMappings: List<Mapping>, parameterName: String): List<AnnotationNameMapping> {
        return typeMappings
            .filterIsInstance<AnnotationNameMapping>()
            .filter {
                parameterName == it.name
            }
    }

    private fun findExtensionMappings(typeMappings: List<Mapping>, extensionName: String): ExtensionMapping? {
        return typeMappings
            .filterIsInstance<ExtensionMapping>()
            .firstOrNull {
                extensionName == it.extension
            }
    }

    private fun splitTypeName(typeName: String): Pair<String, String?> {
        val split = typeName
                .split(":")
                .map { it.trim() }

        val type = split.component1()
        var format: String? = null
        if (split.size == 2) {
            format = split.component2()
        }

        return Pair(type, format)
    }
}
