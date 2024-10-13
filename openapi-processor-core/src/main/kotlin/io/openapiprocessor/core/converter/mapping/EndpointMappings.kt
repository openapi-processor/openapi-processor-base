/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.matcher.*
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.converter.mapping.steps.MethodsStep
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EndpointMappings(
    private val mappings: Mappings,
    private val methodMappings: Map<HttpMethod, Mappings>
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

    fun findTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.findTypeMapping(TypeMatcher(query), step.add(MethodsStep(query)))
            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.findTypeMapping(TypeMatcher(query), step)
        if (mapping != null) {
            return mapping
        }

        return null
    }

    fun findAnnotationTypeMappings(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMappings = httpMethodMappings.findAnnotationTypeMapping(
                AnnotationTypeMatcher(query),
                step.add(MethodsStep(query)))

            if (methodMappings.isNotEmpty()) {
                return methodMappings
            }
        }

        val mappings = mappings.findAnnotationTypeMapping(AnnotationTypeMatcher(query), step)
        return mappings
    }

    fun findParameterTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.findParameterTypeMapping(
                TypeMatcher(query),
                step.add(MethodsStep(query)))

            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.findParameterTypeMapping(TypeMatcher(query), step)
        if (mapping != null) {
            return mapping
        }

        return null
    }

    fun findAnnotationParameterTypeMapping(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMappings = httpMethodMappings.findAnnotationParameterTypeMapping(
                AnnotationTypeMatcher(query),
                step.add(MethodsStep(query)))

            if (methodMappings.isNotEmpty()) {
                return methodMappings
            }
        }

        val mappings = mappings.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(query), step)
        return mappings
    }

    fun findParameterNameTypeMapping(query: MappingQuery, step: MappingStep): NameTypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.findParameterNameTypeMapping(
                ParameterNameTypeMatcher(query),
                step.add(MethodsStep(query)))

            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.findParameterNameTypeMapping(ParameterNameTypeMatcher(query), step)
        if (mapping != null) {
            return mapping
        }

        return null
    }

    fun findAnnotationParameterNameTypeMapping(query: MappingQuery, step: MappingStep): List<AnnotationNameMapping> {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMappings = httpMethodMappings.findAnnotationParameterNameTypeMapping(
                AnnotationParameterNameTypeMatcher(query),
                step.add(MethodsStep(query)))

            if (methodMappings.isNotEmpty()) {
                return methodMappings
            }
        }

        val mappings = mappings.findAnnotationParameterNameTypeMapping(AnnotationParameterNameTypeMatcher(query), step)
        return mappings
    }

    fun findAddParameterTypeMappings(query: MappingQuery, step: MappingStep): List<AddParameterTypeMapping> {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMappings = httpMethodMappings.findAddParameterTypeMappings(
                AddParameterTypeMatcher(),
                step.add(MethodsStep(query)))

            if (methodMappings.isNotEmpty()) {
                return methodMappings
            }
        }

        val mappings = mappings.findAddParameterTypeMappings(AddParameterTypeMatcher(), step)
        return mappings
    }

    fun findContentTypeMapping(query: MappingQuery, step: MappingStep): ContentTypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.findContentTypeMapping(
                ContentTypeMatcher(query),
                step.add(MethodsStep(query)))

            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.findContentTypeMapping(ContentTypeMatcher(query), step)
        if (mapping != null) {
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
