/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.openapi.HttpMethod as OpenApiHttpMethod
import io.openapiprocessor.core.openapi.Operation as OpenApiOperation
import io.openapiprocessor.core.openapi.Parameter as OpenApiParameter
import io.openapiprocessor.core.openapi.RequestBody as OpenApiRequestBody
import io.openapiprocessor.core.openapi.Response as OpenApiResponse
import org.openapi4j.parser.model.v3.Operation as O4jOperation
import org.openapi4j.parser.model.v3.Parameter as O4jParameter
import org.openapi4j.parser.model.v3.Path as O4jPath
import org.openapi4j.parser.model.v3.Response as O4jResponse
import java.net.URI

/**
 * openapi4j Operation abstraction.
 */
class Operation(
    private val method: OpenApiHttpMethod,
    private val operation: O4jOperation,
    private val path: O4jPath,
    private val refResolver: RefResolverNative
): OpenApiOperation {

    override fun getMethod(): OpenApiHttpMethod = method

    override fun getOperationId(): String? {
        return operation.operationId
    }

    override fun getParameters(): List<OpenApiParameter> {
        val parameters = mutableListOf<OpenApiParameter>()

        path.parameters?.map { p: O4jParameter ->
            var param = p
            if(p.isRef) {
                param = refResolver.resolve(p)
            }

            parameters.add(Parameter(param))
        }

        operation.parameters?.map { p: O4jParameter ->
            var param = p
            if(p.isRef) {
                param = refResolver.resolve(p)
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
            requestBody = refResolver.resolve(requestBody)
        }

        return RequestBody (requestBody)
    }

    override fun getResponses(): Map<String, OpenApiResponse> {
        val content = linkedMapOf<String, OpenApiResponse>()

        operation.responses.forEach { (key: String, value: O4jResponse) ->
            content[key] = Response(value, refResolver)
        }

        return content
    }

    override fun isDeprecated(): Boolean = operation.deprecated ?: false

    override fun hasTags(): Boolean = operation.tags?.isNotEmpty() ?: false

    override val summary: String? = operation.summary

    override val description: String? = operation.description

    override fun getFirstTag(): String? = operation.tags.first ()

    override fun getDocumentUri(): URI {
        TODO("deriving the package name from the document location is not supported with the openapi4j parser.")
    }
}
