/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterResultSpec: StringSpec({
    val reader = MappingReader()

    "read global result type mapping, plain" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  result: plain
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val resultTypeMapping = mappings.getResultTypeMapping()!!

        resultTypeMapping.targetTypeName shouldBe "plain"
        resultTypeMapping.genericTypes.shouldBeEmpty()
    }

    "read global result type mapping, class" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  result: io.openapiprocessor.Wrap
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val resultTypeMapping = mappings.getResultTypeMapping()!!

        resultTypeMapping.targetTypeName shouldBe "io.openapiprocessor.Wrap"
        resultTypeMapping.genericTypes.shouldBeEmpty()
    }

    "read global result style, null" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map: {}
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val resultStyle = mappings.getResultStyle()

        resultStyle.shouldBeNull()
    }

    "read global result style, success" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  result-style: success
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val resultStyle = mappings.getResultStyle()!!

        resultStyle.shouldBe(ResultStyle.SUCCESS)
    }

    "read global result style, all" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  result-style: all
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val resultStyle = mappings.getResultStyle()!!

        resultStyle.shouldBe(ResultStyle.ALL)
    }

    "read endpoint result type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      result: io.openapiprocessor.WrapAll
           |      
           |      get:
           |        result: io.openapiprocessor.WrapGet
           |      
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        // then:
        val resultTypeMapping = mappings["/foo"]!!.getResultTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST)
        )!!

        resultTypeMapping.targetTypeName shouldBe "io.openapiprocessor.WrapAll"
        resultTypeMapping.genericTypes.shouldBeEmpty()

        val resultTypeMappingGet = mappings["/foo"]!!.getResultTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET)
        )!!

        resultTypeMappingGet.targetTypeName shouldBe "io.openapiprocessor.WrapGet"
        resultTypeMappingGet.genericTypes.shouldBeEmpty()
    }
})
