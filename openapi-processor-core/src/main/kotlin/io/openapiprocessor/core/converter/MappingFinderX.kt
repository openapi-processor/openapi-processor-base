/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MappingFinderX(mappings: MappingSettings) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private val repository = MappingRepository(
        mappings.globalMappings,
        mappings.endpointMappings,
        mappings.extensionMappings
    )

    fun getResultTypeMapping(query: MappingQuery): ResultTypeMapping? {
        log.trace("looking for result type mapping {}", query)

        val epMapping = repository.getEndpointResultTypeMapping(query)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalResultTypeMapping()
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun getSingleTypeMapping(query: MappingQuery): TypeMapping? {
        log.trace("looking for single type mapping {}", query)

        val epMapping = repository.getEndpointSingleTypeMapping(query)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalSingleTypeMapping()
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun getMultiTypeMapping(query: MappingQuery): TypeMapping? {
        log.trace("looking for multi type mapping {}", query)

        val epMapping = repository.getEndpointMultiTypeMapping(query)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalMultiTypeMapping()
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findTypeMapping(query: MappingQuery): TypeMapping? {
        log.trace("looking for type mapping {}", query)

        val epMapping = repository.findEndpointTypeMapping(query)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalTypeMapping(query)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findAnnotationTypeMappings(sourceName: String, allowObject: Boolean = false): List<AnnotationTypeMapping> {
        val (type, format) = splitTypeName(sourceName)
        return findAnnotationTypeMappings(MappingQueryX(
            type = type,
            format = format,
            allowObject = allowObject))
    }

    fun findAnnotationTypeMappings(query: MappingQuery): List<AnnotationTypeMapping> {
        log.trace("looking for annotation type mapping {}", query)

        val epMapping = repository.findEndpointAnnotationTypeMapping(query)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAnnotationTypeMapping(query)
    }

    fun findParameterTypeMapping(query: MappingQuery): TypeMapping? {
        log.trace("looking for parameter type mapping {}", query)

        val epMapping = repository.findEndpointParameterTypeMapping(query)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalParameterTypeMapping(query)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    // todo test variants
    fun findAnnotationParameterTypeMappings(query: MappingQuery): List<AnnotationTypeMapping> {
        log.trace("looking for annotation parameter type mapping {}", query)

        val eppMapping = repository.findEndpointAnnotationParameterTypeMappings(query)
        if (eppMapping.isNotEmpty()) {
            return eppMapping
        }

        val epMapping = repository.findEndpointAnnotationTypeMapping(query)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        val pMapping = repository.findGlobalAnnotationParameterTypeMappings(query)
        if (pMapping.isNotEmpty()) {
            return pMapping
        }

        return repository.findGlobalAnnotationTypeMapping(query)
    }

    fun findParameterNameTypeMapping(query: MappingQuery): NameTypeMapping? {
        log.trace("looking for parameter name type mapping {}", query)

        val epMapping = repository.findEndpointParameterNameTypeMapping(query)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalParameterNameTypeMapping(query)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findAnnotationParameterNameTypeMapping(query: MappingQuery): List<AnnotationNameMapping> {
        log.trace("looking for annotation parameter name type mapping {}", query)

        val epMapping = repository.findEndpointAnnotationParameterNameTypeMapping(query)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAnnotationParameterNameTypeMapping(query)
    }

    fun findAddParameterTypeMappings(query: MappingQuery): List<AddParameterTypeMapping> {
        val epMapping = repository.findEndpointAddParameterTypeMappings(query)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAddParameterTypeMappings()
    }

    fun findContentTypeMapping(query: MappingQuery): ContentTypeMapping? {
        val epMapping = repository.findEndpointContentTypeMapping(query)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalContentTypeMapping(query)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findNullTypeMapping(query: MappingQuery): NullTypeMapping? {
        return repository.getEndpointNullTypeMapping(query)
    }

    fun findExtensionAnnotations(extension: String, vararg values: String): List<AnnotationNameMapping> {
        return findExtensionAnnotations(extension, values.asList())
    }

    fun findExtensionAnnotations(extension: String, values: List<String>): List<AnnotationNameMapping> {
        log.trace("looking for annotation extension type mapping {} ({})", extension, values)

        return values
            .map { repository.findExtensionAnnotations(extension, it) }
            .flatten()
    }

    fun isEndpointExcluded(query: MappingQuery): Boolean {
        return repository.isEndpointExcluded(query)
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
