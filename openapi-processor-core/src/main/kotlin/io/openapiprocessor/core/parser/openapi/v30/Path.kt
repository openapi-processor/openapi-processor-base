/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiparser.model.v30.PathItem
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.openapiprocessor.core.parser.Path as ParserPath

/**
 * openapi.parser Path abstraction.
 */
class Path(
    private val path: String,
    private val info: PathItem
) : ParserPath {

    override fun getPath(): String = path

    override fun getOperations(): List<ParserOperation> {
        var pathItem = info
        if (info.isRef) {
            pathItem = info.refObject
        }

        return pathItem
            .operations
            .map { Operation(HttpMethod.valueOf(it.key.uppercase()), it.value, pathItem) }
    }
}
