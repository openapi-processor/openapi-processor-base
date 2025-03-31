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

class EndpointMappings(
    private val mappings: Mappings,
    private val methodMappings: Map<HttpMethod, Mappings>
) {
    fun getResultTypeMapping(query: MappingQuery, step: MappingStep): ResultTypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.getResultTypeMapping(step.add(MethodsStep(query)))
            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.getResultTypeMapping(step)
        if (mapping != null) {
            return mapping
        }

        return null
    }

    fun getResultStyle(query: MappingQuery, step: MappingStep): ResultStyle? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.getResultStyle(step.add(MethodsStep(query)))
            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.getResultStyle(step)
        if (mapping != null) {
            return mapping
        }

        return null
    }

    fun getSingleTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.getSingleTypeMapping(step.add(MethodsStep(query)))
            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.getSingleTypeMapping(step)
        if (mapping != null) {
            return mapping
        }

        return null
    }

    fun getMultiTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.getMultiTypeMapping(step.add(MethodsStep(query)))
            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.getMultiTypeMapping(step)
        if (mapping != null) {
            return mapping
        }

        return null
    }

    fun getNullTypeMapping(query: MappingQuery, step: MappingStep): NullTypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.getNullTypeMapping(step.add(MethodsStep(query)))
            if (methodMapping != null) {
                return methodMapping
            }
        }

        val mapping = mappings.getNullTypeMapping(step)
        if (mapping != null) {
            return mapping
        }

        return null
    }

    fun findTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodMapping = httpMethodMappings.findTypeMapping(
                TypeMatcher(query),
                step.add(MethodsStep(query)))

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

    fun isExcluded(query: MappingQuery, step: MappingStep): Boolean {
        val httpMethodMappings = methodMappings[query.method]
        if (httpMethodMappings != null) {
            val methodExcluded = httpMethodMappings.isExcluded(step.add(MethodsStep(query)))
            if (methodExcluded) {
                return true
            }
        }

        return mappings.isExcluded(step)
    }
}
