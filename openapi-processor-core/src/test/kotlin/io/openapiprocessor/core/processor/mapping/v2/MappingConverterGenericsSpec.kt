/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.matcher.AddParameterTypeMatcher
import io.openapiprocessor.core.converter.mapping.matcher.ContentTypeMatcher
import io.openapiprocessor.core.converter.mapping.matcher.ParameterNameTypeMatcher
import io.openapiprocessor.core.converter.mapping.matcher.TypeMatcher
import io.openapiprocessor.core.converter.mapping.steps.ContentTypesStep
import io.openapiprocessor.core.converter.mapping.steps.ParametersStep
import io.openapiprocessor.core.converter.mapping.steps.TypesStep
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.query

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

        val mappingData = converter.convert(reader.read(yaml))
        val type = mappingData.globalMappings.findTypeMapping(TypeMatcher(query(name = "Foo")), TypesStep())!!

        type.sourceTypeName shouldBe "Foo"
        type.sourceTypeFormat.shouldBeNull()
        type.targetTypeName shouldBe "java.util.Map"
        type.genericTypes shouldHaveSize 2
        type.genericTypes[0].typeName shouldBe "java.lang.String"
        type.genericTypes[1].typeName shouldBe "java.lang.String"
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

        val mappingData = converter.convert(reader.read(yaml))
        val type = mappingData.globalMappings.findTypeMapping(TypeMatcher(query(name = "Foo")), TypesStep())!!

        type.sourceTypeName shouldBe "Foo"
        type.sourceTypeFormat.shouldBeNull()
        type.targetTypeName shouldBe "java.util.Map"
        type.genericTypes shouldHaveSize 2
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

        val mappingData = converter.convert(reader.read(yaml))
        val type = mappingData.globalMappings.findTypeMapping(TypeMatcher(query(name = "Foo")), TypesStep())!!

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

        val mappingData = converter.convert(reader.read(yaml))
        val type = mappingData.globalMappings.findTypeMapping(TypeMatcher(query(name = "Foo")), TypesStep())!!

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

        val mappingData = converter.convert(reader.read(yaml))
        val type = mappingData.globalMappings.findTypeMapping(TypeMatcher(query(name = "Foo")), TypesStep())!!

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

        val mappingData = converter.convert(reader.read(yaml))
        val type = mappingData.globalMappings.findParameterNameTypeMapping(
            ParameterNameTypeMatcher(query(name = "foo")), ParametersStep())!!

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

        val mappingData = converter.convert(reader.read(yaml))
        val type = mappingData.globalMappings.findParameterNameTypeMapping(
            ParameterNameTypeMatcher(query(name = "foo")), ParametersStep())!!

        type.parameterName shouldBe "foo"
        val tm = type.mapping
        tm.targetTypeName shouldBe "java.util.Map"
        tm.genericTypes.size shouldBe 2
        tm.genericTypes[0].typeName shouldBe "java.lang.String"

        val coll = tm.genericTypes[1]
        coll.typeName shouldBe "java.util.Collection"
        coll.genericTypes[0].typeName shouldBe "generated.String"
    }

    "read additional parameter with nested generic parameters & package ref" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  parameters:
                   |    - add: foo => java.util.Map
                   |      generics:
                   |        - java.lang.String
                   |        - java.util.Collection<{package-name}.String>
                   """.trimMargin()

        val mappingData = converter.convert(reader.read(yaml))
        val adds = mappingData.globalMappings.findAddParameterTypeMappings(AddParameterTypeMatcher(), ParametersStep())

        adds shouldHaveSize 1
        val type = adds.first()

        type.parameterName shouldBe "foo"
        val tm = type.mapping
        tm.targetTypeName shouldBe "java.util.Map"
        tm.genericTypes.size shouldBe 2
        tm.genericTypes[0].typeName shouldBe "java.lang.String"

        val coll = tm.genericTypes[1]
        coll.typeName shouldBe "java.util.Collection"
        coll.genericTypes[0].typeName shouldBe "generated.String"
    }

    "read response with nested generic parameters & package ref" {
        val yaml = """
                   |openapi-processor-mapping: v2
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  responses:
                   |    - content: foo/bar => java.util.Map
                   |      generics:
                   |        - java.lang.String
                   |        - java.util.Collection<{package-name}.String>
                   """.trimMargin()

        val mappingData = converter.convert(reader.read(yaml))
        val type = mappingData.globalMappings.findContentTypeMapping(
            ContentTypeMatcher(query(contentType = "foo/bar")), ContentTypesStep())!!

        type.contentType shouldBe "foo/bar"
        val tm = type.mapping
        tm.sourceTypeName.shouldBeNull()
        tm.sourceTypeFormat.shouldBeNull()
        tm.targetTypeName shouldBe "java.util.Map"
        tm.genericTypes.size shouldBe 2
        tm.genericTypes[0].typeName shouldBe "java.lang.String"

        val coll = tm.genericTypes[1]
        coll.typeName shouldBe "java.util.Collection"
        coll.genericTypes[0].typeName shouldBe "generated.String"
    }

    "read additional parameter with generic parameter ?" {
        val yaml = """
                   |openapi-processor-mapping: v4
                   |
                   |options:
                   |  package-name: generated
                   |
                   |map:
                   |  parameters:
                   |    - add: foo => io.openapiprocessor.GenericType<?>
                   """.trimMargin()

        // when:
        val mappingData = converter.convert(reader.read(yaml))
        val types = mappingData.globalMappings.findAddParameterTypeMappings(AddParameterTypeMatcher(), ParametersStep())

        types shouldHaveSize 1
        val type = types.first()

        type.parameterName shouldBe "foo"
        val tm = type.mapping
        tm.targetTypeName shouldBe "io.openapiprocessor.GenericType"
        tm.genericTypes.size shouldBe 1
        tm.genericTypes[0].typeName shouldBe "?"
    }
})
