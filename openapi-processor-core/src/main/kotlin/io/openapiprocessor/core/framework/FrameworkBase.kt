/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.framework

import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.datatypes.AnnotationDataType
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.parameters.*
import io.openapiprocessor.core.openapi.Parameter as OpenApiParameter
import io.openapiprocessor.core.openapi.RequestBody as OpenApiRequestBody

/**
 * default implementation of [io.openapiprocessor.core.framework.Framework].
 *
 * extend and override where necessary.
 */
open class FrameworkBase: Framework {

    override fun createQueryParameter(parameter: OpenApiParameter, dataType: DataType): Parameter {
        return QueryParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description
        )
    }

    override fun createHeaderParameter(parameter: OpenApiParameter, dataType: DataType): Parameter {
        return HeaderParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description)
    }

    override fun createCookieParameter(parameter: OpenApiParameter, dataType: DataType): Parameter {
        return CookieParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description)
    }

    override fun createPathParameter(parameter: OpenApiParameter, dataType: DataType): Parameter {
        return PathParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description)
    }

    override fun createMultipartParameter(parameter: OpenApiParameter, dataType: DataType): Parameter {
        return MultipartParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description)
    }

    override fun createAdditionalParameter(parameter: OpenApiParameter, dataType: DataType,
       annotationDataType: AnnotationDataType?): Parameter {

        return AdditionalParameter(
            parameter.getName(),
            dataType,
            annotationDataType,
            parameter.isRequired(),
            false,
            parameter.description)
    }

    override fun createRequestBody(contentType: String, requestBody: OpenApiRequestBody, dataType: DataType): RequestBody {
        return RequestBody(
            "body",
            contentType,
            dataType,
            requestBody.getRequired(),
            false,
            requestBody.description)
    }
}
