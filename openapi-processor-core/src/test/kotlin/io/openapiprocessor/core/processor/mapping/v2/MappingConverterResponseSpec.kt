/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.AmbiguousTypeMappingException
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.MappingSchema

class MappingConverterResponseSpec: StringSpec({
    val reader = MappingReader()

    "read global response type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |map:
           |  responses:
           |    - content: application/vnd.array => java.util.List
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val contentMapping = mappings.findGlobalContentTypeMapping(
            MappingSchema(contentType = "application/vnd.array"))!!

        contentMapping.contentType shouldBe "application/vnd.array"
        contentMapping.mapping.sourceTypeName.shouldBeNull()
        contentMapping.mapping.sourceTypeFormat.shouldBeNull()
        contentMapping.mapping.targetTypeName shouldBe "java.util.List"
        contentMapping.mapping.genericTypes.shouldBeEmpty()
    }

    "missing global response type mapping returns null" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val contentMapping = mappings.findGlobalContentTypeMapping(
            MappingSchema(contentType = "application/vnd.array"))

        contentMapping.shouldBeNull()
    }

    "duplicate global response type mapping throws" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           |map:
           |  responses:
           |    - content: application/vnd.array => java.util.List
           |    - content: application/vnd.array => java.util.List
           |
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convertX()

        shouldThrow<AmbiguousTypeMappingException> {
            mappings.findGlobalContentTypeMapping(
                MappingSchema(contentType = "application/vnd.array")
            )
        }
    }
})
