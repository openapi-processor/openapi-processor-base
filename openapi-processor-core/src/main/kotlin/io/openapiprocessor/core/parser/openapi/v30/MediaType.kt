/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiparser.model.v30.MediaType as MediaType30
import io.openapiprocessor.core.openapi.Encoding as OpenApiEncoding
import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType

/**
 * openapi-parser MediaType abstraction.
 */
class MediaType(val mediaType: MediaType30): OpenApiMediaType {
    override fun getSchema() = Schema(mediaType.schema!!)

    override val encodings: Map<String, OpenApiEncoding>
        get() {
            val encoding = mutableMapOf<String, OpenApiEncoding>()
            mediaType.encoding.forEach {
                encoding[it.key] = OpenApiEncoding(it.value.contentType)
            }
            return encoding
        }
}
