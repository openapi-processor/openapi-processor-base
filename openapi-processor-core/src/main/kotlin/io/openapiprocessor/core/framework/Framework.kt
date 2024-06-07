/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.framework

import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.datatypes.AnnotationDataType
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody

/**
 * factory for framework model objects.
 */
interface Framework {

    /**
     * create a model query parameter.
     *
     * @param parameter an OpenAPI query parameter
     * @param dataType data type of the parameter
     * @return a query {@link Parameter}
     */
    fun createQueryParameter(parameter: ParserParameter, dataType: DataType): Parameter

    /**
     * create a model header parameter.
     *
     * @param parameter an OpenAPI header parameter
     * @param dataType data type of the parameter
     * @return a header {@link Parameter}
     */
    fun createHeaderParameter(parameter: ParserParameter, dataType: DataType): Parameter

    /**
     * create a model cookie parameter.
     *
     * @param parameter an OpenAPI cookie parameter
     * @param dataType data type of the parameter
     * @return a cookie {@link Parameter}
     */
    fun createCookieParameter(parameter: ParserParameter, dataType: DataType): Parameter

    /**
     * create a model path parameter.
     *
     * @param parameter an OpenAPI path parameter
     * @param dataType data type of the parameter
     * @return a path {@link Parameter}
     */
    fun createPathParameter(parameter: ParserParameter, dataType: DataType): Parameter

    /**
     * create a model multipart parameter.
     *
     * @param parameter an OpenAPI multipart parameter
     * @param dataType data type of the parameter
     * @return a multipart {@link Parameter}
     */
    fun createMultipartParameter(parameter: ParserParameter, dataType: DataType): Parameter

    /**
     * create a model additional parameter.
     *
     * @param parameter an OpenAPI additional parameter
     * @param dataType data type of the parameter
     * @param annotationDataType additional annotation
     * @return an additional {@link Parameter}
     */
    fun createAdditionalParameter(parameter: ParserParameter, dataType: DataType,
        annotationDataType: AnnotationDataType? = null): Parameter

    /**
     * create a model request body.
     *
     * @param contentType an OpenAPI request body content type
     * @param requestBody an OpenAPI request body
     * @param dataType data type of the request body
     * @return an additional {@link RequestBody}
     */
    fun createRequestBody(contentType: String, requestBody: ParserRequestBody, dataType: DataType): RequestBody
}
