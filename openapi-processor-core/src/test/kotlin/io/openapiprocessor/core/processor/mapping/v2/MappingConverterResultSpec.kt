/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterResultSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()

//    single: reactor.core.publisher.Mono
//    multi: reactor.core.publisher.Flux

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val resultTypeMapping = mappings.getGlobalResultTypeMapping()!!

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val resultTypeMapping = mappings.getGlobalResultTypeMapping()!!

        resultTypeMapping.targetTypeName shouldBe "io.openapiprocessor.Wrap"
        resultTypeMapping.genericTypes.shouldBeEmpty()
    }
})
