/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.converter.mapping.matcher.InterfaceTypeMatcher
import io.openapiprocessor.core.converter.mapping.steps.TypesStep
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.MappingConstants.VERSION

class MappingConverterInterfacesSpec: StringSpec({
    val reader = MappingReader()

    "read global interface mappings" {
        val yaml = """
           |openapi-processor-mapping: $VERSION
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  types:
           |   - type: object =+ java.io.Serializable
           |   - type: Foo =+ some.other.Interface<java.lang.String>
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        val query = MappingFinderQuery(path = "/foo", name = "Foo", allowObject = true)
        val typeMappings = mappings.findInterfaceTypeMappings(InterfaceTypeMatcher(query), TypesStep())

        typeMappings shouldHaveSize 2
        typeMappings[0].sourceTypeName shouldBe "object"
        typeMappings[0].targetTypeName shouldBe "java.io.Serializable"
        typeMappings[1].sourceTypeName shouldBe "Foo"
        typeMappings[1].targetTypeName shouldBe "some.other.Interface"
        typeMappings[1].genericTypes[0].typeName shouldBe "java.lang.String"
    }

    "read endpoint interface mappings" {
        val yaml = """
           |openapi-processor-mapping: $VERSION
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      types:
           |        - type: Foo =+ java.io.Serializable
           |        - type: Foo =+ some.other.Interface<java.lang.String>
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        val query = MappingFinderQuery(path = "/foo", name = "Foo")
        val typeMappings = mappings["/foo"]!!.findInterfaceTypeMappings(query, TypesStep())

        typeMappings shouldHaveSize 2
        typeMappings[0].sourceTypeName shouldBe "Foo"
        typeMappings[0].targetTypeName shouldBe "java.io.Serializable"
        typeMappings[1].sourceTypeName shouldBe "Foo"
        typeMappings[1].targetTypeName shouldBe "some.other.Interface"
        typeMappings[1].genericTypes[0].typeName shouldBe "java.lang.String"
    }

    "read endpoint/method interface mappings" {
        val yaml = """
           |openapi-processor-mapping: $VERSION
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      post:
           |        types:
           |          - type: Foo =+ java.io.Serializable
           |          - type: Foo =+ some.other.Interface<java.lang.String>
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        val query = MappingFinderQuery(path = "/foo", method = HttpMethod.POST, name = "Foo")
        val typeMappings = mappings["/foo"]!!.findInterfaceTypeMappings(query, TypesStep())

        typeMappings shouldHaveSize 2
        typeMappings[0].sourceTypeName shouldBe "Foo"
        typeMappings[0].targetTypeName shouldBe "java.io.Serializable"
        typeMappings[1].sourceTypeName shouldBe "Foo"
        typeMappings[1].targetTypeName shouldBe "some.other.Interface"
        typeMappings[1].genericTypes[0].typeName shouldBe "java.lang.String"
    }
})
