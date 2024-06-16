/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

/**
 * controls whether @JsonProperty is added to a dto property.
 */
enum class JsonPropertyAnnotationMode(private val mode: String) {
    /** always add @JsonProperty */
    Always("always"),

    /**
     * add @JsonProperty, if the java property name is NOT identical to the OpenAPI property name
     * or set to read/write only.
     */
    Auto("auto");

    companion object {
        fun findBy(mode: String): JsonPropertyAnnotationMode = entries.first { it.mode == mode }
    }
}
