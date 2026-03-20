/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.openapi.HttpMethod as OpenApiHttpMethod
import io.openapiprocessor.core.openapi.Operation as OpenApiOperation
import io.openapiprocessor.core.openapi.Path as OpenApiPath
import io.swagger.v3.oas.models.Operation as SwaggerOperation
import io.swagger.v3.oas.models.PathItem as SwaggerPath

/**
 * Swagger Path abstraction.
 */
class Path(
    private val path: String,
    private val info: SwaggerPath,
    private val refResolver: RefResolverNative
): OpenApiPath {

    override fun getPath(): String = path

    override fun getOperations(): List<OpenApiOperation> {
        val ops: MutableList<OpenApiOperation> = mutableListOf()

        OpenApiHttpMethod.values.forEach {
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
        OpenApiHttpMethod.DELETE.method -> this.delete
        OpenApiHttpMethod.GET.method -> this.get
        OpenApiHttpMethod.HEAD.method -> this.head
        OpenApiHttpMethod.OPTIONS.method -> this.options
        OpenApiHttpMethod.PATCH.method -> this.patch
        OpenApiHttpMethod.POST.method -> this.post
        OpenApiHttpMethod.PUT.method -> this.put
        OpenApiHttpMethod.TRACE.method -> this.trace
        else -> null
    }
}
