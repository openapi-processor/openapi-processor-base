/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Annotation

class JacksonAnnotations(apiOptions: ApiOptions) {
    private val jsonProperty: Annotation
    private val jsonCreator: Annotation
    private val jsonValue: Annotation

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

    fun getJsonProperty(): Annotation = jsonProperty
    fun getJsonCreator(): Annotation = jsonCreator
    fun getJsonValue(): Annotation = jsonValue

    private fun getJacksonFormat(options: ApiOptions): JacksonFormat {
        return when (options.jackson) {
            "v2" -> JacksonFormat.V2
            "v3" -> JacksonFormat.V3
            else -> JacksonFormat.V2
        }
    }
}
