/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.framework

import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.model.Annotation

/**
 * provides annotation details of the framework.
 */
interface FrameworkAnnotations {

    /**
     * provides the details of the requested mapping annotation.
     *
     * @param httpMethod requested http method
     * @return annotation details
     */
    fun getAnnotation(httpMethod: HttpMethod): Annotation

    /**
     * provides the details of the requested method parameter annotation.
     *
     * @param parameter requested parameter
     * @return annotation details
     */
    fun getAnnotation(parameter: Parameter): Annotation

}
