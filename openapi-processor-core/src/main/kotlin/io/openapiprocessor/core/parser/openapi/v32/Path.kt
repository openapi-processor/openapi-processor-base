/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiparser.model.v32.PathItem
import io.openapiprocessor.core.openapi.HttpMethod as OpenApiHttpMethod
import io.openapiprocessor.core.openapi.Operation as OpenApiOperation
import io.openapiprocessor.core.openapi.Path as OpenApiPath

/**
 * openapi.parser Path abstraction.
 */
class Path(
    private val path: String,
    private val info: PathItem
) : OpenApiPath {

    override fun getPath(): String = path

    override fun getOperations(): List<OpenApiOperation> {
        var pathItem = info
        if (info.isRef) {
            pathItem = info.refObject
        }

        val stdOps = pathItem
            .operations
            .map { Operation(OpenApiHttpMethod.valueOf(it.key.uppercase()), it.value, pathItem) }

        val addOps = pathItem.additionalOperations
            .map { Operation(OpenApiHttpMethod.valueOf(it.key.uppercase()), it.value, pathItem) }

        return stdOps + addOps
    }
}
