/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.steps.*
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

class Mappings(
    private val resultTypeMapping: ResultTypeMapping? = null,
    private val resultStyle: ResultStyle? = null,
    private val resultStatus: Boolean? = null,
    private val singleTypeMapping: TypeMapping? = null,
    private val multiTypeMapping: TypeMapping? = null,
    private val nullTypeMapping: NullTypeMapping? = null,
    private val typeMappings: TypeMappings = TypeMappings(),
    private val schemaMappings: TypeMappings = TypeMappings(),
    private val parameterTypeMappings: TypeMappings = TypeMappings(),
    private val responseTypeMappings: TypeMappings = TypeMappings(),
    private val exclude: Boolean = false
) {
    fun getResultTypeMapping(step: MappingStep): ResultTypeMapping? {
        if (resultTypeMapping != null) {
            step.add(StringStep("result-type: ${resultTypeMapping.targetTypeName}", true))
        }
        return resultTypeMapping
    }

    fun getResultStyle(step: MappingStep): ResultStyle? {
        if (resultStyle != null) {
            step.add(StringStep("result-style: $resultStyle", true))
        }
        return resultStyle
    }

    fun getResultStatus(step: MappingStep): Boolean? {
        if (resultStatus != null) {
            step.add(StringStep("result-status: $resultStatus", true))
        }
        return resultStatus
    }

    fun getSingleTypeMapping(step: MappingStep): TypeMapping? {
        if (singleTypeMapping != null) {
            step.add(StringStep("single: ${singleTypeMapping.targetTypeName}", true))
        }
        return singleTypeMapping
    }

    fun getMultiTypeMapping(step: MappingStep): TypeMapping? {
        if (multiTypeMapping != null) {
            step.add(StringStep("multi: ${multiTypeMapping.targetTypeName}", true))
        }
        return multiTypeMapping
    }

    fun getNullTypeMapping(step: MappingStep): NullTypeMapping? {
        if (nullTypeMapping != null) {
            step.add(StringStep("null: $nullTypeMapping", true))
        }
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

    fun findInterfaceTypeMappings(filter: MappingMatcher, step: MappingStep): List<InterfaceTypeMapping> {
        val mappings = typeMappings.filter(filter, step.add(TypesStep()))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as InterfaceTypeMapping }
    }

    fun findSchemaTypeMapping(filter: MappingMatcher, step: MappingStep): TypeMapping? {
        val mappings = schemaMappings.filter(filter, step.add(SchemasStep()))
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as TypeMapping
    }

    fun findAnnotationSchemaTypeMapping(filter: MappingMatcher, step: MappingStep): List<AnnotationTypeMapping> {
        val mappings = schemaMappings.filter(filter, step.add(SchemasStep()))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationTypeMapping }
    }

    fun findParameterTypeMapping(filter: MappingMatcher, step: MappingStep): TypeMapping? {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep("type")))
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as TypeMapping
    }

    fun findInterfaceParameterTypeMappings(filter: MappingMatcher, step: MappingStep): List<InterfaceTypeMapping> {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep("type")))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as InterfaceTypeMapping }
    }

    fun findAnnotationParameterTypeMapping(filter: MappingMatcher, step: MappingStep): List<AnnotationTypeMapping> {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep("type")))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationTypeMapping }
    }

    fun findParameterNameTypeMapping(filter: MappingMatcher, step: MappingStep): NameTypeMapping? {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep("name")))
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as NameTypeMapping
    }

    fun findAnnotationParameterNameTypeMapping(filter: MappingMatcher, step: MappingStep): List<AnnotationNameMapping> {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep("name")))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AnnotationNameMapping }
    }

    fun findAddParameterTypeMappings(filter: MappingMatcher, step: MappingStep): List<AddParameterTypeMapping>  {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep("add")))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as AddParameterTypeMapping }
    }

    fun findDropParameterTypeMappings(filter: MappingMatcher, step: MappingStep): List<DropParameterTypeMapping>  {
        val mappings = parameterTypeMappings.filter(filter, step.add(ParametersStep("drop")))
        if (mappings.isEmpty()) {
            return emptyList()
        }

        return mappings.map { it as DropParameterTypeMapping }
    }

    fun findContentTypeMapping(filter: MappingMatcher, step: MappingStep): ContentTypeMapping? {
        val mappings = responseTypeMappings.filter(filter, step.add(ContentTypesStep()))
        if (mappings.isEmpty()) {
            return null
        }

        if (mappings.size > 1) {
            throw AmbiguousTypeMappingException(mappings)
        }

        return mappings.first() as ContentTypeMapping
    }

    fun isExcluded(step: MappingStep): Boolean {
        if (exclude) {
            step.add(StringStep("exclude: true", true))
        }
        return exclude
    }
}
