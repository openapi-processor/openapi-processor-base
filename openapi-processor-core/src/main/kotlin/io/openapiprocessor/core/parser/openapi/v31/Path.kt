/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiparser.model.v31.PathItem
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

        return pathItem
            .operations
            .map { Operation(OpenApiHttpMethod.valueOf(it.key.uppercase()), it.value, pathItem) }
    }
}
