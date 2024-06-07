/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.parameters.ParameterBase

/**
 * Endpoint request body properties.
 */
class RequestBody(
    name: String,
    val contentType: String,
    dataType: DataType,
    required: Boolean = false,
    deprecated: Boolean = false,
    description: String? = null
): ParameterBase(name, dataType, required, deprecated, description)
