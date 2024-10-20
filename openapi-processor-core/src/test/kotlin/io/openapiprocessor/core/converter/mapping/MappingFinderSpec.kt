/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MappingFinder
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.processor.mapping.v2.Mapping
import io.openapiprocessor.core.processor.mapping.v2.MappingConverter
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import io.openapiprocessor.core.support.mappingFinder
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.support.query

class MappingFinderSpec: StringSpec({

    "no type mapping in empty mappings" {
        val finder = mappingFinder()
        val result = finder.findTypeMapping(query(path = "/any", name = "any", type = "any"))

        result.shouldBeNull()
    }

    "type mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo => io.openapiprocessor.Foo
            |    - type: Far => io.openapiprocessor.Far
            |    - type: Bar => io.openapiprocessor.Bar
            """)

        val finder = mappingFinder(options)
        val result = finder.findTypeMapping(query(path = "/any", name = "Foo", type = "object"))

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate type mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  types:
            |    - type: Foo => io.openapiprocessor.Foo
            |    - type: Foo => io.openapiprocessor.Foo
            """)

        shouldThrow<AmbiguousTypeMappingException> {
            mappingFinder(options).findTypeMapping(query(name = "Foo"))
        }
    }

    "no io mapping in empty mappings" {
        val finder = mappingFinder()

        val paramResult = finder.findParameterTypeMapping(query(name = "parameter"))
        paramResult.shouldBeNull()

        val responseResult = finder.findContentTypeMapping(query(contentType = "application/json"))
        responseResult.shouldBeNull()
    }

    "parameter mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  parameters:
            |    - name: fooParam => io.openapiprocessor.Foo
            |    - name: farParam => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)
        val result = finder.findParameterNameTypeMapping(query(name = "farParam"))

        result.shouldNotBeNull()
        result.mapping.sourceTypeName.shouldBeNull()
        result.mapping.targetTypeName.shouldBe("io.openapiprocessor.Far")
    }

    "response mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  responses:
            |    - content: application/json => io.openapiprocessor.Foo
            |    - content: application/json-2 => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)
        val result = finder.findContentTypeMapping(query(contentType = "application/json"))

        result.shouldNotBeNull()
        result.mapping.sourceTypeName.shouldBeNull()
        result.mapping.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate parameter mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  parameters:
            |    - name: fooParam => io.openapiprocessor.Foo
            |    - name: fooParam => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findParameterNameTypeMapping(query(name = "fooParam"))
        }
    }

    "throws on duplicate response mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  responses:
            |    - content: application/json => io.openapiprocessor.Foo
            |    - content: application/json => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findContentTypeMapping(query(contentType = "application/json"))
        }
    }

    "no endpoint type mapping in empty mappings" {
        val finder = mappingFinder()

        val result = finder.findTypeMapping(query(name = "Foo"))

        result.shouldBeNull()
    }

    "endpoint parameter mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /foo:
            |      parameters:
            |        - name: fooParam => io.openapiprocessor.Foo
            |        - name: farParam => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)
        val result = finder.findParameterNameTypeMapping(query(path = "/foo", name = "farParam"))

        result.shouldNotBeNull()
        result.mapping.sourceTypeName.shouldBeNull()
        result.mapping.targetTypeName.shouldBe("io.openapiprocessor.Far")
    }

    "endpoint response mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /foo:
            |      responses:
            |        - content: application/json => io.openapiprocessor.Foo
            |        - content: application/json-2 => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)
        val result = finder.findContentTypeMapping(query(path = "/foo", contentType = "application/json"))

        result.shouldNotBeNull()
        result.mapping.sourceTypeName.shouldBeNull()
        result.mapping.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "throws on duplicate endpoint parameter mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /foo:
            |      parameters:
            |        - name: fooParam => io.openapiprocessor.Foo
            |        - name: fooParam => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findParameterNameTypeMapping(query(path = "/foo", name = "fooParam"))
        }
    }

    "throws on duplicate endpoint response mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /foo:
            |      responses:
            |        - content: application/json => io.openapiprocessor.Foo
            |        - content: application/json => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)

        shouldThrow<AmbiguousTypeMappingException> {
            finder.findContentTypeMapping(query(path = "/foo", contentType = "application/json"))
        }
    }

    "endpoint type mapping matches single mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /foo:
            |      types:
            |        - type: Foo => io.openapiprocessor.Foo
            |        - type: Far => io.openapiprocessor.Far
            """)

        val finder = mappingFinder(options)
        val result = finder.findTypeMapping(query(path = "/foo", name = "Foo"))

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("Foo")
        result.targetTypeName.shouldBe("io.openapiprocessor.Foo")
    }

    "no endpoint null mapping in empty mappings" {
        val finder = mappingFinder()

        val result = finder.findNullTypeMapping(query())

        result.shouldBeNull()
    }

    "endpoint type mapping matches null mapping" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /foo:
            |      null: org.openapitools.jackson.nullable.JsonNullable
            """)

        val finder = mappingFinder(options)
        val result = finder.findNullTypeMapping(query(path = "/foo", name = "Foo"))

        result.shouldNotBeNull()
        result.sourceTypeName.shouldBe("null")
        result.targetTypeName.shouldBe("org.openapitools.jackson.nullable.JsonNullable")
    }

    "find unset result style option mapping" {
        val finder = mappingFinder()

        val result = finder.getResultTypeMapping(query())

        result.shouldBe(null)
    }

    "find result style option mapping" {
        ResultStyle.entries.toTypedArray().forAll { style ->
            val options = parseOptions(mapping =
                """
                |map:
                |  result-style: ${style.toString().lowercase()}
                """)

            val finder = mappingFinder(options)

            val result = finder.findResultStyleMapping(query())

            result.shouldBe(style)
        }
    }

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
        val reader = MappingReader()
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert()
        val finder = MappingFinder(ApiOptions().apply {
            globalMappings = mappings.globalMappings
            endpointMappings = mappings.endpointMappings
            extensionMappings = mappings.extensionMappings
        })

        // then:
        val resultTypeMapping = finder.getResultTypeMapping(
            MappingFinderQuery(path = "/bar", method = HttpMethod.GET)
        )!!

        resultTypeMapping.targetTypeName shouldBe "io.openapiprocessor.WrapGlobal"
        resultTypeMapping.genericTypes.shouldBeEmpty()

        val resultTypeMappingPath = finder.getResultTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST)
        )!!

        resultTypeMappingPath.targetTypeName shouldBe "io.openapiprocessor.WrapPath"
        resultTypeMappingPath.genericTypes.shouldBeEmpty()

        val resultTypeMappingGet = finder.getResultTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET)
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
        val reader = MappingReader()
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert()
        val finder = MappingFinder(ApiOptions().apply {
            globalMappings = mappings.globalMappings
            endpointMappings = mappings.endpointMappings
            extensionMappings = mappings.extensionMappings
        })

        // then:
        val singleTypeMappingG = finder.getSingleTypeMapping(
            MappingFinderQuery(path = "/bar", method = HttpMethod.GET)
        )!!

        singleTypeMappingG.sourceTypeName shouldBe "single"
        singleTypeMappingG.targetTypeName shouldBe "reactor.core.publisher.MonoGlobal"

        val multiTypeMappingG = finder.getMultiTypeMapping(
            MappingFinderQuery(path = "/bar", method = HttpMethod.GET)
        )!!

        multiTypeMappingG.sourceTypeName shouldBe "multi"
        multiTypeMappingG.targetTypeName shouldBe "reactor.core.publisher.FluxGlobal"

        val singleTypeMappingP = finder.getSingleTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST)
        )!!

        singleTypeMappingP.sourceTypeName shouldBe "single"
        singleTypeMappingP.targetTypeName shouldBe "reactor.core.publisher.MonoPath"

        val multiTypeMappingP = finder.getMultiTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST)
        )!!

        multiTypeMappingP.sourceTypeName shouldBe "multi"
        multiTypeMappingP.targetTypeName shouldBe "reactor.core.publisher.FluxPath"

        val singleTypeMappingGet = finder.getSingleTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET)
        )!!

        singleTypeMappingGet.sourceTypeName shouldBe "single"
        singleTypeMappingGet.targetTypeName shouldBe "reactor.core.publisher.MonoMethod"

        val multiTypeMappingGet = finder.getMultiTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET)
        )!!

        multiTypeMappingGet.sourceTypeName shouldBe "multi"
        multiTypeMappingGet.targetTypeName shouldBe "reactor.core.publisher.FluxMethod"
    }
})
