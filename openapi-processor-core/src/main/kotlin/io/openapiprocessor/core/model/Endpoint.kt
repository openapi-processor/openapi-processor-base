/*
 * Copyright © 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model


import io.openapiprocessor.core.model.parameters.MultipartParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.parser.HttpMethod

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
     * all possible responses for an openapi status ('200', '2xx',... or 'default'). If the given
     * status has no responses the result is an empty list.
     *
     * @param status the response status
     * @return the list of responses
     */
    private fun getResponses (status: String): List<Response> {
        if (!responses.containsKey (status)) {
            return emptyList()
        }
        return responses.getOrDefault(status, emptyList())
    }

    /**
     * provides a set of all consumed content types of this endpoint including multipart/form-data.
     *
     * multipart/form-data is special because a multipart request body with multiple properties is
     * converted to multiple {@link MultipartParameter}s in the internal model. The request body
     * information is no longer available.
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

    // not needed.... => EndpointResponse.getContentTypes()
    // only called by tests?
    fun getProducesContentTypes (status: String): List<String> {
        val responses = getResponses (status)
        val errors = getErrorResponses ()

        val contentTypes = mutableSetOf<String>()
        responses.forEach {
            if (it.empty) {
                return@forEach
            }

            contentTypes.add (it.contentType)
        }

        errors.forEach {
            contentTypes.add (it.contentType)
        }

        return contentTypes.toList ()
    }

    /**
     * test support => extension function ?
     *
     * @param status the response status
     * @return first response of status
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
     * @return true if condition is met, otherwise false.
     */
    fun hasMultipleEndpointResponses(): Boolean {
        return endpointResponses.size > 1
    }

    /**
     * creates groups from the responses.
     *
     * if the endpoint does provide its result in multiple content types it will create one entry
     * for each response kind (main response). if error responses are defined they are added as
     * error responses.
     *
     * this is used to create one controller method for each (successful) response definition.
     *
     * @return list of method responses
     */
    private fun createEndpointResponses(): List<EndpointResponse> {
        val successes = getSuccessResponses()
        val errors = getErrorResponses()

        return successes.map { (key, responses) ->
            EndpointResponse(responses.first(), errors, responses)
        }
    }

    private fun getSuccessResponses(): MutableMap<ContentType, MutableList<Response>> {
        val result = mutableMapOf<ContentType, MutableList<Response>>()

        // prefer responses with content type.
        filterSuccessResponses()
            .filter { hasContentType(it) }
            .forEach {
                var contentValues = result[it.contentType]
                if (contentValues == null) {
                    contentValues = mutableListOf()
                    result[it.contentType] = contentValues
                }
                contentValues += it
            }

        // check for responses without content type (e.g. 204) to generate a void method.
        if (result.isEmpty()) {
            filterSuccessResponses()
                .forEach {
                    var contentValues = result[it.contentType]
                    if (contentValues == null) {
                        contentValues = mutableListOf()
                        result[it.contentType] = contentValues
                    }
                    contentValues += it
                }
        }

        return result
    }

    private fun getErrorResponses(): Set<Response> {
        return responses
            .filterKeys { !isSuccessCode(it) }
            .values
            .map { it.first() }
            .filter { !it.empty }
            .toSet()
    }

    private fun filterSuccessResponses() = responses
        .filterKeys { isSuccessCode(it) }
        .values
        .flatten()

    private fun isSuccessCode(code: String) = code.startsWith("2")

    private fun hasContentType(response: Response) = response.contentType != "?"
}
