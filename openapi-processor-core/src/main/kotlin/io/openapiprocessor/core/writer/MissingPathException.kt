/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer

class MissingPathException(private val path: String): RuntimeException() {

    override val message: String
        get() = "the path '$path' does not exist!"
}
