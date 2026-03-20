/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.openapi.Encoding
import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType
import io.swagger.v3.oas.models.media.MediaType as SwaggerMediaType

/**
 * Swagger MediaType abstraction.
 */
class MediaType(val mediaType: SwaggerMediaType): OpenApiMediaType {
    override fun getSchema() = Schema(mediaType.schema)

    override val encodings: Map<String, Encoding>
        get() {
            val encoding = mutableMapOf<String, Encoding>()
            mediaType.encoding?.forEach {
                encoding[it.key] = Encoding(it.value.contentType)
            }
            return encoding
        }
}
