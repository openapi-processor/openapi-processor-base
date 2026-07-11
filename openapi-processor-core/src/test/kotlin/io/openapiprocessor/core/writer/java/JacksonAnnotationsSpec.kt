/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.mockk
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.PropertyDataType

class JacksonAnnotationsSpec: StringSpec({

    "provides jackson 2 annotations" {
        val options = ApiOptions()
        options.jackson = "v2"

        val jackson = JacksonAnnotations(options)

        jackson.createPropertyImports(mockk<PropertyDataType>())
            .shouldContainExactly(setOf("com.fasterxml.jackson.annotation.JsonProperty"))

        jackson.jsonCreator.imports shouldContainExactly setOf("com.fasterxml.jackson.annotation.JsonCreator")
        jackson.jsonValue.imports shouldContainExactly setOf("com.fasterxml.jackson.annotation.JsonValue")
    }

    "provides jackson 3 annotations".config(enabled = false) {
        val options = ApiOptions()
        options.jackson = "v3"

        val jackson = JacksonAnnotations(options)

        jackson.createPropertyImports(mockk<PropertyDataType>())
            .shouldContainExactly(setOf("tools.jackson.annotation.JsonProperty"))

        jackson.jsonCreator.imports shouldContainExactly setOf("tools.jackson.annotation.JsonCreator")
        jackson.jsonValue.imports shouldContainExactly setOf("tools.jackson.annotation.JsonValue")
    }
})
