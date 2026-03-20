/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.openapi.Encoding
import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType
import org.openapi4j.parser.model.v3.MediaType as O4jMediaType

/**
 * openapi4j MediaType abstraction.
 */
class MediaType(val mediaType: O4jMediaType): OpenApiMediaType {
    override fun getSchema() = Schema(mediaType.schema)

    override val encodings: Map<String, Encoding>
        get() {
            val encoding = mutableMapOf<String, Encoding>()
            mediaType.encodings?.forEach {
                encoding[it.key] = Encoding(it.value.contentType)
            }
            return encoding
        }
}
