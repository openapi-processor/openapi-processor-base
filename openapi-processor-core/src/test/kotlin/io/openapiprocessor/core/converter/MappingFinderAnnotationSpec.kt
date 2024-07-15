/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.support.parseOptions

class MappingFinderAnnotationSpec: StringSpec({

    "find global annotation type mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo @ annotation.Bar
            """)

        val finder = MappingFinder(options)

        val mapping = finder.findAnnotationTypeMappings(MappingQueryX(type = "Foo"))

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "Foo"
    }

    "find 'object' annotation type mapping for model data type" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: object @ annotation.Bar
            """)

        val finder = MappingFinder(options)

        val mapping = finder.findAnnotationTypeMappings(MappingQueryX(type = "Foo", allowObject = true))

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "object"
    }

    "ignore 'object' annotation type mapping for non model data types" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: object @ annotation.Bar
            """)

        val finder = MappingFinder(options)

        val mappingSimple = finder.findAnnotationTypeMappings(MappingQueryX(type = "Foo"))
        mappingSimple.shouldBeEmpty()

        val mappingCollection = finder.findAnnotationTypeMappings(MappingQueryX(type = "Foo[]"))
        mappingCollection.shouldBeEmpty()
    }

    "find type:format annotation type mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: string:uuid @ annotation.Foo
            """)

        val finder = MappingFinder(options)

        val mapping = finder.findAnnotationTypeMappings(MappingQueryX(type = "string", format = "uuid"))

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "string"
        mapping.first().sourceTypeFormat shouldBe "uuid"
    }

    "find parameter annotation type mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo @ annotation.Bar
            """)

        val finder = MappingFinder(options)

        val mapping = finder.findAnnotationParameterTypeMappings(MappingQueryX(type = "Foo"))

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "Foo"
    }

    "find parameter type:format annotation type mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: string:uuid @ annotation.Foo
            """)

        val finder = MappingFinder(options)

        val mapping = finder.findAnnotationParameterTypeMappings(MappingQueryX(type = "string", format = "uuid"))

        mapping.size shouldBe 1
        mapping.first().sourceTypeName shouldBe "string"
        mapping.first().sourceTypeFormat shouldBe "uuid"
    }

    // find parameter, find parameter endpoint

    "find parameter annotation name mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  parameters:
            |    - name: foo @ annotation.Bar
            """)

        val finder = MappingFinder(options)

        val mapping = finder.findAnnotationParameterNameTypeMapping(MappingQueryX(name = "foo"))

        mapping.size shouldBe 1
        mapping.first().name shouldBe "foo"
    }

    "find extension name/value annotation mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  extensions:
            |    x-foo: foo @ annotation.Foo
            """)

        val finder = MappingFinder(options)

        val mapping = finder.findExtensionAnnotations("x-foo", "foo")

        mapping.size shouldBe 1
        mapping.first().name shouldBe "foo"
    }

    "find extension name/value annotation mappings" {
        val options = parseOptions(mapping =
            """
            |map:
            |  extensions:
            |    x-foo:
            |      - fooA @ annotation.FooA
            |      - fooB @ annotation.FooB
            """)

        val finder = MappingFinder(options)

        val mapping = finder.findExtensionAnnotations("x-foo", "fooA", "fooB")

        mapping.size shouldBe 2
        mapping[0].name shouldBe "fooA"
        mapping[1].name shouldBe "fooB"
    }
})
