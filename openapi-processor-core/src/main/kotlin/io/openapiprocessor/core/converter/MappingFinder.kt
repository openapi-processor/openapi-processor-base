/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MappingFinder(mappings: MappingSettings) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    companion object;

    private val repository = MappingRepository(
        mappings.globalMappings,
        mappings.endpointMappings,
        mappings.extensionMappings
    )

    // path/method
    fun getResultTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): ResultTypeMapping? {
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

    fun findResultStyleMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): ResultStyle {
        log.trace("looking for result style mapping {}", query)

        val epMapping = repository.getEndpointResultStyleMapping(query)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalResultStyleMapping()
        if(gMapping != null) {
            return gMapping
        }

        return ResultStyle.SUCCESS
    }

    // path/method
    fun getSingleTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): TypeMapping? {
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

    // path/method
    fun getMultiTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): TypeMapping? {
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

    /**
     * find any type mapping. The mappings are checked in the following order and the first match wins:
     *
     * - endpoint parameter type
     * - endpoint parameter name
     * - endpoint response type
     * - endpoint type
     * - global parameter type
     * - global parameter name
     * - global response type
     * - global type
     */
    fun findAnyTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): TypeMapping? {
        log.trace("looking for any type mapping {}", query)

        val eppMapping = repository.findEndpointParameterTypeMapping(query)
        if (eppMapping != null) {
            return eppMapping
        }

        val eppnMapping = repository.findEndpointParameterNameTypeMapping(query)
        if (eppnMapping != null) {
            return eppnMapping.mapping
        }

        val eprMapping = repository.findEndpointContentTypeMapping(query)
        if (eprMapping != null) {
            return eprMapping.mapping
        }

        val eptMapping = repository.findEndpointTypeMapping(query)
        if (eptMapping != null) {
            return eptMapping
        }

        val gpMapping = repository.findGlobalParameterTypeMapping(query)
        if (gpMapping != null) {
            return gpMapping
        }

        val gpnMapping = repository.findGlobalParameterNameTypeMapping(query)
        if (gpnMapping != null) {
            return gpnMapping.mapping
        }

        val grMapping = repository.findGlobalContentTypeMapping(query)
        if (grMapping != null) {
            return grMapping.mapping
        }

        val gtMapping = repository.findGlobalTypeMapping(query)
        if (gtMapping != null) {
            return gtMapping
        }

        return null
    }

    // path/method/name???/format/type
    fun findTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): TypeMapping? {
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
        return findAnnotationTypeMappings(
            MappingFinderQuery(
                type = type,
                format = format,
                allowObject = allowObject)
        )
    }

    fun findAnnotationTypeMappings(query: io.openapiprocessor.core.converter.mapping.MappingQuery): List<AnnotationTypeMapping> {
        log.trace("looking for annotation type mapping {}", query)

        val epMapping = repository.findEndpointAnnotationTypeMapping(query)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAnnotationTypeMapping(query)
    }

    fun findParameterTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): TypeMapping? {
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
    fun findAnnotationParameterTypeMappings(query: io.openapiprocessor.core.converter.mapping.MappingQuery): List<AnnotationTypeMapping> {
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

    fun findParameterNameTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): NameTypeMapping? {
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

    fun findAnnotationParameterNameTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): List<AnnotationNameMapping> {
        log.trace("looking for annotation parameter name type mapping {}", query)

        val epMapping = repository.findEndpointAnnotationParameterNameTypeMapping(query)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAnnotationParameterNameTypeMapping(query)
    }

    fun findAddParameterTypeMappings(query: io.openapiprocessor.core.converter.mapping.MappingQuery): List<AddParameterTypeMapping> {
        val epMapping = repository.findEndpointAddParameterTypeMappings(query)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAddParameterTypeMappings()
    }

    fun findContentTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): ContentTypeMapping? {
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

    fun findNullTypeMapping(query: io.openapiprocessor.core.converter.mapping.MappingQuery): NullTypeMapping? {
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

    fun isEndpointExcluded(query: io.openapiprocessor.core.converter.mapping.MappingQuery): Boolean {
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
