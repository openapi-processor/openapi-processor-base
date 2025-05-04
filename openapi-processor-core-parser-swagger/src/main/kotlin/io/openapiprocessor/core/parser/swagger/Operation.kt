/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.HttpMethod
import java.net.URI
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody
import io.openapiprocessor.core.parser.Response as ParserResponse
import io.swagger.v3.oas.models.Operation as SwaggerOperation
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.oas.models.parameters.Parameter as SwaggerParameter
import io.swagger.v3.oas.models.responses.ApiResponse as SwaggerResponse

/**
 * Swagger Operation abstraction.
 */
class Operation(
    private val method: HttpMethod,
    private val operation: SwaggerOperation,
    private val path: SwaggerPath,
    private val refResolver: RefResolverNative
): ParserOperation {

    override fun getMethod(): HttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<ParserParameter> {
        val parameters = mutableListOf<ParserParameter>()

        // the swagger parser moves the endpoint parameters to the operation level, sometimes.
        // Sometimes it does not. Check both lists.
        path.parameters?.map { p: SwaggerParameter ->
            parameters.add(Parameter(p))
        }

        operation.parameters?.map { p: SwaggerParameter ->
            parameters.add(Parameter(p))
        }

        return parameters
    }

    override fun getRequestBody(): ParserRequestBody? {
        var requestBody = operation.requestBody
        if (requestBody == null) {
            return null

        } else if (requestBody.`$ref` != null) {
            requestBody = refResolver.resolve(requestBody)
        }

        return RequestBody (requestBody)
    }

    override fun getResponses(): Map<String, ParserResponse> {
        val content = linkedMapOf<String, ParserResponse>()

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
