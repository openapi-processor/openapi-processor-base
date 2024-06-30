/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.MappingFinderX
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.processor.mapping.v2.Mapping
import io.openapiprocessor.core.processor.mapping.v2.MappingConverter
import io.openapiprocessor.core.support.MappingSchema


class MappingFinderXSpec: StringSpec({
    val reader = MappingReader()

    "get result type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  result: io.openapiprocessor.WrapGlobal
           |  paths:
           |    /foo:
           |      result: io.openapiprocessor.WrapPath
           |      
           |      get:
           |        result: io.openapiprocessor.WrapMethod
           |      
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convertX()
        val finder = MappingFinderX(mappings)

        // then:
        val resultTypeMapping = finder.getResultTypeMapping(
            MappingSchema(path = "/bar", method = HttpMethod.GET))!!

        resultTypeMapping.targetTypeName shouldBe "io.openapiprocessor.WrapGlobal"
        resultTypeMapping.genericTypes.shouldBeEmpty()

        val resultTypeMappingPath = mappings.getEndpointResultTypeMapping(
            MappingSchema(path = "/foo", method = HttpMethod.POST))!!

        resultTypeMappingPath.targetTypeName shouldBe "io.openapiprocessor.WrapPath"
        resultTypeMappingPath.genericTypes.shouldBeEmpty()

        val resultTypeMappingGet = mappings.getEndpointResultTypeMapping(
            MappingSchema(path = "/foo", method = HttpMethod.GET)
        )!!

        resultTypeMappingGet.targetTypeName shouldBe "io.openapiprocessor.WrapMethod"
        resultTypeMappingGet.genericTypes.shouldBeEmpty()
    }

    "reads single & multi type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  single: reactor.core.publisher.MonoGlobal
           |  multi: reactor.core.publisher.FluxGlobal
           |      
           |  paths:
           |    /foo:
           |      single: reactor.core.publisher.MonoPath
           |      multi: reactor.core.publisher.FluxPath
           |      
           |      get:
           |        single: reactor.core.publisher.MonoMethod
           |        multi: reactor.core.publisher.FluxMethod
           
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convertX()
        val finder = MappingFinderX(mappings)

        // then:
        val singleTypeMappingG = finder.getSingleTypeMapping(
            MappingSchema(path = "/bar", method = HttpMethod.GET))!!

        singleTypeMappingG.sourceTypeName shouldBe "single"
        singleTypeMappingG.targetTypeName shouldBe "reactor.core.publisher.MonoGlobal"

        val multiTypeMappingG = finder.getMultiTypeMapping(
            MappingSchema(path = "/bar", method = HttpMethod.GET))!!

        multiTypeMappingG.sourceTypeName shouldBe "multi"
        multiTypeMappingG.targetTypeName shouldBe "reactor.core.publisher.FluxGlobal"

        val singleTypeMappingP = finder.getSingleTypeMapping(
            MappingSchema(path = "/foo", method = HttpMethod.POST))!!

        singleTypeMappingP.sourceTypeName shouldBe "single"
        singleTypeMappingP.targetTypeName shouldBe "reactor.core.publisher.MonoPath"

        val multiTypeMappingP = finder.getMultiTypeMapping(
            MappingSchema(path = "/foo", method = HttpMethod.POST))!!

        multiTypeMappingP.sourceTypeName shouldBe "multi"
        multiTypeMappingP.targetTypeName shouldBe "reactor.core.publisher.FluxPath"

        val singleTypeMappingGet = finder.getSingleTypeMapping(
            MappingSchema(path = "/foo", method = HttpMethod.GET))!!

        singleTypeMappingGet.sourceTypeName shouldBe "single"
        singleTypeMappingGet.targetTypeName shouldBe "reactor.core.publisher.MonoMethod"

        val multiTypeMappingGet = finder.getMultiTypeMapping(
            MappingSchema(path = "/foo", method = HttpMethod.GET))!!

        multiTypeMappingGet.sourceTypeName shouldBe "multi"
        multiTypeMappingGet.targetTypeName shouldBe "reactor.core.publisher.FluxMethod"
    }
})
