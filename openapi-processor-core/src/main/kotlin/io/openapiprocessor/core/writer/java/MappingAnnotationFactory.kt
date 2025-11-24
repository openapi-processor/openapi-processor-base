/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse

/**
 * mapping annotation factory.
 */
interface MappingAnnotationFactory {
    fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<String>
}
