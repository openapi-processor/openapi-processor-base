/*
 * Copyright 2015 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.StatusAnnotationWriter
import java.io.Writer

/**
 * simple [io.openapiprocessor.core.writer.java.StatusAnnotationWriter] implementation for testing.
 */
class TestProcessorStatusAnnotationWriter: StatusAnnotationWriter {
    override fun write(
        target: Writer,
        endpoint: Endpoint,
        endpointResponse: EndpointResponse
    ) {
        target.write ("""${STATUS.annotationName}("${endpointResponse.statusCode}")""")
    }
}
