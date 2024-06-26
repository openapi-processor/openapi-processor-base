/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Mappings(
    private val resultTypeMapping: ResultTypeMapping?,
    private val resultStyle: ResultStyle?,
    private val singleTypeMapping: TypeMapping?,
    private val multiTypeMapping: TypeMapping?,
    private val typeMappings: TypeMappings,
    private val parameterTypeMappings: TypeMappings,
    private val responseTypeMappings: TypeMappings
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getGlobalResultTypeMapping(): ResultTypeMapping? {
        return resultTypeMapping
    }

    fun getGlobalResultStyle(): ResultStyle? {
        return resultStyle
    }

    fun getGlobalSingleTypeMapping(): TypeMapping? {
        return singleTypeMapping
    }

    fun getGlobalMultiTypeMapping(): TypeMapping? {
        return multiTypeMapping
    }

    fun findGlobalTypeMapping(filter: MappingMatcher): TypeMapping? {
        log.trace("looking for global type mapping of {}", filter)

        val mappings = typeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as TypeMapping
    }

    fun findGlobalAnnotationTypeMapping(filter: MappingMatcher): List<AnnotationTypeMapping> {
        log.trace("looking for global annotation type mapping of {}", filter)

        val mappings = typeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationTypeMapping }
    }

    fun findGlobalParameterTypeMapping(filter: MappingMatcher): TypeMapping? {
        log.trace("looking for global parameter type mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as TypeMapping
    }

    fun findGlobalAnnotationParameterTypeMapping(filter: MappingMatcher): List<AnnotationTypeMapping> {
        log.trace("looking for global annotation parameter type mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationTypeMapping }
    }

    fun findGlobalParameterNameTypeMapping(filter: MappingMatcher): NameTypeMapping? {
        log.trace("looking for global parameter mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as NameTypeMapping
    }

    fun findGlobalAnnotationParameterNameTypeMapping(filter: MappingMatcher): List<AnnotationNameMapping> {
        log.trace("looking for global annotation parameter name mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationNameMapping }
    }

    fun findGlobalAddParameterTypeMappings(filter: MappingMatcher): List<AddParameterTypeMapping>  {
        log.trace("looking for global additional parameter mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AddParameterTypeMapping }
    }

    fun findGlobalResponseTypeMapping(filter: MappingMatcher): ContentTypeMapping? {
        log.trace("looking for global response type mapping of {}", filter)

        val mappings = responseTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as ContentTypeMapping
    }
}
