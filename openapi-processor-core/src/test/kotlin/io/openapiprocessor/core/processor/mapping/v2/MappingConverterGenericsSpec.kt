/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.ParameterTypeMapping
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader

class MappingConverterGenericsSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()
    val converter = MappingConverter()

    "read generic parameter" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  types:
                   |    - type: Foo => java.util.Map<java.lang.String, java.lang.String>
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val type = mappings.first() as TypeMapping
        type.targetTypeName shouldBe "java.util.Map"
        type.genericTypes.size shouldBe 2
        type.genericTypes[0].typeName shouldBe "java.lang.String"
        type.genericTypes[1].typeName shouldBe "java.lang.String"
    }

    "read explicit generic parameter" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  types:
                   |    - type: Foo => java.util.Map
                   |      generics:
                   |        - java.lang.String
                   |        - java.lang.String
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val type = mappings.first() as TypeMapping
        type.targetTypeName shouldBe "java.util.Map"
        type.genericTypes.size shouldBe 2
        type.genericTypes[0].typeName shouldBe "java.lang.String"
        type.genericTypes[1].typeName shouldBe "java.lang.String"
    }

    "read generic parameter with package ref" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  types:
                   |    - type: Foo => java.util.Map<{package-name}.String, {package-name}.String>
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val type = mappings.first() as TypeMapping
        type.targetTypeName shouldBe "java.util.Map"
        type.genericTypes.size shouldBe 2
        type.genericTypes[0].typeName shouldBe "generated.String"
        type.genericTypes[1].typeName shouldBe "generated.String"
    }

    "read nested generic parameters" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  types:
                   |    - type: Foo => java.util.Map<java.lang.String, java.util.Collection<java.lang.String>>
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val type = mappings.first() as TypeMapping
        type.targetTypeName shouldBe "java.util.Map"
        type.genericTypes.size shouldBe 2
        type.genericTypes[0].typeName shouldBe "java.lang.String"

        val coll = type.genericTypes[1]
        coll.typeName shouldBe "java.util.Collection"
        coll.genericTypes[0].typeName shouldBe "java.lang.String"
    }

    "read nested generic parameters with package ref" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  types:
                   |    - type: Foo => java.util.Map
                   |      generics:
                   |        - java.lang.String
                   |        - java.util.Collection<{package-name}.String>
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val type = mappings.first() as TypeMapping
        type.targetTypeName shouldBe "java.util.Map"
        type.genericTypes.size shouldBe 2
        type.genericTypes[0].typeName shouldBe "java.lang.String"

        val coll = type.genericTypes[1]
        coll.typeName shouldBe "java.util.Collection"
        coll.genericTypes[0].typeName shouldBe "generated.String"
    }

    "read parameter with generic parameters" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  parameters:
                   |    - name: foo => java.util.Map<java.lang.String, java.lang.String>
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val type = mappings.first() as ParameterTypeMapping
        type.parameterName shouldBe "foo"
        val tm = type.mapping
        tm.targetTypeName shouldBe "java.util.Map"
        tm.genericTypes.size shouldBe 2
        tm.genericTypes[0].typeName shouldBe "java.lang.String"
        tm.genericTypes[1].typeName shouldBe "java.lang.String"
    }

    "read parameter with nested generic parameters & package ref" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  parameters:
                   |    - name: foo => java.util.Map
                   |      generics:
                   |        - java.lang.String
                   |        - java.util.Collection<{package-name}.String>
                   """.trimMargin()

        // when:
        val mapping = reader.read (yaml)
        val mappings = converter.convert (mapping)

        // then:
        val type = mappings.first() as ParameterTypeMapping
        type.parameterName shouldBe "foo"
        val tm = type.mapping
        tm.targetTypeName shouldBe "java.util.Map"
        tm.genericTypes.size shouldBe 2
        tm.genericTypes[0].typeName shouldBe "java.lang.String"

        val coll = tm.genericTypes[1]
        coll.typeName shouldBe "java.util.Collection"
        coll.genericTypes[0].typeName shouldBe "generated.String"
    }
})
