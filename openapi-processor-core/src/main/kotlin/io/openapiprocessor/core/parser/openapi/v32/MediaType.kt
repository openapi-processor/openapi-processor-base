/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiprocessor.core.parser.Encoding
import io.openapiparser.model.v32.MediaType as MediaType32
import io.openapiprocessor.core.parser.MediaType as ParserMediaType

/**
 * openapi-parser MediaType abstraction.
 */
class MediaType(val mediaType: MediaType32): ParserMediaType {
    override fun getSchema() = Schema(mediaType.schema!!)

    override val encodings: Map<String, Encoding>
        get() {
            val encoding = mutableMapOf<String, Encoding>()
            mediaType.encoding.forEach {
                encoding[it.key] = Encoding(it.value.contentType)
            }
            return encoding
        }
}
