/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.ParametersStep
import io.openapiprocessor.core.converter.mapping.steps.TypesStep
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Mappings(
    private val resultTypeMapping: ResultTypeMapping? = null,
    private val resultStyle: ResultStyle? = null,
    private val singleTypeMapping: TypeMapping? = null,
    private val multiTypeMapping: TypeMapping? = null,
    private val nullTypeMapping: NullTypeMapping? = null,
    private val typeMappings: TypeMappings = TypeMappings(),
    private val parameterTypeMappings: TypeMappings = TypeMappings(),
    private val responseTypeMappings: TypeMappings = TypeMappings(),
    private val exclude: Boolean = false
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getResultTypeMapping(): ResultTypeMapping? {
        return resultTypeMapping
    }

    fun getResultStyle(): ResultStyle? {
        return resultStyle
    }

    fun getSingleTypeMapping(): TypeMapping? {
        return singleTypeMapping
    }

    fun getMultiTypeMapping(): TypeMapping? {
        return multiTypeMapping
    }

    fun getNullTypeMapping(): NullTypeMapping? {
        return nullTypeMapping
    }

    fun findTypeMapping(filter: MappingMatcher, step: MappingStep): TypeMapping? {
        val mappings = typeMappings.filter(filter, step.add(TypesStep()))
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as TypeMapping
    }

    fun findAnnotationTypeMapping(filter: MappingMatcher, step: MappingStep): List<AnnotationTypeMapping> {
        val mappings = typeMappings.filter(filter, step.add(TypesStep()))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationTypeMapping }
    }

    fun findParameterTypeMapping(filter: MappingMatcher, step: MappingStep): TypeMapping? {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep()))
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as TypeMapping
    }

    fun findAnnotationParameterTypeMapping(filter: MappingMatcher, step: MappingStep): List<AnnotationTypeMapping> {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep()))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationTypeMapping }
    }

    fun findParameterNameTypeMapping(filter: MappingMatcher, step: MappingStep): NameTypeMapping? {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep()))
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as NameTypeMapping
    }

    fun findAnnotationParameterNameTypeMapping(filter: MappingMatcher, step: MappingStep): List<AnnotationNameMapping> {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep()))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationNameMapping }
    }

    fun findAddParameterTypeMappings(filter: MappingMatcher, step: MappingStep): List<AddParameterTypeMapping>  {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep()))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AddParameterTypeMapping }
    }

    fun findContentTypeMapping(filter: MappingMatcher, step: MappingStep): ContentTypeMapping? {
        val mappings = responseTypeMappings.filter(filter, step)
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
