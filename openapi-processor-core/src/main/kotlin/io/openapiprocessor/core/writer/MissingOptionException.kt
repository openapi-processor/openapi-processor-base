/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer

class MissingOptionException(private val option: String): RuntimeException() {

    override val message: String
        get() = "mandatory option '$option' is missing!"
}
