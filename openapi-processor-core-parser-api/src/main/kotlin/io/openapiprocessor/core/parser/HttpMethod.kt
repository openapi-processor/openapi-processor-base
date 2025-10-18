/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * http methods.
 */
class HttpMethod private constructor(val method: String) {

    override fun toString(): String {
        return method
    }

    companion object {
        val DELETE = HttpMethod("delete")
        val GET = HttpMethod("get")
        val HEAD = HttpMethod("head")
        val OPTIONS = HttpMethod("options")
        val PATCH = HttpMethod("patch")
        val POST = HttpMethod("post")
        val PUT = HttpMethod("put")
        val TRACE = HttpMethod("trace")

        val values: Array<HttpMethod> = arrayOf(DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE)

        fun values(): Array<HttpMethod> {
            return values.copyOf()
        }

        fun valueOf(method: String): HttpMethod {
            return when (method.lowercase()) {
                "delete" -> DELETE
                "get" -> GET
                "head" -> HEAD
                "options" -> OPTIONS
                "patch" -> PATCH
                "post" -> POST
                "put" -> PUT
                "trace" -> TRACE
                else -> HttpMethod(method)
            }
        }
    }
}
