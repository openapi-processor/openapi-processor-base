/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import java.io.Writer

/**
 * mapping annotation writer interface.
 */
// TODO rename to factory
interface MappingAnnotationWriter {
    @Deprecated("remove, use create()")
    fun write (target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse)

    fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<String>
}
