/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.matcher.*
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EndpointMappings(
    private val mappings: Mappings,
    private val methodMappings: Map<HttpMethod, Mappings>,
    private val exclude: Boolean
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getResultTypeMapping(query: MappingQuery): ResultTypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        val methodMapping = httpMethodMappings?.getResultTypeMapping()
        if (methodMapping != null) {
            log.trace("found endpoint method result type mapping ({} {})", query.path, query.method)
            return methodMapping
        }

        val mapping = mappings.getResultTypeMapping()
        if (mapping != null) {
            log.trace("found endpoint result type mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun getResultStyle(query: MappingQuery): ResultStyle? {
        val mapping = mappings.getResultStyle()
        if (mapping != null) {
            log.trace("found endpoint result style mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun getSingleTypeMapping(query: MappingQuery): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        val methodMapping = httpMethodMappings?.getSingleTypeMapping()
        if (methodMapping != null) {
            log.trace("found endpoint method single mapping ({} {})", query.path, query.method)
            return methodMapping
        }

        val mapping = mappings.getSingleTypeMapping()
        if (mapping != null) {
            log.trace("found endpoint single mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun getMultiTypeMapping(query: MappingQuery): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        val methodMapping = httpMethodMappings?.getMultiTypeMapping()
        if (methodMapping != null) {
            log.trace("found endpoint method multi mapping ({} {})", query.path, query.method)
            return methodMapping
        }

        val mapping = mappings.getMultiTypeMapping()
        if (mapping != null) {
            log.trace("found endpoint multi mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun getNullTypeMapping(query: MappingQuery): NullTypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        val methodMapping = httpMethodMappings?.getNullTypeMapping()
        if (methodMapping != null) {
            log.trace("found endpoint method null mapping ({} {})", query.path, query.method)
            return methodMapping
        }

        val mapping = mappings.getNullTypeMapping()
        if (mapping != null) {
            log.trace("found endpoint null mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun findTypeMapping(query: MappingQuery): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        val methodMapping = httpMethodMappings?.findTypeMapping(TypeMatcher(query))
        if (methodMapping != null) {
            log.trace("found endpoint method type mapping ({} {})", query.path, query.method)
            return methodMapping
        }

        val mapping = mappings.findTypeMapping(TypeMatcher(query))
        if (mapping != null) {
            log.trace("found endpoint type mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun findAnnotationTypeMappings(query: MappingQuery): List<AnnotationTypeMapping> {
        val httpMethodMappings = methodMappings[query.method]
        val methodMappings = httpMethodMappings?.findAnnotationTypeMapping(AnnotationTypeMatcher(query))
        if (!methodMappings.isNullOrEmpty()) {
            log.trace("found endpoint method annotation type mappings ({} {})", query.path, query.method)
            return methodMappings
        }

        val mappings = mappings.findAnnotationTypeMapping(AnnotationTypeMatcher(query))
        log.trace("found endpoint annotation type mapping ({})", query.path)
        return mappings
    }

    fun findParameterTypeMapping(query: MappingQuery): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        val methodMapping = httpMethodMappings?.findParameterTypeMapping(TypeMatcher(query))
        if (methodMapping != null) {
            log.trace("found endpoint method parameter type mapping ({} {})", query.path, query.method)
            return methodMapping
        }

        val mapping = mappings.findParameterTypeMapping(TypeMatcher(query))
        if (mapping != null) {
            log.trace("found endpoint parameter type mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun findAnnotationParameterTypeMapping(query: MappingQuery): List<AnnotationTypeMapping> {
        val httpMethodMappings = methodMappings[query.method]
        val methodMappings = httpMethodMappings?.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(query))
        if (!methodMappings.isNullOrEmpty()) {
            log.trace("found endpoint method annotation parameter type mappings ({} {})", query.path, query.method)
            return methodMappings
        }

        val mappings = mappings.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(query))
        log.trace("found endpoint annotation parameter type mapping ({})", query.path)
        return mappings
    }

    fun findParameterNameTypeMapping(query: MappingQuery): NameTypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        val methodMapping = httpMethodMappings?.findParameterNameTypeMapping(ParameterNameTypeMatcher(query))
        if (methodMapping != null) {
            log.trace("found endpoint method parameter name type mapping ({} {})", query.path, query.method)
            return methodMapping
        }

        val mapping = mappings.findParameterNameTypeMapping(ParameterNameTypeMatcher(query))
        if (mapping != null) {
            log.trace("found endpoint parameter name type mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun findAnnotationParameterNameTypeMapping(query: MappingQuery): List<AnnotationNameMapping> {
        val httpMethodMappings = methodMappings[query.method]
        val methodMappings = httpMethodMappings?.findAnnotationParameterNameTypeMapping(
            AnnotationParameterNameMatcher(query))
        if (!methodMappings.isNullOrEmpty()) {
            log.trace("found endpoint method annotation parameter name type mappings ({} {})", query.path, query.method)
            return methodMappings
        }

        val mappings = mappings.findAnnotationParameterNameTypeMapping(AnnotationParameterNameMatcher(query))
        log.trace("found endpoint annotation parameter name type mapping ({})", query.path)
        return mappings
    }

    fun findAddParameterTypeMappings(query: MappingQuery): List<AddParameterTypeMapping> {
        val httpMethodMappings = methodMappings[query.method]
        val methodMappings = httpMethodMappings?.findAddParameterTypeMappings(AddParameterTypeMatcher())
        if (!methodMappings.isNullOrEmpty()) {
            log.trace("found endpoint method add parameter type mappings ({} {})", query.path, query.method)
            return methodMappings
        }

        val mappings = mappings.findAddParameterTypeMappings(AddParameterTypeMatcher())
        log.trace("found endpoint add parameter type mapping ({})", query.path)
        return mappings
    }

    fun findContentTypeMapping(query: MappingQuery): ContentTypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        val methodMapping = httpMethodMappings?.findContentTypeMapping(ResponseTypeMatcher(query))
        if (methodMapping != null) {
            log.trace("found endpoint method content type mapping ({} {})", query.path, query.method)
            return methodMapping
        }

        val mapping = mappings.findContentTypeMapping(ResponseTypeMatcher(query))
        if (mapping != null) {
            log.trace("found endpoint content type mapping ({})", query.path)
            return mapping
        }

        return null
    }

    fun isExcluded(query: MappingQuery): Boolean {
        val httpMethodMappings = methodMappings[query.method]
        val methodExcluded = httpMethodMappings?.isExcluded()
        if(methodExcluded != null && methodExcluded == true) {
            log.trace("found endpoint method exclude ({} {})", query.path, query.method)
            return methodExcluded
        }

        val excluded = mappings.isExcluded()
        if(excluded) {
            log.trace("found endpoint exclude ({})", query.path)
            return true
        }

        return false
    }
}
