/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.AnnotationData
import io.openapiprocessor.core.writer.java.MappingAnnotationFactory

class TestMappingAnnotationFactory(val withPath: Boolean = false): MappingAnnotationFactory {

    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<AnnotationData> {
        return if (withPath) {
            listOf(AnnotationData("""${MAPPING.annotationName}("${endpoint.path}")""", MAPPING.imports))
        } else {
            listOf(AnnotationData(MAPPING.annotationName, MAPPING.imports))
        }
    }
}
