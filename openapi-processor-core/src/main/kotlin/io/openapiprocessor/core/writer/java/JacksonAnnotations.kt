/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Annotation

/**
 * TODO remove v3
 *
 * Jackson 3 still uses the annotations from the `com.fasterxml.jackson.annotation` package.
 *
 * mapping jon schema definition to configure the jackson version.
 *
 *         "jackson": {
 *           "description": "jackson annotations version.",
 *           "default": "v2",
 *           "enum": ["v2", "v3"]
 *         },
 */
class JacksonAnnotations(apiOptions: ApiOptions) {
    val jsonProperty: Annotation
    val jsonCreator: Annotation
    val jsonValue: Annotation

    init {
        when (getJacksonFormat(apiOptions)) {
            JacksonFormat.V2 -> {
                jsonProperty = Annotation("com.fasterxml.jackson.annotation.JsonProperty")
                jsonCreator = Annotation("com.fasterxml.jackson.annotation.JsonCreator")
                jsonValue = Annotation("com.fasterxml.jackson.annotation.JsonValue")
            }
            JacksonFormat.V3 -> {
                jsonProperty = Annotation("tools.jackson.annotation.JsonProperty")
                jsonCreator = Annotation("tools.jackson.annotation.JsonCreator")
                jsonValue = Annotation("tools.jackson.annotation.JsonValue")
            }
        }
    }

    private fun getJacksonFormat(options: ApiOptions): JacksonFormat {
        return when (options.jackson) {
            "v2" -> JacksonFormat.V2
            "v3" -> JacksonFormat.V3
            else -> JacksonFormat.V2
        }
    }
}
