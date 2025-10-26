/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter
import java.io.Writer

/**
 * simple [io.openapiprocessor.core.writer.java.MappingAnnotationWriter] implementation for testing.
 */
// todo rename to factory
class TestProcessorMappingAnnotationWriter: MappingAnnotationWriter {

    @Deprecated("remove, use create()")
    override fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse) {
        target.write ("""${MAPPING.annotationName}("${endpoint.path}")""")
    }

    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<String> {
        return listOf("""${MAPPING.annotationName}("${endpoint.path}")""")
    }
}
