/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiprocessor.core.parser.HttpMethod
import java.net.URI
import io.openapiparser.model.v32.Operation as Operation32
import io.openapiparser.model.v32.Parameter as Parameter32
import io.openapiparser.model.v32.PathItem as Path32
import io.openapiparser.model.v32.Response as Response32
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody
import io.openapiprocessor.core.parser.Response as ParserResponse

/**
 * openapi-parser Operation abstraction.
 */
class Operation(
    private val method: HttpMethod,
    private val operation: Operation32,
    private val path: Path32,
): ParserOperation {

    override fun getMethod(): HttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<ParserParameter> {
        val parameters = mutableListOf<ParserParameter>()

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

    override fun getRequestBody(): ParserRequestBody? {
        var requestBody = operation.requestBody
        if (requestBody == null) {
            return null

        } else if (requestBody.isRef) {
            requestBody = requestBody.refObject
        }

        return RequestBody(requestBody)
    }

    override fun getResponses(): Map<String, ParserResponse> {
        val content = linkedMapOf<String, ParserResponse>()

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
