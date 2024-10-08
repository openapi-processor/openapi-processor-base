/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterReactiveSpec: StringSpec({
    val reader = MappingReader()

    "reads global single & multi type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  single: reactor.core.publisher.Mono
           |  multi: reactor.core.publisher.Flux
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val singleTypeMapping = mappings.getSingleTypeMapping()!!
        singleTypeMapping.sourceTypeName shouldBe "single"
        singleTypeMapping.targetTypeName shouldBe "reactor.core.publisher.Mono"

        val multiTypeMapping = mappings.getMultiTypeMapping()!!
        multiTypeMapping.sourceTypeName shouldBe "multi"
        multiTypeMapping.targetTypeName shouldBe "reactor.core.publisher.Flux"
    }

    "reads endpoint single & multi type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      single: reactor.core.publisher.Mono
           |      multi: reactor.core.publisher.Flux
           |      
           |      get:
           |        single: reactor.core.publisher.Mono2
           |        multi: reactor.core.publisher.Flux2
           
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        // then:
        val singleTypeMapping = mappings["/foo"]!!.getSingleTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST))!!

        singleTypeMapping.sourceTypeName shouldBe "single"
        singleTypeMapping.targetTypeName shouldBe "reactor.core.publisher.Mono"

        val multiTypeMapping = mappings["/foo"]!!.getMultiTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST))!!

        multiTypeMapping.sourceTypeName shouldBe "multi"
        multiTypeMapping.targetTypeName shouldBe "reactor.core.publisher.Flux"


        val singleTypeMappingGet = mappings["/foo"]!!.getSingleTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET))!!

        singleTypeMappingGet.sourceTypeName shouldBe "single"
        singleTypeMappingGet.targetTypeName shouldBe "reactor.core.publisher.Mono2"

        val multiTypeMappingGet = mappings["/foo"]!!.getMultiTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET))!!

        multiTypeMappingGet.sourceTypeName shouldBe "multi"
        multiTypeMappingGet.targetTypeName shouldBe "reactor.core.publisher.Flux2"
    }
})
