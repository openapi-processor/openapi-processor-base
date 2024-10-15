/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.matcher.*
import io.openapiprocessor.core.converter.mapping.steps.EndpointsStep
import io.openapiprocessor.core.converter.mapping.steps.GlobalsStep
import io.openapiprocessor.core.converter.mapping.steps.MappingStep
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MappingRepository(
    private val globalMappings: Mappings = Mappings(),
    private val endpointMappings: Map<String /* path */, EndpointMappings> = emptyMap(),
    private val extensionMappings: Map<String /* x- */, ExtensionMappings> = emptyMap()
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getGlobalResultTypeMapping(step: MappingStep): ResultTypeMapping? {
        return globalMappings.getResultTypeMapping(step.add(GlobalsStep()))
    }

    fun getGlobalResultStyleMapping(step: MappingStep): ResultStyle? {
        return globalMappings.getResultStyle(step.add(GlobalsStep()))
    }

    fun getGlobalSingleTypeMapping(step: MappingStep): TypeMapping? {
        return globalMappings.getSingleTypeMapping(step.add(GlobalsStep()))
    }

    fun getGlobalMultiTypeMapping(step: MappingStep): TypeMapping? {
        return globalMappings.getMultiTypeMapping(step.add(GlobalsStep()))
    }

    fun findGlobalTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        return globalMappings.findTypeMapping(TypeMatcher(query), step.add(GlobalsStep()))
    }

    fun findGlobalAnnotationTypeMapping(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        return globalMappings.findAnnotationTypeMapping(AnnotationTypeMatcher(query), step.add(GlobalsStep()))
    }

    fun findGlobalParameterTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        return globalMappings.findParameterTypeMapping(TypeMatcher(query), step.add(GlobalsStep()))
    }

    fun findGlobalAnnotationParameterTypeMappings(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        return globalMappings.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(query), step.add(GlobalsStep()))
    }

    fun findGlobalParameterNameTypeMapping(query: MappingQuery, step: MappingStep): NameTypeMapping? {
        return globalMappings.findParameterNameTypeMapping(ParameterNameTypeMatcher(query), step.add(GlobalsStep()))
    }

    fun findGlobalAnnotationParameterNameTypeMapping(query: MappingQuery, step: MappingStep): List<AnnotationNameMapping> {
        return globalMappings.findAnnotationParameterNameTypeMapping(AnnotationParameterNameTypeMatcher(query),
            step.add(GlobalsStep()))
    }

    fun findGlobalAddParameterTypeMappings(step: MappingStep): List<AddParameterTypeMapping>  {
        return globalMappings.findAddParameterTypeMappings(AddParameterTypeMatcher(), step.add(GlobalsStep()))
    }

    fun findGlobalContentTypeMapping(query: MappingQuery, step: MappingStep): ContentTypeMapping? {
        return globalMappings.findContentTypeMapping(ContentTypeMatcher(query), step.add(GlobalsStep()))
    }

    fun getEndpointResultTypeMapping(query: MappingQuery, step: MappingStep): ResultTypeMapping? {
        return endpointMappings[query.path]?.getResultTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun getEndpointResultStyleMapping(query: MappingQuery, step: MappingStep): ResultStyle? {
        return endpointMappings[query.path]?.getResultStyle(query, step.add(EndpointsStep(query)))
    }

    fun getEndpointSingleTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        return endpointMappings[query.path]?.getSingleTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun getEndpointMultiTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        return endpointMappings[query.path]?.getMultiTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun getEndpointNullTypeMapping(query: MappingQuery, step: MappingStep): NullTypeMapping? {
        return endpointMappings[query.path]?.getNullTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        return endpointMappings[query.path]?.findTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointAnnotationTypeMapping(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        val pathMappings = endpointMappings[query.path] ?: return emptyList()
        return pathMappings.findAnnotationTypeMappings(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointParameterTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        return endpointMappings[query.path]?.findParameterTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointAnnotationParameterTypeMappings(query: MappingQuery, step: MappingStep): List<AnnotationTypeMapping> {
        val mappings = endpointMappings[query.path] ?: return emptyList()
        return mappings.findAnnotationParameterTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointParameterNameTypeMapping(query: MappingQuery, step: MappingStep): NameTypeMapping? {
        return endpointMappings[query.path]?.findParameterNameTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointAnnotationParameterNameTypeMapping(query: MappingQuery, step: MappingStep): List<AnnotationNameMapping> {
        val mappings = endpointMappings[query.path] ?: return emptyList()
        return mappings.findAnnotationParameterNameTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointAddParameterTypeMappings(query: MappingQuery, step: MappingStep): List<AddParameterTypeMapping> {
        val mappings = endpointMappings[query.path] ?: return emptyList()
        return mappings.findAddParameterTypeMappings(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointContentTypeMapping(query: MappingQuery, step: MappingStep): ContentTypeMapping? {
        return endpointMappings[query.path]?.findContentTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun findExtensionAnnotations(extension: String, value: String): List<AnnotationNameMapping> {
        val extMappings = extensionMappings[extension] ?: return emptyList()
        return extMappings.get(value)
    }

    fun isEndpointExcluded(query: MappingQuery, step: MappingStep): Boolean {
        val mappings = endpointMappings[query.path]?: return false
        return mappings.isExcluded(query, step.add(EndpointsStep(query)))
    }
}
