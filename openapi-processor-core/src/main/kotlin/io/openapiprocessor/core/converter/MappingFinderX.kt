/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*

class MappingFinderX(private val repository: MappingRepository) {

    fun getResultTypeMapping(schema: MappingSchema): ResultTypeMapping? {
        val epMapping = repository.getEndpointResultTypeMapping(schema)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalResultTypeMapping()
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun getSingleTypeMapping(schema: MappingSchema): TypeMapping? {
        val epMapping = repository.getEndpointSingleTypeMapping(schema)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalSingleTypeMapping()
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun getMultiTypeMapping(schema: MappingSchema): TypeMapping? {
        val epMapping = repository.getEndpointMultiTypeMapping(schema)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.getGlobalMultiTypeMapping()
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findTypeMapping(schema: MappingSchema): TypeMapping? {
        val epMapping = repository.findEndpointTypeMapping(schema)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalTypeMapping(schema)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findAnnotationTypeMapping(schema: MappingSchema): List<AnnotationTypeMapping> {
        val epMapping = repository.findEndpointAnnotationTypeMapping(schema)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAnnotationTypeMapping(schema)
    }

    fun findParameterTypeMapping(schema: MappingSchema): TypeMapping? {
        val epMapping = repository.findEndpointParameterTypeMapping(schema)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalParameterTypeMapping(schema)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findAnnotationParameterTypeMapping(schema: MappingSchema): List<AnnotationTypeMapping> {
        val epMapping = repository.findEndpointAnnotationParameterTypeMapping(schema)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAnnotationParameterTypeMapping(schema)
    }

    fun findParameterNameTypeMapping(schema: MappingSchema): NameTypeMapping? {
        val epMapping = repository.findEndpointParameterNameTypeMapping(schema)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalParameterNameTypeMapping(schema)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findAddParameterTypeMappings(schema: MappingSchema): List<AddParameterTypeMapping> {
        val epMapping = repository.findEndpointAddParameterTypeMappings(schema)
        if (epMapping.isNotEmpty()) {
            return epMapping
        }

        return repository.findGlobalAddParameterTypeMappings()
    }

    fun findContentTypeMapping(schema: MappingSchema): ContentTypeMapping? {
        val epMapping = repository.findEndpointContentTypeMapping(schema)
        if (epMapping != null) {
            return epMapping
        }

        val gMapping = repository.findGlobalContentTypeMapping(schema)
        if(gMapping != null) {
            return gMapping
        }

        return null
    }

    fun findExtensionAnnotations(extension: String, value: String): List<AnnotationNameMapping> {
        return repository.findExtensionAnnotations(extension, value)
    }

    fun isEndpointExcluded(schema: MappingSchema): Boolean {
        return repository.isEndpointExcluded(schema)
    }
}
