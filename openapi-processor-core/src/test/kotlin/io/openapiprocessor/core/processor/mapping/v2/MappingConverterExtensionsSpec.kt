/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.AnnotationNameMapping
import io.openapiprocessor.core.converter.mapping.ExtensionMapping
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

        val mappings = converter.convert(reader.read(yaml))

        mappings shouldHaveSize 1
        val xFoo = mappings.first() as ExtensionMapping
        xFoo.extension shouldBe "x-foo"

        xFoo.mappings shouldHaveSize 2
        val xFoo1 = xFoo.mappings[0] as AnnotationNameMapping
        xFoo1.name shouldBe "foo"
        xFoo1.annotation.type shouldBe "annotation.Foo"
        val xFoo2 = xFoo.mappings[1] as AnnotationNameMapping
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
        val mappings = MappingConverter(mapping).convertX()

        val annotationsFoo = mappings.findExtensionAnnotations("x-foo", "foo")
        annotationsFoo shouldHaveSize 1
        annotationsFoo[0].name shouldBe "foo"
        annotationsFoo[0].annotation.type shouldBe "annotation.Foo"

        val annotationsBar = mappings.findExtensionAnnotations("x-foo", "bar")
        annotationsBar shouldHaveSize 1
        annotationsBar[0].name shouldBe "bar"
        annotationsBar[0].annotation.type shouldBe "annotation.Bar"
    }
})
