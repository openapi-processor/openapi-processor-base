/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import java.net.URI
import io.openapiprocessor.core.openapi.HttpMethod as OpenApiHttpMethod
import io.openapiprocessor.core.openapi.Operation as OpenApiOperation
import io.openapiprocessor.core.openapi.Parameter as OpenApiParameter
import io.openapiprocessor.core.openapi.RequestBody as OpenApiRequestBody
import io.openapiprocessor.core.openapi.Response as OpenApiResponse
import io.swagger.v3.oas.models.Operation as SwaggerOperation
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.oas.models.parameters.Parameter as SwaggerParameter
import io.swagger.v3.oas.models.responses.ApiResponse as SwaggerResponse

/**
 * Swagger Operation abstraction.
 */
class Operation(
    private val method: OpenApiHttpMethod,
    private val operation: SwaggerOperation,
    private val path: SwaggerPath,
    private val refResolver: RefResolverNative
): OpenApiOperation {

    override fun getMethod(): OpenApiHttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<OpenApiParameter> {
        val parameters = mutableListOf<OpenApiParameter>()

        // the swagger parser moves the endpoint parameters to the operation level, sometimes.
        // Sometimes it does not. Check both lists.
        path.parameters?.forEach { p: SwaggerParameter ->
            parameters.add(Parameter(p))
        }

        operation.parameters?.forEach { p: SwaggerParameter ->
            parameters.add(Parameter(p))
        }

        return parameters
    }

    override fun getRequestBody(): OpenApiRequestBody? {
        var requestBody = operation.requestBody
        if (requestBody == null) {
            return null

        } else if (requestBody.`$ref` != null) {
            requestBody = refResolver.resolve(requestBody)
        }

        return RequestBody (requestBody)
    }

    override fun getResponses(): Map<String, OpenApiResponse> {
        val content = linkedMapOf<String, OpenApiResponse>()

        operation.responses.forEach { (key: String, value: SwaggerResponse) ->
            content[key] = Response(value)
        }

        return content
    }

    override fun isDeprecated(): Boolean = operation.deprecated ?: false

    override fun hasTags(): Boolean = operation.tags?.isNotEmpty() ?: false

    override val summary: String? = operation.summary

    override val description: String? = operation.description

    override fun getFirstTag(): String? = operation.tags?.first()

    override fun getDocumentUri(): URI {
        TODO("deriving the package name from the document location is not supported with the swagger parser.")
    }
}
