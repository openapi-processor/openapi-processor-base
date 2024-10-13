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

    fun getGlobalResultTypeMapping(): ResultTypeMapping? {
        return globalMappings.getResultTypeMapping()
    }

    fun getGlobalResultStyleMapping(): ResultStyle? {
        return globalMappings.getResultStyle()
    }

    fun getGlobalSingleTypeMapping(): TypeMapping? {
        return globalMappings.getSingleTypeMapping()
    }

    fun getGlobalMultiTypeMapping(): TypeMapping? {
        return globalMappings.getMultiTypeMapping()
    }

    fun findGlobalTypeMapping(query: MappingQuery): TypeMapping? {
        return globalMappings.findTypeMapping(TypeMatcher(query))
    }

    fun findGlobalAnnotationTypeMapping(query: MappingQuery): List<AnnotationTypeMapping> {
        return globalMappings.findAnnotationTypeMapping(AnnotationTypeMatcher(query))
    }

    fun findGlobalParameterTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        return globalMappings.findParameterTypeMapping(TypeMatcher(query), step.add(GlobalsStep()))
    }

    fun findGlobalAnnotationParameterTypeMappings(query: MappingQuery): List<AnnotationTypeMapping> {
        return globalMappings.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(query))
    }

    fun findGlobalParameterNameTypeMapping(query: MappingQuery): NameTypeMapping? {
        return globalMappings.findParameterNameTypeMapping(ParameterNameTypeMatcher(query))
    }

    fun findGlobalAnnotationParameterNameTypeMapping(query: MappingQuery): List<AnnotationNameMapping> {
        return globalMappings.findAnnotationParameterNameTypeMapping(AnnotationParameterNameTypeMatcher(query))
    }

    fun findGlobalAddParameterTypeMappings(): List<AddParameterTypeMapping>  {
        return globalMappings.findAddParameterTypeMappings(AddParameterTypeMatcher())
    }

    fun findGlobalContentTypeMapping(query: MappingQuery): ContentTypeMapping? {
        return globalMappings.findContentTypeMapping(ContentTypeMatcher(query))
    }

    fun getEndpointResultTypeMapping(query: MappingQuery): ResultTypeMapping? {
        return endpointMappings[query.path]?.getResultTypeMapping(query)
    }

    fun getEndpointResultStyleMapping(query: MappingQuery): ResultStyle? {
        return endpointMappings[query.path]?.getResultStyle(query)
    }

    fun getEndpointSingleTypeMapping(query: MappingQuery): TypeMapping? {
        return endpointMappings[query.path]?.getSingleTypeMapping(query)
    }

    fun getEndpointMultiTypeMapping(query: MappingQuery): TypeMapping? {
        return endpointMappings[query.path]?.getMultiTypeMapping(query)
    }

    fun getEndpointNullTypeMapping(query: MappingQuery): NullTypeMapping? {
        return endpointMappings[query.path]?.getNullTypeMapping(query)
    }

    fun findEndpointTypeMapping(query: MappingQuery): TypeMapping? {
        return endpointMappings[query.path]?.findTypeMapping(query)
    }

    fun findEndpointAnnotationTypeMapping(query: MappingQuery): List<AnnotationTypeMapping> {
        val mappings = endpointMappings[query.path] ?: return emptyList()
        return mappings.findAnnotationTypeMappings(query)
    }

    fun findEndpointParameterTypeMapping(query: MappingQuery, step: MappingStep): TypeMapping? {
        return endpointMappings[query.path]?.findParameterTypeMapping(query, step.add(EndpointsStep(query)))
    }

    fun findEndpointAnnotationParameterTypeMappings(query: MappingQuery): List<AnnotationTypeMapping> {
        val mappings = endpointMappings[query.path] ?: return emptyList()
        return mappings.findAnnotationParameterTypeMapping(query)
    }

    fun findEndpointParameterNameTypeMapping(query: MappingQuery): NameTypeMapping? {
        return endpointMappings[query.path]?.findParameterNameTypeMapping(query)
    }

    fun findEndpointAnnotationParameterNameTypeMapping(query: MappingQuery): List<AnnotationNameMapping> {
        val mappings = endpointMappings[query.path] ?: return emptyList()
        return mappings.findAnnotationParameterNameTypeMapping(query)
    }

    fun findEndpointAddParameterTypeMappings(query: MappingQuery): List<AddParameterTypeMapping> {
        val mappings = endpointMappings[query.path] ?: return emptyList()
        return mappings.findAddParameterTypeMappings(query)
    }

    fun findEndpointContentTypeMapping(query: MappingQuery): ContentTypeMapping? {
        return endpointMappings[query.path]?.findContentTypeMapping(query)
    }

    fun findExtensionAnnotations(extension: String, value: String): List<AnnotationNameMapping> {
        val extMappings = extensionMappings[extension] ?: return emptyList()
        return extMappings.get(value)
    }

    fun isEndpointExcluded(query: MappingQuery): Boolean {
        val mappings = endpointMappings[query.path]?: return false
        return mappings.isExcluded(query)
    }
}
