/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.matcher.*
import io.openapiprocessor.core.parser.HttpMethod
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EndpointMappings(
    private val mappings: Mappings,
    private val methodMappings: Map<HttpMethod, Mappings>,
    private val exclude: Boolean
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getResultTypeMapping(schema: MappingSchema): ResultTypeMapping? {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMapping = httpMethodMappings?.getResultTypeMapping()
        if (methodMapping != null) {
            log.trace("found endpoint result type mapping ({} {})", schema.getPath(), schema.getMethod())
            return methodMapping
        }

        val mapping = mappings.getResultTypeMapping()
        if (mapping != null) {
            log.trace("found endpoint result type mapping ({})", schema.getPath())
            return mapping
        }

        return null
    }

    fun getSingleTypeMapping(schema: MappingSchema): TypeMapping? {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMapping = httpMethodMappings?.getSingleTypeMapping()
        if (methodMapping != null) {
            log.trace("found endpoint single mapping ({} {})", schema.getPath(), schema.getMethod())
            return methodMapping
        }

        val mapping = mappings.getSingleTypeMapping()
        if (mapping != null) {
            log.trace("found endpoint single mapping ({})", schema.getPath())
            return mapping
        }

        return null
    }

    fun getMultiTypeMapping(schema: MappingSchema): TypeMapping? {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMapping = httpMethodMappings?.getMultiTypeMapping()
        if (methodMapping != null) {
            log.trace("found endpoint multi mapping ({} {})", schema.getPath(), schema.getMethod())
            return methodMapping
        }

        val mapping = mappings.getMultiTypeMapping()
        if (mapping != null) {
            log.trace("found endpoint multi mapping ({})", schema.getPath())
            return mapping
        }

        return null
    }

    fun findTypeMapping(schema: MappingSchema): TypeMapping? {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMapping = httpMethodMappings?.findTypeMapping(TypeMatcher(schema))
        if (methodMapping != null) {
            log.trace("found endpoint type mapping ({} {})", schema.getPath(), schema.getMethod())
            return methodMapping
        }

        val mapping = mappings.findTypeMapping(TypeMatcher(schema))
        if (mapping != null) {
            log.trace("found endpoint type mapping ({})", schema.getPath())
            return mapping
        }

        return null
    }

    fun findAnnotationTypeMapping(schema: MappingSchema, allowObject: Boolean = false): List<AnnotationTypeMapping> {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMappings = httpMethodMappings?.findAnnotationTypeMapping(AnnotationTypeMatcher(schema, allowObject))
        if (!methodMappings.isNullOrEmpty()) {
            log.trace("found endpoint annotation type mappings ({} {})", schema.getPath(), schema.getMethod())
            return methodMappings
        }

        val mappings = mappings.findAnnotationTypeMapping(AnnotationTypeMatcher(schema, allowObject))
        log.trace("found endpoint annotation type mapping ({})", schema.getPath())
        return mappings
    }

    fun findParameterTypeMapping(schema: MappingSchema): TypeMapping? {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMapping = httpMethodMappings?.findParameterTypeMapping(TypeMatcher(schema))
        if (methodMapping != null) {
            log.trace("found endpoint parameter type mapping ({} {})", schema.getPath(), schema.getMethod())
            return methodMapping
        }

        val mapping = mappings.findParameterTypeMapping(TypeMatcher(schema))
        if (mapping != null) {
            log.trace("found endpoint parameter type mapping ({})", schema.getPath())
            return mapping
        }

        return null
    }

    fun findAnnotationParameterTypeMapping(schema: MappingSchema): List<AnnotationTypeMapping> {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMappings = httpMethodMappings?.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(schema))
        if (!methodMappings.isNullOrEmpty()) {
            log.trace("found endpoint annotation parameter type mappings ({} {})", schema.getPath(), schema.getMethod())
            return methodMappings
        }

        val mappings = mappings.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(schema))
        log.trace("found endpoint annotation parameter type mapping ({})", schema.getPath())
        return mappings
    }

    fun findParameterNameTypeMapping(schema: MappingSchema): NameTypeMapping? {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMapping = httpMethodMappings?.findParameterNameTypeMapping(ParameterTypeMatcher(schema))
        if (methodMapping != null) {
            log.trace("found endpoint parameter name type mapping ({} {})", schema.getPath(), schema.getMethod())
            return methodMapping
        }

        val mapping = mappings.findParameterNameTypeMapping(ParameterTypeMatcher(schema))
        if (mapping != null) {
            log.trace("found endpoint parameter name type mapping ({})", schema.getPath())
            return mapping
        }

        return null
    }

    fun findAnnotationParameterNameTypeMapping(schema: MappingSchema): List<AnnotationNameMapping> {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMappings = httpMethodMappings?.findAnnotationParameterNameTypeMapping(
            AnnotationParameterNameMatcher(schema))
        if (!methodMappings.isNullOrEmpty()) {
            log.trace("found endpoint annotation parameter name type mappings ({} {})", schema.getPath(), schema.getMethod())
            return methodMappings
        }

        val mappings = mappings.findAnnotationParameterNameTypeMapping(AnnotationParameterNameMatcher(schema))
        log.trace("found endpoint annotation parameter name type mapping ({})", schema.getPath())
        return mappings
    }

    fun findAddParameterTypeMappings(schema: MappingSchema): List<AddParameterTypeMapping> {
        val httpMethodMappings = methodMappings[schema.getMethod()]
        val methodMappings = httpMethodMappings?.findAddParameterTypeMappings(AddParameterTypeMatcher())
        if (!methodMappings.isNullOrEmpty()) {
            log.trace("found endpoint add parameter type mappings ({} {})", schema.getPath(), schema.getMethod())
            return methodMappings
        }

        val mappings = mappings.findAddParameterTypeMappings(AddParameterTypeMatcher())
        log.trace("found endpoint add parameter type mapping ({})", schema.getPath())
        return mappings
    }

    fun findContentTypeMapping(schema: MappingSchema): ContentTypeMapping? {
        return null
    }

    fun isExcluded(schema: MappingSchema): Boolean {
        return false
    }
}
