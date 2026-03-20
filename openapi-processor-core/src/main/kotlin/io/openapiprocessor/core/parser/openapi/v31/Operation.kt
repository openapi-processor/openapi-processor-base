/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiprocessor.core.openapi.HttpMethod
import java.net.URI
import io.openapiparser.model.v31.Operation as Operation31
import io.openapiparser.model.v31.Parameter as Parameter31
import io.openapiparser.model.v31.PathItem as Path31
import io.openapiparser.model.v31.Response as Response31
import io.openapiprocessor.core.openapi.Operation as OpenApiOperation
import io.openapiprocessor.core.openapi.Parameter as OpenApiParameter
import io.openapiprocessor.core.openapi.RequestBody as OpenApiRequestBody
import io.openapiprocessor.core.openapi.Response as OpenApiResponse

/**
 * openapi-parser Operation abstraction.
 */
class Operation(
    private val method: HttpMethod,
    private val operation: Operation31,
    private val path: Path31,
): OpenApiOperation {

    override fun getMethod(): HttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<OpenApiParameter> {
        val parameters = mutableListOf<OpenApiParameter>()

        path.parameters.map { p: Parameter31 ->
            var param = p
            if(p.isRef) {
                param = p.refObject
            }

            parameters.add(Parameter(param))
        }

        operation.parameters.map { p: Parameter31 ->
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

        operation.responses?.responses?.forEach { (key: String, value: Response31) ->
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
