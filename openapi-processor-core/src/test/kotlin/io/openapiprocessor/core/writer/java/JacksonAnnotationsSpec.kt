/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.openapiprocessor.core.converter.ApiOptions

class JacksonAnnotationsSpec: StringSpec({

    "provides jackson 2 annotations" {
        val options = ApiOptions()
        options.jackson = "v2"

        val jackson = JacksonAnnotations(options)

        jackson.getJsonProperty().imports shouldContainExactly setOf("com.fasterxml.jackson.annotation.JsonProperty")
        jackson.getJsonCreator().imports shouldContainExactly setOf("com.fasterxml.jackson.annotation.JsonCreator")
        jackson.getJsonValue().imports shouldContainExactly setOf("com.fasterxml.jackson.annotation.JsonValue")
    }

    "provides jackson 3 annotations" {
        val options = ApiOptions()
        options.jackson = "v3"

        val jackson = JacksonAnnotations(options)

        jackson.getJsonProperty().imports shouldContainExactly setOf("tools.jackson.annotation.JsonProperty")
        jackson.getJsonCreator().imports shouldContainExactly setOf("tools.jackson.annotation.JsonCreator")
        jackson.getJsonValue().imports shouldContainExactly setOf("tools.jackson.annotation.JsonValue")
    }
})
