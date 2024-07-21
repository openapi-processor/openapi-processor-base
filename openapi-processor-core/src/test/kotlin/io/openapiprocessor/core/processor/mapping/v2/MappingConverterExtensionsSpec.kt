/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.processor.BadMappingException
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterExtensionsSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()
    val converter = MappingConverter()

    "read extension mappings" {
        val yaml = """
           |openapi-processor-mapping: v6
           |options:
           |  package-name: some.package
           |
           |map:
           |  extensions:
           |    x-foo:
           |      - foo @ annotation.Foo
           |      - bar @ annotation.Bar
           """.trimMargin()

        val mappingData = converter.convert(reader.read(yaml))

        mappingData.extensionMappings.shouldHaveSize(1)
        val mappings = mappingData.extensionMappings["x-foo"]!!

        val foo = mappings.get("foo")
        val xFoo1 = foo[0]
        xFoo1.name shouldBe "foo"
        xFoo1.annotation.type shouldBe "annotation.Foo"

        val bar = mappings.get("bar")
        val xFoo2 = bar[0]
        xFoo2.name shouldBe "bar"
        xFoo2.annotation.type shouldBe "annotation.Bar"
    }

    "read extension mappings throws if no annotation mapping" {
        val yaml = """
           |openapi-processor-mapping: v6
           |options:
           |  package-name: some.package
           |
           |map:
           |  extensions:
           |    x-foo:
           |      - foo => annotation.Foo
           """.trimMargin()

        shouldThrow<BadMappingException> {
            converter.convert(reader.read(yaml))
        }
    }

    "read extension annotation mappings" {
        val yaml = """
           |openapi-processor-mapping: v8
           |options:
           |  package-name: some.package
           |
           |map:
           |  extensions:
           |    x-foo:
           |      - foo @ annotation.Foo
           |      - bar @ annotation.Bar
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappingData = MappingConverter(mapping).convertX2()

        mappingData.extensionMappings.shouldHaveSize(1)
        val mappings = mappingData.extensionMappings["x-foo"]!!

        val foo = mappings.get("foo")
        val xFoo1 = foo[0]
        xFoo1.name shouldBe "foo"
        xFoo1.annotation.type shouldBe "annotation.Foo"

        val bar = mappings.get("bar")
        val xFoo2 = bar[0]
        xFoo2.name shouldBe "bar"
        xFoo2.annotation.type shouldBe "annotation.Bar"
    }
})
