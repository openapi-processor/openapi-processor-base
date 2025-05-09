/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.EndpointResponseStatus
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.parser.HttpMethod

val MAPPING = Annotation("annotation.Mapping")
val PARAMETER = Annotation("annotation.Parameter")
val STATUS = Annotation("annotation.Status")

/**
 * simple [io.openapiprocessor.core.framework.FrameworkAnnotations] implementation for testing.
 *
 * this also exists as a groovy version
 */
class TestFrameworkAnnotations: FrameworkAnnotations {

    override fun getAnnotation(httpMethod: HttpMethod): Annotation {
        return MAPPING
    }

    override fun getAnnotation(parameter: Parameter): Annotation {
        return PARAMETER
    }

    override fun getAnnotation(status: EndpointResponseStatus): Annotation {
        return STATUS
    }
}
