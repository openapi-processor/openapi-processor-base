/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * "fake" parameter for framework-specific annotation selection
 */
class UrlencodedRequestBody(val body: RequestBody): Parameter {

    override fun getIn(): String {
        return "urlencoded"
    }

    override fun getName(): String {
        return "body"
    }

    override fun getSchema(): Schema {
        throw NotImplementedError() // never called
    }

    override fun isRequired(): Boolean {
        return body.getRequired()
    }

    override fun isDeprecated(): Boolean {
        return false
    }

    override val description: String?
        get() = body.description

}
