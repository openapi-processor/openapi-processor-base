/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.mapping.matcher.*
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MappingRepository(
    private val globalMappings: Mappings,
    private val endpointMappings: Map<String /* path */, EndpointMappings>,
    private val extensionMappings: Map<String /* x- */, ExtensionMappings>
) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getGlobalResultTypeMapping(): ResultTypeMapping? {
        return globalMappings.getResultTypeMapping()
    }

    fun getGlobalResultStyle(): ResultStyle? {
        return globalMappings.getResultStyle()
    }

    fun getGlobalSingleTypeMapping(): TypeMapping? {
        return globalMappings.getSingleTypeMapping()
    }

    fun getGlobalMultiTypeMapping(): TypeMapping? {
        return globalMappings.getMultiTypeMapping()
    }

    fun findGlobalTypeMapping(schema: MappingSchema): TypeMapping? {
        return globalMappings.findTypeMapping(TypeMatcher(schema))
    }

    fun findGlobalAnnotationTypeMapping(schema: MappingSchema, allowObject: Boolean = false): List<AnnotationTypeMapping> {
        return globalMappings.findAnnotationTypeMapping(AnnotationTypeMatcher(schema, allowObject))
    }

    fun findGlobalParameterTypeMapping(schema: MappingSchema): TypeMapping? {
        return globalMappings.findParameterTypeMapping(TypeMatcher(schema))
    }

    fun findGlobalAnnotationParameterTypeMapping(schema: MappingSchema): List<AnnotationTypeMapping> {
        return globalMappings.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(schema))
    }

    fun findGlobalParameterNameTypeMapping(schema: MappingSchema): NameTypeMapping? {
        return globalMappings.findParameterNameTypeMapping(ParameterTypeMatcher(schema))
    }

    fun findGlobalAnnotationParameterNameTypeMapping(schema: MappingSchema): List<AnnotationNameMapping> {
        return globalMappings.findAnnotationParameterNameTypeMapping(AnnotationParameterNameMatcher(schema))
    }

    fun findGlobalAddParameterTypeMappings(): List<AddParameterTypeMapping>  {
        return globalMappings.findAddParameterTypeMappings(AddParameterTypeMatcher())
    }

    fun findGlobalContentTypeMapping(schema: MappingSchema): ContentTypeMapping? {
        return globalMappings.findContentTypeMapping(ResponseTypeMatcher(schema))
    }

    fun getEndpointResultTypeMapping(schema: MappingSchema): ResultTypeMapping? {
        return endpointMappings[schema.getPath()]?.getResultTypeMapping(schema)
    }

    fun getEndpointSingleTypeMapping(schema: MappingSchema): TypeMapping? {
        return endpointMappings[schema.getPath()]?.getSingleTypeMapping(schema)
    }

    fun getEndpointMultiTypeMapping(schema: MappingSchema): TypeMapping? {
        return endpointMappings[schema.getPath()]?.getMultiTypeMapping(schema)
    }

    fun findEndpointTypeMapping(schema: MappingSchema): TypeMapping? {
        return endpointMappings[schema.getPath()]?.findTypeMapping(schema)
    }

    fun findEndpointAnnotationTypeMapping(schema: MappingSchema, allowObject: Boolean = false): List<AnnotationTypeMapping> {
        val mappings = endpointMappings[schema.getPath()] ?: return emptyList()
        return mappings.findAnnotationTypeMapping(schema, allowObject)
    }

    fun findEndpointParameterTypeMapping(schema: MappingSchema): TypeMapping? {
        return endpointMappings[schema.getPath()]?.findParameterTypeMapping(schema)
    }

    fun findEndpointAnnotationParameterTypeMapping(schema: MappingSchema): List<AnnotationTypeMapping> {
        val mappings = endpointMappings[schema.getPath()] ?: return emptyList()
        return mappings.findAnnotationParameterTypeMapping(schema)
    }

    fun findEndpointParameterNameTypeMapping(schema: MappingSchema): NameTypeMapping? {
        return endpointMappings[schema.getPath()]?.findParameterNameTypeMapping(schema)
    }

    fun findEndpointAnnotationParameterNameTypeMapping(schema: MappingSchema): List<AnnotationNameMapping> {
        val mappings = endpointMappings[schema.getPath()] ?: return emptyList()
        return mappings.findAnnotationParameterNameTypeMapping(schema)
    }

    fun findEndpointAddParameterTypeMappings(schema: MappingSchema): List<AddParameterTypeMapping> {
        val mappings = endpointMappings[schema.getPath()] ?: return emptyList()
        return mappings.findAddParameterTypeMappings(schema)
    }

    fun findEndpointContentTypeMapping(schema: MappingSchema): ContentTypeMapping? {
        return endpointMappings[schema.getPath()]?.findContentTypeMapping(schema)
    }

    fun findExtensionAnnotations(extension: String, value: String): List<AnnotationNameMapping> {
        val extMappings = extensionMappings[extension] ?: return emptyList()
        return extMappings.get(value)
    }

    fun isEndpointExcluded(schema: MappingSchema): Boolean {
        val mappings = endpointMappings[schema.getPath()]?: return false
        return mappings.isExcluded(schema)
    }
}
