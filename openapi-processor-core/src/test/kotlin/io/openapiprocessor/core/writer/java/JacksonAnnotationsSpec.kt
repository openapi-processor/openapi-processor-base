/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.PropertyDataType
import io.openapiprocessor.core.support.datatypes.propertyDataTypeString

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

    "creates jackson property annotation data" {
        val options = ApiOptions()
        val jackson = JacksonAnnotations(options)

        val prop = propertyDataTypeString()
        val annotation = jackson.createPropertyAnnotation("foo", prop)

        annotation.text shouldBe """@JsonProperty("foo")"""
        annotation.imports shouldContainExactly setOf("com.fasterxml.jackson.annotation.JsonProperty")
    }

    "creates jackson read-only property annotation data" {
        val options = ApiOptions()
        val jackson = JacksonAnnotations(options)

        val prop = propertyDataTypeString(readOnly = true)
        val annotation = jackson.createPropertyAnnotation("foo", prop)

        annotation.text shouldBe """@JsonProperty(value = "foo", access = JsonProperty.Access.READ_ONLY)"""
        annotation.imports shouldContainExactly setOf("com.fasterxml.jackson.annotation.JsonProperty")
    }

    "creates jackson write-only property annotation data" {
        val options = ApiOptions()
        val jackson = JacksonAnnotations(options)

        val prop = propertyDataTypeString(writeOnly = true)
        val annotation = jackson.createPropertyAnnotation("foo", prop)

        annotation.text shouldBe """@JsonProperty(value = "foo", access = JsonProperty.Access.WRITE_ONLY)"""
        annotation.imports shouldContainExactly setOf("com.fasterxml.jackson.annotation.JsonProperty")
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
