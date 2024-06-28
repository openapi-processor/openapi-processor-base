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
    private val endpointMappings: Map<String, EndpointMappings>
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

    fun getEndpointResultMapping(schema: MappingSchema): ResultTypeMapping? {
        val pathMappings = endpointMappings[schema.getPath()]
        val pathMapping = pathMappings?.getEndpointResultMapping(schema)
        if (pathMapping != null) {
            return pathMapping
        }

        val mapping = globalMappings.getResultTypeMapping()
        if(mapping != null) {
            return mapping
        }

        return null
    }
}
