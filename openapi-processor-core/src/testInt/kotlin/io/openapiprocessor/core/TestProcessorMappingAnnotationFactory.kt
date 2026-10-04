/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.AnnotationData
import io.openapiprocessor.core.writer.java.MappingAnnotationFactory

/**
 * simple [io.openapiprocessor.core.writer.java.MappingAnnotationFactory] implementation for testing.
 */
class TestProcessorMappingAnnotationFactory: MappingAnnotationFactory {

    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<AnnotationData> {
        return listOf(AnnotationData(
            """${MAPPING.annotationName}("${endpoint.path}")""",
            MAPPING.imports + MAPPING.annotationName))
    }
}
