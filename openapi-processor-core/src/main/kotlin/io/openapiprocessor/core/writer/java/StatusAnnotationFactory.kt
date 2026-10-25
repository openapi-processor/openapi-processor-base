/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse

/**
 * status annotation writer interface.
 */
fun interface StatusAnnotationFactory {
    fun create (endpoint: Endpoint, endpointResponse: EndpointResponse): List<CodeSnippet>
}
