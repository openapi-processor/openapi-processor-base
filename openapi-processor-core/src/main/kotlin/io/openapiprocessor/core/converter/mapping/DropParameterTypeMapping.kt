/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * configure parameters that should be dropped.
 */
class DropParameterTypeMapping(

    /**
     * The parameter name of this mapping.
     */
    val parameterName: String

): Mapping
