/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

class MappingFormatException(): RuntimeException() {

    override val message: String
        get() = "this mapping format is not supported!"
}
