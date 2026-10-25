/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.CodeSnippet
import io.openapiprocessor.core.writer.java.StatusAnnotationFactory

class TestStatusAnnotationFactory(val withCode: Boolean = false) : StatusAnnotationFactory {
    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<CodeSnippet> {
        return if (withCode) {
            listOf(CodeSnippet(
                """${STATUS.annotationName}("${endpointResponse.statusCode}")""",
                STATUS.imports))
        } else {
            listOf(CodeSnippet(
                STATUS.annotationName,
                STATUS.imports))
        }
    }
}
