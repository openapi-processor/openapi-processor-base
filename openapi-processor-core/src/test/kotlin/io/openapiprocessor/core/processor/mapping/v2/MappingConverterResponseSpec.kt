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
import io.openapiprocessor.core.converter.MappingQuery
import io.openapiprocessor.core.converter.mapping.AmbiguousTypeMappingException
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.responseTypeMatcher

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
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val contentMapping = mappings.findContentTypeMapping(
            responseTypeMatcher(contentType = "application/vnd.array"))!!

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
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val contentMapping = mappings.findContentTypeMapping(
            responseTypeMatcher(contentType = "application/vnd.array"))

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
        val mappings = MappingConverter(mapping).convert().globalMappings

        shouldThrow<AmbiguousTypeMappingException> {
            mappings.findContentTypeMapping(
                responseTypeMatcher(contentType = "application/vnd.array")
            )
        }
    }

    "reads endpoint parameter type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      responses:
           |        - content: application/vnd.array => java.util.List
           |      
           |      get:
           |        responses:
           |          - content: application/vnd.array => java.util.List2
           |
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        // then:
        val contentMapping = mappings["/foo"]!!.findContentTypeMapping(
            MappingQuery(path = "/foo", method = HttpMethod.POST, contentType = "application/vnd.array"))!!

        contentMapping.contentType shouldBe "application/vnd.array"
        contentMapping.mapping.sourceTypeName.shouldBeNull()
        contentMapping.mapping.sourceTypeFormat.shouldBeNull()
        contentMapping.mapping.targetTypeName shouldBe "java.util.List"
        contentMapping.mapping.genericTypes.shouldBeEmpty()

        val contentMappingGet = mappings["/foo"]!!.findContentTypeMapping(
            MappingQuery(path = "/foo", method = HttpMethod.GET, contentType = "application/vnd.array"))!!

        contentMappingGet.contentType shouldBe "application/vnd.array"
        contentMappingGet.mapping.sourceTypeName.shouldBeNull()
        contentMappingGet.mapping.sourceTypeFormat.shouldBeNull()
        contentMappingGet.mapping.targetTypeName shouldBe "java.util.List2"
        contentMappingGet.mapping.genericTypes.shouldBeEmpty()
    }
})
