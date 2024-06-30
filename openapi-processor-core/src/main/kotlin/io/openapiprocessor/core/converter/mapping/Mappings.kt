/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Mappings(
    private val resultTypeMapping: ResultTypeMapping? = null,
    private val resultStyle: ResultStyle? = null,
    private val singleTypeMapping: TypeMapping? = null,
    private val multiTypeMapping: TypeMapping? = null,
    private val typeMappings: TypeMappings = TypeMappings(emptyList()),
    private val parameterTypeMappings: TypeMappings = TypeMappings(emptyList()),
    private val responseTypeMappings: TypeMappings = TypeMappings(emptyList()),
    private val exclude: Boolean = false
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getResultTypeMapping(): ResultTypeMapping? {
        log.trace("looking for result type mapping")
        return resultTypeMapping
    }

    fun getResultStyle(): ResultStyle? {
        log.trace("looking for result style")
        return resultStyle
    }

    fun getSingleTypeMapping(): TypeMapping? {
        log.trace("looking for single mapping")
        return singleTypeMapping
    }

    fun getMultiTypeMapping(): TypeMapping? {
        log.trace("looking for multi mapping")
        return multiTypeMapping
    }

    fun findTypeMapping(filter: MappingMatcher): TypeMapping? {
        log.trace("looking for type mapping of {}", filter)

        val mappings = typeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as TypeMapping
    }

    fun findAnnotationTypeMapping(filter: MappingMatcher): List<AnnotationTypeMapping> {
        log.trace("looking for annotation type mapping of {}", filter)

        val mappings = typeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationTypeMapping }
    }

    fun findParameterTypeMapping(filter: MappingMatcher): TypeMapping? {
        log.trace("looking for parameter type mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as TypeMapping
    }

    fun findAnnotationParameterTypeMapping(filter: MappingMatcher): List<AnnotationTypeMapping> {
        log.trace("looking for annotation parameter type mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationTypeMapping }
    }

    fun findParameterNameTypeMapping(filter: MappingMatcher): NameTypeMapping? {
        log.trace("looking for parameter mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as NameTypeMapping
    }

    fun findAnnotationParameterNameTypeMapping(filter: MappingMatcher): List<AnnotationNameMapping> {
        log.trace("looking for annotation parameter name mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationNameMapping }
    }

    fun findAddParameterTypeMappings(filter: MappingMatcher): List<AddParameterTypeMapping>  {
        log.trace("looking for global additional parameter mapping of {}", filter)

        val mappings = parameterTypeMappings.filter(filter)
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AddParameterTypeMapping }
    }

    fun findContentTypeMapping(filter: MappingMatcher): ContentTypeMapping? {
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

    fun isExcluded(): Boolean {
        return exclude
    }
}
