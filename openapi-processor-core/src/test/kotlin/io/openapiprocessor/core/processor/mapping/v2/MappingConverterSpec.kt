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
import io.openapiprocessor.core.converter.MappingQuery
import io.openapiprocessor.core.converter.mapping.matcher.AnnotationTypeMatcher
import io.openapiprocessor.core.converter.mapping.matcher.TypeMatcher
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.query

class MappingConverterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()
    val converter = MappingConverter()


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

        val mappingData = MappingConverter(reader.read(yaml) as Mapping).convertX2()

        val epMappings = mappingData.globalMappings
        val mapping = epMappings.findTypeMapping(TypeMatcher(query(path = "/foo", name = "Foo")))

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

        val mappingData = MappingConverter(reader.read(yaml) as Mapping).convertX2()
        val gMappings = mappingData.globalMappings

        val mapping = gMappings.getNullTypeMapping()
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

        val mappingData = MappingConverter(reader.read(yaml) as Mapping).convertX2()
        val mappings = mappingData.endpointMappings["/foo"]

        val mapping = mappings?.getNullTypeMapping(query(path = "/foo", method = HttpMethod.GET, name = "Foo"))
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

        val mappingData = MappingConverter(reader.read(yaml) as Mapping).convertX2()
        val mappings = mappingData.endpointMappings["/foo"]

        val mapping = mappings?.getNullTypeMapping(query(path = "/foo", method = HttpMethod.GET, name = "Foo"))
        mapping!!.targetTypeName shouldBe "org.openapitools.jackson.nullable.JsonNullable"
        mapping.undefined shouldBe "JsonNullable.undefined()"
    }

    "read additional source type parameter annotation" {
        val yaml = """
                   |openapi-processor-mapping: v2.1
                   |
                   |options:
                   |  package-name: io.openapiprocessor.somewhere
                   | 
                   |map:
                   |  parameters:
                   |    - type: Foo @ io.openapiprocessor.Annotation
                   """.trimMargin()

        val mappingData = MappingConverter(reader.read(yaml) as Mapping).convertX2()
        val mappings = mappingData.globalMappings

        val annotations = mappings.findAnnotationParameterTypeMapping(AnnotationTypeMatcher(query(name = "Foo")))
        annotations shouldHaveSize 1

        val annotation = annotations.first()
        annotation.sourceTypeName shouldBe "Foo"
        annotation.sourceTypeFormat.shouldBeNull()
        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }

    "read additional source type parameter annotation of path" {
        val yaml = """
                   |openapi-processor-mapping: v2.1
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

        val mappingData = MappingConverter(reader.read(yaml) as Mapping).convertX2()
        val mappings = mappingData.endpointMappings

        val annotations = mappings["/foo"]!!.findAnnotationParameterTypeMapping(query(name = "Foo"))

        annotations shouldHaveSize 1

        val annotation = annotations.first()
        annotation.sourceTypeName shouldBe "Foo"
        annotation.sourceTypeFormat.shouldBeNull()
        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }

    "read additional source type parameter annotation of path with method" {
        val yaml = """
                   |openapi-processor-mapping: v2.1
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

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convertX (mapping)

        // then:
//        mappings.size.shouldBe(2)
//        val ep = mappings[1] as EndpointTypeMapping
//
//        val annotation = ep.typeMappings.first() as AnnotationTypeMapping
//        annotation.sourceTypeName shouldBe "Foo"
//        annotation.sourceTypeFormat.shouldBeNull()
//        annotation.annotation.type shouldBe "io.openapiprocessor.Annotation"
    }

    "does not fail on 'empty' options: key" {
        val yaml = """
           |openapi-processor-mapping: v8
           |options:
           |
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping

        val mappings = shouldNotThrow<Exception> {
            MappingConverter(mapping).convertX()
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
            MappingConverter(mapping).convertX()
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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val excluded = mappings.isEndpointExcluded(MappingQuery(path = "/foo", method = HttpMethod.POST))
        excluded.shouldBeFalse()

        val excludedGet = mappings.isEndpointExcluded(MappingQuery(path = "/foo", method = HttpMethod.GET))
        excludedGet.shouldBeTrue()
    }
})
