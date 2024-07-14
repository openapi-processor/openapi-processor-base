/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.NullSchema.Companion.nullSchema
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

class MappingFinderSpec: StringSpec({
    val resolver = mockk<RefResolver>()
    val any = SchemaInfo.Endpoint("/any", HttpMethod.GET)
    val foo = SchemaInfo.Endpoint("/foo", HttpMethod.GET)

    "no type mapping in empty mappings" {
        val finder = MappingFinder(emptyList())

        val info = SchemaInfo(any, "Any", "", nullSchema, resolver)
        val result = finder.findTypeMapping(info)

        result.shouldBeNull()
    }

    "type mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                TypeMapping("Foo", null, "io.openapiprocessor.Foo"),
                TypeMapping("Far", null, "io.openapiprocessor.Far"),
                TypeMapping("Bar", null, "io.openapiprocessor.Bar")
            )
        )

        val info = SchemaInfo(any, "Foo", "", nullSchema, resolver)
        val result = finder.findTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate type mapping" {
        val finder = MappingFinder(
            listOf(
                TypeMapping("Foo", null, "io.openapiprocessor.Foo"),
                TypeMapping("Foo", null, "io.openapiprocessor.Foo")
            )
        )

        val info = SchemaInfo(any, "Foo", "", nullSchema, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findTypeMapping(info)
        }
    }

    "no io mapping in empty mappings" {
        val finder = MappingFinder(emptyList())

        val param = SchemaInfo(any, "parameter", "", nullSchema, resolver)
        val paramResult = finder.findIoTypeMapping(param)
        paramResult.shouldBeNull()

        val response = SchemaInfo(any, "", "application/json", nullSchema, resolver)
        val responseResult = finder.findIoTypeMapping(response)
        responseResult.shouldBeNull()
    }

    "io parameter mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                NameTypeMapping("foo param",
                    TypeMapping("Foo", null, "io.openapiprocessor.Foo")),
                NameTypeMapping("far param",
                    TypeMapping("far", null, "io.openapiprocessor.Far")),
                NameTypeMapping("bar param",
                    TypeMapping("Bar", null, "io.openapiprocessor.Bar"))
            )
        )

        val info = SchemaInfo(any, "far param", "", nullSchema, resolver)
        val result = finder.findIoTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("far")
        result.targetTypeName.shouldBe("io.openapiprocessor.Far")
    }

    "io response mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                ContentTypeMapping("application/json",
                    TypeMapping("Foo", null, "io.openapiprocessor.Foo")),
                ContentTypeMapping("application/json-2",
                    TypeMapping("Far", null, "io.openapiprocessor.Far")),
                ContentTypeMapping("application/json-3",
                    TypeMapping("Bar", null, "io.openapiprocessor.Bar"))
            )
        )

        val info = SchemaInfo(any, "", "application/json", nullSchema, resolver)
        val result = finder.findIoTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate parameter mapping" {
        val finder = MappingFinder(
            listOf(
                NameTypeMapping("foo param",
                    TypeMapping("Foo A", null, "io.openapiprocessor.Foo A")),
                NameTypeMapping("foo param",
                    TypeMapping("Foo B", null, "io.openapiprocessor.Foo B"))
            )
        )

        val info = SchemaInfo(any, "foo param", "", nullSchema, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findIoTypeMapping(info)
        }
    }

    "throws on duplicate response mapping" {
        val finder = MappingFinder(
            listOf(
                ContentTypeMapping("application/json",
                    TypeMapping("Foo", null, "io.openapiprocessor.Foo")),
                ContentTypeMapping("application/json",
                    TypeMapping("far", null, "io.openapiprocessor.Far"))
            )
        )

        val info = SchemaInfo(any, "", "application/json", nullSchema, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findIoTypeMapping(info)
        }
    }

    "no endpoint type mapping in empty mappings" {
        val finder = MappingFinder(emptyList())

        val info = SchemaInfo(foo, "Foo", "", nullSchema, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldBeNull()
    }

    "endpoint parameter mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, listOf(
                    NameTypeMapping("foo param",
                        TypeMapping("Foo", null, "io.openapiprocessor.Foo")),
                    NameTypeMapping("far param",
                        TypeMapping("far", null, "io.openapiprocessor.Far")),
                    NameTypeMapping("bar param",
                        TypeMapping("Bar", null, "io.openapiprocessor.Bar"))
            )))
        )

        val info = SchemaInfo(foo, "far param", "", nullSchema, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("far")
        result.targetTypeName.shouldBe("io.openapiprocessor.Far")
    }

    "endpoint response mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, listOf(
                    ContentTypeMapping("application/json",
                        TypeMapping("Foo", null, "io.openapiprocessor.Foo")),
                    ContentTypeMapping("application/json-2",
                        TypeMapping("far", null, "io.openapiprocessor.Far")),
                    ContentTypeMapping("application/json-3",
                        TypeMapping("Bar", null, "io.openapiprocessor.Bar"))
            )))
        )

        val info = SchemaInfo(foo, "", "application/json", nullSchema, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate endpoint parameter mapping" {
        val finder = MappingFinder(listOf(
            EndpointTypeMapping("/foo", null, listOf(
                    NameTypeMapping("foo param",
                        TypeMapping("Foo A", null, "io.openapiprocessor.Foo A")),
                    NameTypeMapping("foo param",
                        TypeMapping("Foo B", null, "io.openapiprocessor.Foo B"))
                )))
        )

        val info = SchemaInfo(foo, "foo param", "", nullSchema, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findEndpointTypeMapping(info)
        }
    }

    "throws on duplicate endpoint response mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, listOf(
                    ContentTypeMapping("application/json",
                        TypeMapping("Foo", null, "io.openapiprocessor.Foo")),
                    ContentTypeMapping("application/json",
                        TypeMapping("far", null, "io.openapiprocessor.Far"))
            )))
        )

        val info = SchemaInfo(foo, "", "application/json", nullSchema, resolver)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findEndpointTypeMapping(info)
        }
    }

    "endpoint type mapping matches single mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, listOf(
                    TypeMapping("Foo", null, "io.openapiprocessor.Foo"),
                    TypeMapping("Far", null, "io.openapiprocessor.Far"),
                    TypeMapping("Bar", null, "io.openapiprocessor.Bar")
            )))
        )

        val info = SchemaInfo(foo, "Foo", "", nullSchema, resolver)
        val result = finder.findEndpointTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "no endpoint null mapping in empty mappings" {
        val finder = MappingFinder(emptyList())

        val info = SchemaInfo(foo, "", "", nullSchema, resolver)
        val result = finder.findEndpointNullTypeMapping(info)

        result.shouldBeNull()
    }

    "endpoint type mapping matches null mapping" {
        val finder = MappingFinder(
            listOf(
                EndpointTypeMapping("/foo", null, listOf(
                    NullTypeMapping("null", "org.openapitools.jackson.nullable.JsonNullable"),
                    TypeMapping("Far", null, "io.openapiprocessor.Far"),
                    TypeMapping("Bar", null, "io.openapiprocessor.Bar")
            )))
        )

        val info = SchemaInfo(foo, "Foo", "", nullSchema, resolver)
        val result = finder.findEndpointNullTypeMapping(info)

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("null")
        result.targetTypeName.shouldBe("org.openapitools.jackson.nullable.JsonNullable")
    }

    "find unset result style option mapping" {
        val finder = MappingFinder(listOf())

        val result = finder.findResultStyleMapping()

        result.shouldBe(null)
    }

    "find result style option mapping" {
        ResultStyle.entries.toTypedArray().forAll { style ->
            val finder = MappingFinder(listOf(ResultStyleOptionMapping(style)))

            val result = finder.findResultStyleMapping()

            result.shouldBe(style)
        }
    }

})
