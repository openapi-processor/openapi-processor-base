/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.parser.HttpMethod

val MAPPING = Annotation("annotation.Mapping")
val PARAMETER = Annotation("annotation.Parameter")

/**
 * simple [io.openapiprocessor.core.framework.FrameworkAnnotations] implementation for testing.
 */
class TestFrameworkAnnotations: FrameworkAnnotations {

    override fun getAnnotation(httpMethod: HttpMethod): Annotation {
        return MAPPING
    }

    override fun getAnnotation(parameter: Parameter): Annotation {
        return PARAMETER
    }
}
