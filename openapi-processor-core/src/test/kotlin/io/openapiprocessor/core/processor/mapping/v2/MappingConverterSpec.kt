/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.converter.mapping.steps.ParametersStep
import io.openapiprocessor.core.converter.mapping.steps.TypesStep
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.annotationTypeMatcher
import io.openapiprocessor.core.support.typeMatcher
import org.slf4j.Logger

class MappingConverterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()
    reader.log = mockk<Logger>(relaxed = true)

    "read global type mapping with generic parameter using the generated package ref" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  types:
           |    - type: Foo => io.openapiprocessor.Foo<{package-name}.Bar>
           """.trimMargin()

        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().globalMappings

        val mapping = mappings.findTypeMapping(typeMatcher(path = "/foo", name = "Foo"), TypesStep())
        mapping!!.sourceTypeName shouldBe "Foo"
        mapping.sourceTypeFormat.shouldBeNull()
        mapping.targetTypeName shouldBe "io.openapiprocessor.Foo"
        mapping.genericTypes.size shouldBe 1
        mapping.genericTypes.first().typeName shouldBe "io.openapiprocessor.somewhere.Bar"
    }

    // no global null mapping
    "read global 'null' mapping" {
        val yaml = """
                   |openapi-processor-mapping: v8
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  null: org.openapitools.jackson.nullable.JsonNullable
                   """.trimMargin()

        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().globalMappings

        val mapping = mappings.getNullTypeMapping()
        mapping.shouldBeNull()
        //mapping!!.targetTypeName shouldBe "org.openapitools.jackson.nullable.JsonNullable"
    }

    "read endpoint 'null' mapping" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |      null: org.openapitools.jackson.nullable.JsonNullable
                   """.trimMargin()

        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().endpointMappings

        val mapping = mappings["/foo"]?.getNullTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET, name = "Foo"))

        mapping!!.targetTypeName shouldBe "org.openapitools.jackson.nullable.JsonNullable"
    }

    "read endpoint 'null' mapping with init value" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |      null: org.openapitools.jackson.nullable.JsonNullable = JsonNullable.undefined()
                   """.trimMargin()

        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().endpointMappings["/foo"]

        val mapping = mappings?.getNullTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET, name = "Foo"))

        mapping!!.targetTypeName shouldBe "org.openapitools.jackson.nullable.JsonNullable"
        mapping.undefined shouldBe "JsonNullable.undefined()"
    }

    "read additional source type parameter annotation" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  parameters:
                   |    - type: Foo @ io.openapiprocessor.Annotation
                   """.trimMargin()

        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().globalMappings

        val annotations = mappings.findAnnotationParameterTypeMapping(
            annotationTypeMatcher(name = "Foo"), ParametersStep())

        annotations shouldHaveSize 1
        val annotation = annotations.first()
        annotation.sourceTypeName shouldBe "Foo"
        annotation.sourceTypeFormat.shouldBeNull()
        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }

    "read additional source type parameter annotation of path" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |      parameters:
                   |        - type: Foo @ io.openapiprocessor.Annotation
                   """.trimMargin()

        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().endpointMappings
        val annotations = mappings["/foo"]!!.findAnnotationParameterTypeMapping(
            MappingFinderQuery(name = "Foo"), ParametersStep())

        annotations shouldHaveSize 1

        val annotation = annotations.first()
        annotation.sourceTypeName shouldBe "Foo"
        annotation.sourceTypeFormat.shouldBeNull()
        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }

    "read additional source type parameter annotation of path with method" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  paths:
                   |    /foo:
                   |        get:
                   |          parameters:
                   |           - type: Foo @ io.openapiprocessor.Annotation
                   """.trimMargin()

        val mappings = MappingConverter(reader.read(yaml) as Mapping).convert().endpointMappings
        val annotations = mappings["/foo"]!!.findAnnotationParameterTypeMapping(
            MappingFinderQuery(name = "Foo", method = HttpMethod.GET), ParametersStep())

        annotations shouldHaveSize 1

        val annotation = annotations.first()
        annotation.sourceTypeName shouldBe "Foo"
        annotation.sourceTypeFormat.shouldBeNull()
        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }

    "does not fail on 'empty' options: key" {
        val yaml = """
           |openapi-processor-mapping: v8
           |options:
           |
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping

        val mappings = shouldNotThrow<Exception> {
            MappingConverter(mapping).convert()
        }

        mappings.shouldNotBeNull()
    }

    "does not fail on 'empty' mapping.yaml" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping

        val mappings = shouldNotThrow<Exception> {
            MappingConverter(mapping).convert()
        }

        mappings.shouldNotBeNull()
    }

    "read endpoint path exclude" {
        val yaml =
            """
            |openapi-processor-mapping: v8
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
            | 
            |map:
            |  paths:
            |    /foo:
            |      get:
            |        exclude: true
            """.trimMargin()

        // when:
        val mapping = reader.read(yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        // then:
        val excluded = mappings["/foo"]!!.isExcluded(MappingFinderQuery(path = "/foo", method = HttpMethod.POST))
        excluded.shouldBeFalse()

        val excludedGet = mappings["/foo"]!!.isExcluded(MappingFinderQuery(path = "/foo", method = HttpMethod.GET))
        excludedGet.shouldBeTrue()
    }
})
