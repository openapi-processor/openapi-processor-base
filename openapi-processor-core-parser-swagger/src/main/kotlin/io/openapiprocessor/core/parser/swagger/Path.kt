/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Path as ParserPath
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.oas.models.Operation as SwaggerOperation

/**
 * Swagger Path abstraction.
 */
class Path(
    private val path: String,
    private val info: SwaggerPath,
    private val refResolver: RefResolverNative
): ParserPath {

    override fun getPath(): String = path

    override fun getOperations(): List<ParserOperation> {
        val ops: MutableList<ParserOperation> = mutableListOf()

        HttpMethod.entries.map {
            val op = info.getOperation(it.method)
            if (op != null) {
                ops.add (Operation(it, op, info, refResolver))
            }
        }

        return ops
    }

}

fun SwaggerPath.getOperation(method: String): SwaggerOperation? {
    return when(method) {
        HttpMethod.DELETE.method -> this.delete
        HttpMethod.GET.method -> this.get
        HttpMethod.HEAD.method -> this.head
        HttpMethod.OPTIONS.method -> this.options
        HttpMethod.PATCH.method -> this.patch
        HttpMethod.POST.method -> this.post
        HttpMethod.PUT.method -> this.put
        HttpMethod.TRACE.method -> this.trace
        else -> null
    }
}
