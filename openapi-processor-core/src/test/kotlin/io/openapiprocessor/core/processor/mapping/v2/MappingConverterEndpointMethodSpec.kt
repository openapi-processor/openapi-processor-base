/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.query

class MappingConverterEndpointMethodSpec: StringSpec({

    val reader = MappingReader()
    val converter = MappingConverter()

    "reads endpoint method type mapping" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |options: {} 
                   |map:
                   |  paths:
                   |    /foo:
                   |      get:
                   |        types:
                   |         - type: Foo => io.openapiprocessor.Foo
                   """.trimMargin()

        // when:
        val mappingData = converter.convert(reader.read(yaml))

        mappingData.endpointMappings.shouldHaveSize(1)
        val epMappings = mappingData.endpointMappings["/foo"]
        val mapping = epMappings?.findTypeMapping(query(path = "/foo", method = HttpMethod.GET, name = "Foo"))

        mapping!!.sourceTypeName shouldBe "Foo"
        mapping.sourceTypeFormat.shouldBeNull()
        mapping.targetTypeName shouldBe "io.openapiprocessor.Foo"
        mapping.genericTypes.shouldBeEmpty()
    }

    "reads any endpoint method type mappings" {
        forAll(
            row(HttpMethod.GET),
            row(HttpMethod.PUT),
            row(HttpMethod.POST),
            row(HttpMethod.DELETE),
            row(HttpMethod.OPTIONS),
            row(HttpMethod.HEAD),
            row(HttpMethod.PATCH),
            row(HttpMethod.TRACE)
        ) { method ->
            val yaml =
                """
                |openapi-processor-mapping: v2
                |options: {} 
                |map:
                |  paths:
                |    /foo:
                |      ${method.method}:
                |        types:
                |         - type: Foo => io.openapiprocessor.Foo
                """.trimMargin()

            // when:
            val mappingData = converter.convert(reader.read(yaml))

            mappingData.endpointMappings.shouldHaveSize(1)
            val epMappings = mappingData.endpointMappings["/foo"]
            val mapping = epMappings?.findTypeMapping(query(path = "/foo", method = method, name = "Foo"))

            mapping!!.sourceTypeName shouldBe "Foo"
            mapping.sourceTypeFormat.shouldBeNull()
            mapping.targetTypeName shouldBe "io.openapiprocessor.Foo"
            mapping.genericTypes.shouldBeEmpty()
        }
    }
})
