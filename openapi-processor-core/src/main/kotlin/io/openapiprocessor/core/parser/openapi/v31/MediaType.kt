/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiprocessor.core.openapi.Encoding
import io.openapiparser.model.v31.MediaType as MediaType31
import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType

/**
 * openapi-parser MediaType abstraction.
 */
class MediaType(val mediaType: MediaType31): OpenApiMediaType {
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
