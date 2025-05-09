/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.AnyOneOfObjectDataType
import io.openapiprocessor.core.model.datatypes.ResultDataType
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

/**
 * The responses that can be returned by an endpoint method for one (successful) response.
 */
class EndpointResponse(
    /**
     * success response
     */
    @Deprecated("use successes", replaceWith = ReplaceWith("successes"))
    private val main: ResponseWithStatus,

    /**
     * additional (error) responses
     */
    private val errors: Set<ResponseWithStatus>,

    /**
     * todo replace main
     * success responses
     */
    private val successes: List<ResponseWithStatus> = listOf()
): EndpointResponseStatus {

    val success: Response
        get() = main.response

    // todo all have the same, maybe pass as parameter
    val contentType: String
        get() = success.contentType

    override val statusCode: HttpStatus
        get() {
            if (successes.size == 1) {
                return successes.first().status
            }

            if (errors.size == 1) {
                return errors.first().status
            }

            throw IllegalStateException("multiple successful or error responses without status code")
        }

    /**
     * Provides the response type based on the requested style.
     *
     * [ResultStyle.SUCCESS]
     * - Response type is the single success response type regardless of the number of available
     * error responses.
     *
     * [ResultStyle.ALL]
     * - If the endpoint has multiple responses the response type is `Object`. If the response is
     * wrapped by a result data type (i.e., wrapper) the response type is `ResultDataType<?>`.
     * - If the endpoint has only a success response type it is used as the response type.
     *
     * @param style required style
     * @return the response data type in the requested style
     */
    fun getResponseType(style: ResultStyle): String {
        if (isAnyOneOfResponse())
            return getMultiResponseTypeName()

        if (style == ResultStyle.ALL && errors.isNotEmpty())
            return getMultiResponseTypeName()

        val distinct = getDistinctResponseTypes()
        if (distinct.size > 1)
            return getMultiResponseTypeName()

        return getSingleResponseTypeName()
    }

    /**
     * Check if the response has a single response status not equal to 200 ok. It is used to check if it is possible
     * to generate code that automatically returns the status code (like annotation the endpoint method).
     *
     * [ResultStyle.SUCCESS]
     * - True if there is a single response status not equal to 200, i.e., any success status
     *
     * [ResultStyle.ALL]
     * - True if there is a single response status not equal to 200, i.e., any status
     */
    fun hasSingleResponse(style: ResultStyle): Boolean {
        if (style == ResultStyle.ALL) {
            if (successes.size == 1 && errors.isEmpty()) {
                return successes.first().status != "200"
            }
        } else if (style == ResultStyle.SUCCESS) {
            if (successes.size == 1) {
                return successes.first().status != "200"
            }
        }
        return false
    }

    /**
     * test only: provides the response type.
     */
    val responseType: String
        get() {
            return getResponseType(ResultStyle.SUCCESS)
        }

    val description: String?
    get() = success.description

    /**
     * provides the imports required for {@link #getResponseType()}.
     *
     * @param style required style
     * @return list of imports
     */

    fun getResponseImports(style: ResultStyle): Set<String> {
        if (isAnyOneOfResponse())
            return getImportsMulti()

        if (style == ResultStyle.ALL && errors.isNotEmpty())
            return getImportsMulti()

        val distinct = getDistinctResponseTypes()
        if (distinct.size > 1)
            return getImportsMulti()

        return getImportsSingle()
    }

    /**
     * returns a list with all content types.
     *
     * todo only called by test code
     */
    val contentTypes: Set<String>
        get() {
            val result = mutableSetOf<String>()
            if (!success.empty) {
                result.add(success.contentType)
            }

            errors.forEach {
                result.add(it.response.contentType)
            }
            return result
        }

    private fun isAnyOneOfResponse(): Boolean {
        return success.responseType is AnyOneOfObjectDataType
    }

    /**
     * Object or ResultDataType<?> if wrapped
     */
    private fun getMultiResponseTypeName(): String {
        val rt = success.responseType
        if (rt is ResultDataType) {
            return rt.getNameMulti()
        }
        return "Object"
    }

    private fun getSingleResponseTypeName(): String {
        val types = getDistinctResponseTypes()
            .map { r -> r.response.responseType.getTypeName() }

        if(types.size != 1) {
            throw IllegalStateException("ambiguous response types: $types")
        }

        return types.first()
    }

    private fun getImportsMulti(): Set<String> {
        val rt = success.responseType
        return if (rt is ResultDataType) {
            rt.getImportsMulti()
        } else {
            emptySet()
        }
    }

    private fun getImportsSingle(): Set<String> {
        return success.imports
    }

    private fun getDistinctResponseTypes(): List<ResponseWithStatus> {
        return successes.distinctBy { statusResponse ->  statusResponse.response.responseType.getTypeName() }
    }
}
