/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.AnnotationNameMapping
import io.openapiprocessor.core.converter.mapping.NameTypeMapping
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterParameterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()
    val converter = MappingConverter()

    "read global parameter name type mapping" {
        val yaml = """
           |openapi-processor-mapping: v5
           |map:
           |  parameters:
           |    - name: foo => mapping.Foo
           """.trimMargin()

        val mappings = converter.convert(reader.read(yaml))

        // then:
        val parameter = mappings.first() as NameTypeMapping
        parameter.parameterName shouldBe "foo"
        parameter.mapping.sourceTypeName.shouldBeNull()
        parameter.mapping.sourceTypeFormat.shouldBeNull()
        parameter.mapping.targetTypeName shouldBe "mapping.Foo"
        parameter.mapping.genericTypes.shouldBeEmpty()
    }

    "read global parameter name annotation mapping" {
        val yaml = """
           |openapi-processor-mapping: v5
           |map:
           |  parameters:
           |    - name: foo @ annotation.Foo
           """.trimMargin()

        val mappings = converter.convert(reader.read(yaml))

        mappings.size.shouldBe(1)
        val annotation = mappings.first() as AnnotationNameMapping
        annotation.name shouldBe "foo"
        annotation.annotation.type shouldBe "annotation.Foo"
    }
})
