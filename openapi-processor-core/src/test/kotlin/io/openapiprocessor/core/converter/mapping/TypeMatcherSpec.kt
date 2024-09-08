/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.converter.mapping.matcher.TypeMatcher

class TypeMatcherSpec: StringSpec({

    fun createSchema(name: String, format: String?): MappingSchema {
        return MappingSchemaPlain(
            name = name,
            type = "any",
            format = format
        )
    }

    fun createPrimitiveSchema(type: String, format: String?): MappingSchema {
        return MappingSchemaPlain(
            name = "any",
            type = type,
            format = format,
            primitive = true
        )
    }

    fun createArraySchema(): MappingSchema {
        return MappingSchemaPlain(
            name = "array",
            type = "any",
            array = true
        )
    }

    "does not match if name differs" {
        val mapping = TypeMapping("other Name", null,"Target")
        val matcher = TypeMatcher(MappingFinderQuery(createSchema("Name", null)))

        matcher(mapping).shouldBeFalse()
    }

    "does not match if format differs" {
        val mapping = TypeMapping("Name", "other format","Target")
        val matcher = TypeMatcher(MappingFinderQuery(createSchema("Name", "a format")))

        matcher(mapping).shouldBeFalse()
    }

    "matches by name and null format" {
        val mapping = TypeMapping("Name", null,"Target")
        val matcher = TypeMatcher(MappingFinderQuery(createSchema("Name", null)))

        matcher(mapping).shouldBeTrue()
    }

    "matches by name and format" {
        val mapping = TypeMapping("Name", "format","Target")
        val matcher = TypeMatcher(MappingFinderQuery(createSchema("Name", "format")))

        matcher(mapping).shouldBeTrue()
    }

    "does not match primitive if type differs" {
        val mapping = TypeMapping("other type", null,"Target")
        val matcher = TypeMatcher(MappingFinderQuery(createPrimitiveSchema("type", null)))

        matcher(mapping).shouldBeFalse()
    }

    "does not match primitive if format differs" {
        val mapping = TypeMapping("type", "other format","Target")
        val matcher = TypeMatcher(MappingFinderQuery(createPrimitiveSchema("type", "a format")))

        matcher(mapping).shouldBeFalse()
    }

    "matches primitive by type and null format" {
        val mapping = TypeMapping("type", null,"Target")
        val matcher = TypeMatcher(MappingFinderQuery(createPrimitiveSchema("type", null)))

        matcher(mapping).shouldBeTrue()
    }

    "matches primitive by type and format" {
        val mapping = TypeMapping("type", "format","Target")
        val matcher = TypeMatcher(MappingFinderQuery(createPrimitiveSchema("type", "format")))

        matcher(mapping).shouldBeTrue()
    }

    "matches array by name" {
        val mapping = TypeMapping("array", null,"Target")
        val matcher = TypeMatcher(MappingFinderQuery(createArraySchema()))

        matcher(mapping).shouldBeTrue()
    }
})
