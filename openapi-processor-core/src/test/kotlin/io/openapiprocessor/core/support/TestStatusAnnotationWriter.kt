/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.StatusAnnotationWriter
import java.io.Writer

class TestStatusAnnotationWriter(val withCode: Boolean = false) : StatusAnnotationWriter {
    override fun write(
        target: Writer,
        endpoint: Endpoint,
        endpointResponse: EndpointResponse
    ): Set<String> {
        if (withCode) {
            target.write ("""${STATUS.annotationName}("${endpointResponse.statusCode}")""")
        } else {
            target.write (STATUS.annotationName)
        }
        return STATUS.imports
    }
}
