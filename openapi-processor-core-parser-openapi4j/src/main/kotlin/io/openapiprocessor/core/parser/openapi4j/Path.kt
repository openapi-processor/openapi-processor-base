/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.openapi.Path as OpenApiPath
import io.openapiprocessor.core.openapi.HttpMethod as OpenApiHttpMethod
import io.openapiprocessor.core.openapi.Operation as OpenApiOperation
import org.openapi4j.parser.model.v3.Path as Oa4jPath

/**
 * openapi4j Path abstraction.
 */
class Path(
    private val path: String,
    private val info: Oa4jPath,
    private val refResolver: RefResolverNative
): OpenApiPath {

    override fun getPath(): String = path

    override fun getOperations(): List<OpenApiOperation> {
        return info
            .operations
            .map { Operation(OpenApiHttpMethod.valueOf(it.key.uppercase()), it.value, info, refResolver) }
    }
}
