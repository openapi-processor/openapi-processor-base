/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiprocessor.core.openapi.HttpMethod
import java.net.URI
import io.openapiparser.model.v32.Operation as Operation32
import io.openapiparser.model.v32.Parameter as Parameter32
import io.openapiparser.model.v32.PathItem as Path32
import io.openapiparser.model.v32.Response as Response32
import io.openapiprocessor.core.openapi.Operation as OpenApiOperation
import io.openapiprocessor.core.openapi.Parameter as OpenApiParameter
import io.openapiprocessor.core.openapi.RequestBody as OpenApiRequestBody
import io.openapiprocessor.core.openapi.Response as OpenApiResponse

/**
 * openapi-parser Operation abstraction.
 */
class Operation(
    private val method: HttpMethod,
    private val operation: Operation32,
    private val path: Path32,
): OpenApiOperation {

    override fun getMethod(): HttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<OpenApiParameter> {
        val parameters = mutableListOf<OpenApiParameter>()

        path.parameters.map { p: Parameter32 ->
            var param = p
            if(p.isRef) {
                param = p.refObject
            }

            parameters.add(Parameter(param))
        }

        operation.parameters.map { p: Parameter32 ->
            var param = p
            if(p.isRef) {
                param = p.refObject
            }

            parameters.add(Parameter(param))
        }

        return parameters
    }

    override fun getRequestBody(): OpenApiRequestBody? {
        var requestBody = operation.requestBody
        if (requestBody == null) {
            return null

        } else if (requestBody.isRef) {
            requestBody = requestBody.refObject
        }

        return RequestBody(requestBody)
    }

    override fun getResponses(): Map<String, OpenApiResponse> {
        val content = linkedMapOf<String, OpenApiResponse>()

        operation.responses?.responses?.forEach { (key: String, value: Response32) ->
            content[key] = Response(value)
        }

        return content
    }

    override fun isDeprecated(): Boolean = operation.deprecated

    override fun hasTags(): Boolean = operation.tags.isNotEmpty()

    override val summary: String? = operation.summary

    override val description: String? = operation.description

    override fun getFirstTag(): String? = if (hasTags()) operation.tags.first () else null

    override fun getDocumentUri(): URI {
        return path.documentUri
    }
}
