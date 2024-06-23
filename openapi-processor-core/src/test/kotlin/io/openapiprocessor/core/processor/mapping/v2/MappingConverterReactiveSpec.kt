/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterReactiveSpec: StringSpec({
    val reader = MappingReader()

    "reads global singe & multi type mapping" {
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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val singleTypeMapping = mappings.getGlobalSingleTypeMapping()!!
        singleTypeMapping.sourceTypeName shouldBe "single"
        singleTypeMapping.targetTypeName shouldBe "reactor.core.publisher.Mono"

        val multiTypeMapping = mappings.getGlobalMultiTypeMapping()!!
        multiTypeMapping.sourceTypeName shouldBe "multi"
        multiTypeMapping.targetTypeName shouldBe "reactor.core.publisher.Flux"
    }
})
