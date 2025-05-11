/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model


import io.openapiprocessor.core.model.parameters.MultipartParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.parser.HttpMethod
import kotlin.collections.plusAssign

/**
 * Endpoint properties.
 */
class Endpoint(
    val path: String,
    val method: HttpMethod,
    val parameters: List<Parameter>,
    val requestBodies: List<RequestBody>,
    val responses: Map<HttpStatus, List<Response>>,
    val operationId: String? = null,
    val deprecated: Boolean = false,
    private val documentation: Documentation? = null
) {
    // grouped responses
    val endpointResponses: List<EndpointResponse> = createEndpointResponses()

    fun getRequestBody(): RequestBody {
        return requestBodies.first ()
    }

    /**
     * all possible responses for an openapi status ('200', '2xx', ... or 'default'). If the given
     * status has no responses, the result is an empty list.
     *
     * @param status the response status
     * @return the list of responses
     */
    @Deprecated("only used in class")
    private fun getResponses (status: String): List<ResponseWithStatus> {
        if (!responses.containsKey (status)) {
            return emptyList()
        }
        return responses.getOrDefault(status, emptyList())
            .map { ResponseWithStatus(status, it) }
    }

    /**
     * provides a set of all consumed content types of this endpoint including multipart/form-data.
     *
     * multipart/form-data is special because a multipart request body with multiple properties is
     * converted to multiple {@link MultipartParameter}s in the internal model. The request body
     * information is no longer available.
     *
     * used by openapi-processor-spring, ...
     *
     * @return the set of content types
     */
    fun getConsumesContentTypes(): Set<String> {
        val contentTypes = requestBodies
            .map { it.contentType }
            .toMutableSet()

        val multipart = parameters.find {
            it is MultipartParameter
        }

        if (multipart != null) {
            contentTypes.add ("multipart/form-data")
        }

        return contentTypes
    }

    @Deprecated("only used in test")
    fun getProducesContentTypes (status: String): Set<String> {
        return endpointResponses
            .map { it.contentTypes }
            .flatten()
            .toSet()
    }

    /**
     * test support => extension function?
     *
     * @param status the response status
     * @return first response of status
     *
     * only used in test
     */
    fun getFirstResponse (status: String): Response? {
        if (!responses.containsKey (status)) {
            return null
        }

        val resp = responses[status]
        if (resp != null && resp.isEmpty()) {
            return null
        }

        return resp?.first ()
    }

    val summary: String?
        get() = documentation?.summary

    val description: String?
        get() = documentation?.description

    /**
     * checks if the endpoint has multiple success responses with different content types.
     *
     * @return true if the condition is met, otherwise false.
     */
    fun hasMultipleEndpointResponses(): Boolean {
        return endpointResponses.size > 1
    }

    /**
     * creates groups from the responses.
     *
     * if the endpoint does provide its result in multiple content types, it will create one entry
     * for each response kind (main response). if error responses are defined, they are added as
     * error responses.
     *
     * this is used to create one controller method for each (successful) response definition.
     *
     * @return list of method responses
     */
    private fun createEndpointResponses(): List<EndpointResponse> {
        val successes = getSuccessResponses()
        val errors = getErrorResponses()

        return successes.map { (_, statusResponses) ->
            EndpointResponse(statusResponses.first(), errors, statusResponses)
        }
    }

    private fun getSuccessResponses(): MutableMap<ContentType, MutableList<ResponseWithStatus>> {
        val result = mutableMapOf<ContentType, MutableList<ResponseWithStatus>>()

        responses
            .filterKeys { isSuccessCode(it) }
            .forEach { entry ->
                val status = entry.key

                entry.value
                    .filter { hasContentType(it) }
                    .forEach { response ->
                        var contentValues = result[response.contentType]
                        if (contentValues == null) {
                            contentValues = mutableListOf()
                            result[response.contentType] = contentValues
                        }
                        contentValues += ResponseWithStatus(status, response)
                    }
            }

        // check for responses without a content type (e.g., 204) to generate a void method.
        if (result.isEmpty()) {
            responses
                .filterKeys { isSuccessCode(it) }
                .forEach { entry ->
                    val status = entry.key

                    entry.value
                        .forEach { response ->
                            var contentValues = result[response.contentType]
                            if (contentValues == null) {
                                contentValues = mutableListOf()
                                result[response.contentType] = contentValues
                            }
                            contentValues += ResponseWithStatus(status, response)
                        }
                }
        }

        return result
    }

    private fun getErrorResponses(): Set<ResponseWithStatus> {
        return responses
            .filterKeys { !isSuccessCode(it) }
            .map { entry ->
                val status = entry.key
                val response = entry.value.first()
                ResponseWithStatus(status, response)
            }
            .filter { !it.response.empty }
            .toSet()
    }

    private fun isSuccessCode(code: String) = code.startsWith("2")

    private fun hasContentType(response: Response) = response.contentType != "?"
}
