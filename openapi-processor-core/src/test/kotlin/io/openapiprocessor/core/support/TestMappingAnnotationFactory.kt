/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.CodeSnippet
import io.openapiprocessor.core.writer.java.MappingAnnotationFactory

class TestMappingAnnotationFactory(val withPath: Boolean = false): MappingAnnotationFactory {

    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<CodeSnippet> {
        return if (withPath) {
            listOf(CodeSnippet("""${MAPPING.annotationName}("${endpoint.path}")""", MAPPING.imports))
        } else {
            listOf(CodeSnippet(MAPPING.annotationName, MAPPING.imports))
        }
    }
}
