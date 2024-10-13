/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.MappingFinderQuery
import io.openapiprocessor.core.converter.mapping.AmbiguousTypeMappingException
import io.openapiprocessor.core.converter.mapping.steps.EndpointsStep
import io.openapiprocessor.core.converter.mapping.steps.GlobalsStep
import io.openapiprocessor.core.converter.mapping.steps.ParametersStep
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.*
import org.slf4j.Logger


class MappingConverterParameterSpec: StringSpec({
    val reader = MappingReader()
    reader.log = mockk<Logger>(relaxed = true)

    "read global parameter type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |map:
           |  parameters:
           |    - type: Foo => mapping.Foo
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val typeMapping = mappings.findParameterTypeMapping(typeMatcher(name = "Foo"), GlobalsStep())!!

        typeMapping.sourceTypeName shouldBe "Foo"
        typeMapping.targetTypeName shouldBe "mapping.Foo"
        typeMapping.sourceTypeFormat.shouldBeNull()
        typeMapping.genericTypes.shouldBeEmpty()
    }

    "missing global parameter type mapping returns null" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val typeMapping = mappings.findParameterTypeMapping(typeMatcher(name = "Foo"), GlobalsStep())

        typeMapping.shouldBeNull()
    }

    "duplicate global parameter type mapping throws" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           |map:
           |  parameters:
           |    - type: Foo => io.openapiprocessor.Foo
           |    - type: Foo => io.openapiprocessor.Foo
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        shouldThrow<AmbiguousTypeMappingException> {
            mappings.findParameterTypeMapping(typeMatcher(name = "Foo"), GlobalsStep())
        }
    }

    "read global parameter name type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |map:
           |  parameters:
           |    - name: foo => mapping.Foo
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val nameTypeMapping = mappings.findParameterNameTypeMapping(parameterNameMatcher(name = "foo"), ParametersStep())!!

        nameTypeMapping.parameterName shouldBe "foo"
        nameTypeMapping.mapping.sourceTypeName.shouldBeNull()
        nameTypeMapping.mapping.targetTypeName shouldBe "mapping.Foo"
        nameTypeMapping.mapping.sourceTypeFormat.shouldBeNull()
        nameTypeMapping.mapping.genericTypes.shouldBeEmpty()
    }

    "missing global parameter name type mapping returns null" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val nameTypeMapping = mappings.findParameterNameTypeMapping(parameterNameMatcher(name = "foo"), ParametersStep())

        nameTypeMapping.shouldBeNull()
    }

    "duplicate global parameter name type mapping throws" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           |map:
           |  parameters:
           |    - name: foo => io.openapiprocessor.Foo
           |    - name: foo => io.openapiprocessor.Foo
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        shouldThrow<AmbiguousTypeMappingException> {
            mappings.findParameterNameTypeMapping(parameterNameMatcher(name = "foo"), ParametersStep())
        }
    }

    "missing global additional parameter name type mapping returns empty list" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |  
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val addMappings = mappings.findAddParameterTypeMappings(addParameterTypeMatcher(), ParametersStep())

        addMappings.shouldBeEmpty()
    }

    "read global additional parameter type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |map:
           |  parameters:
           |    - add: foo => annotation.Bar mapping.Foo
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val addMappings = mappings.findAddParameterTypeMappings(addParameterTypeMatcher(), ParametersStep())

        addMappings.size.shouldBe(1)
        val add = addMappings.first()
        add.parameterName shouldBe "foo"
        add.mapping.sourceTypeName.shouldBeNull()
        add.mapping.sourceTypeFormat.shouldBeNull()
        add.mapping.targetTypeName shouldBe "mapping.Foo"
        add.annotation!!.type shouldBe "annotation.Bar"
        add.annotation!!.parameters.shouldBeEmpty()
    }

    "read multiple global additional parameter type mappings" {
        val yaml = """
           |openapi-processor-mapping: v8
           |map:
           |  parameters:
           |    - add: foo => mapping.Foo
           |    - add: bar => mapping.Bar
           """.trimMargin()

        // when:
        val mapping = reader.read(yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val addMappings = mappings.findAddParameterTypeMappings(addParameterTypeMatcher(), ParametersStep())

        addMappings.size.shouldBe(2)
        addMappings[0].parameterName shouldBe "foo"
        addMappings[1].parameterName shouldBe "bar"
    }

    "read global annotation parameter type mapping" {
        val yaml = """
            |openapi-processor-mapping: v8
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
            | 
            |map:
            |  parameters:
            |    - type: Foo @ io.openapiprocessor.Foo
            """.trimMargin()

        val mapping = reader.read(yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        val annotationMappings = mappings.findAnnotationParameterTypeMapping(
            annotationTypeMatcher(type = "Foo"), ParametersStep())

         annotationMappings shouldHaveSize 1
         val annotationMapping = annotationMappings.first()
         annotationMapping.sourceTypeName shouldBe  "Foo"
         annotationMapping.sourceTypeFormat.shouldBeNull()
         annotationMapping.annotation.type shouldBe "io.openapiprocessor.Foo"
         annotationMapping.annotation.parameters.shouldBeEmpty()
     }

     "multiple global annotation parameter type mappings returns all" {
         val yaml = """
            |openapi-processor-mapping: v8
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
            |  
            |map:
            |  parameters:
            |    - type: Foo @ io.openapiprocessor.Foo
            |    - type: Foo @ io.openapiprocessor.Bar
            """.trimMargin()

         val mapping = reader.read (yaml) as Mapping
         val mappings = MappingConverter(mapping).convert().globalMappings

         val annotationMappings = mappings.findAnnotationParameterTypeMapping(
             annotationTypeMatcher(type = "Foo"), ParametersStep())

         annotationMappings shouldHaveSize 2
     }

     "missing annotation parameter type mapping returns empty list" {
         val yaml = """
            |openapi-processor-mapping: v8
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
            |  
            """.trimMargin()

         // when:
         val mapping = reader.read (yaml) as Mapping
         val mappings = MappingConverter(mapping).convert().globalMappings

         // then:
         val annotationMappings = mappings.findAnnotationParameterTypeMapping(
             annotationTypeMatcher(name = "Foo"), ParametersStep())

         annotationMappings.shouldBeEmpty()
     }

    "read global annotation parameter name type mapping" {
         val yaml = """
            |openapi-processor-mapping: v8
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
            | 
            |map:
            |  parameters:
            |    - name: foo @ io.openapiprocessor.Foo
            """.trimMargin()

         val mapping = reader.read (yaml) as Mapping
         val mappings = MappingConverter(mapping).convert().globalMappings

         val annotationMappings = mappings.findAnnotationParameterNameTypeMapping(
             annotationParameterNameTypeMatcher(name = "foo"), ParametersStep())

         annotationMappings shouldHaveSize 1
         val annotationMapping = annotationMappings.first()
         annotationMapping.name shouldBe  "foo"
         annotationMapping.annotation.type shouldBe "io.openapiprocessor.Foo"
         annotationMapping.annotation.parameters.shouldBeEmpty()
     }

    "multiple global annotation parameter name type mappings returns all" {
        val yaml = """
            |openapi-processor-mapping: v8
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
            |  
            |map:
            |  parameters:
            |    - name: foo @ io.openapiprocessor.Foo
            |    - name: foo @ io.openapiprocessor.Bar
            """.trimMargin()

        val mapping = reader.read(yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        val annotationMappings = mappings.findAnnotationParameterNameTypeMapping(
            annotationParameterNameTypeMatcher(name = "foo"), ParametersStep())

        annotationMappings shouldHaveSize 2
    }

    "missing annotation parameter name type mapping returns empty list" {
        val yaml = """
            |openapi-processor-mapping: v8
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
            |  
            """.trimMargin()

        // when:
        val mapping = reader.read(yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val annotationMappings = mappings.findAnnotationParameterNameTypeMapping(
            annotationTypeMatcher(name = "foo"), ParametersStep())

        annotationMappings.shouldBeEmpty()
    }

    "reads endpoint parameter type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      parameters:
           |        - type: Foo => io.openapiprocessor.Foo
           |      
           |      get:
           |        parameters:
           |          - type: Foo => io.openapiprocessor.Foo2
           |
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        // then:
        val postQuery = MappingFinderQuery(path = "/foo", method = HttpMethod.POST, name = "Foo")
        val typeMapping = mappings["/foo"]!!.findParameterTypeMapping(postQuery, ParametersStep())!!

        typeMapping.sourceTypeName shouldBe "Foo"
        typeMapping.sourceTypeFormat.shouldBeNull()
        typeMapping.targetTypeName shouldBe "io.openapiprocessor.Foo"

        val getQuery = MappingFinderQuery(path = "/foo", method = HttpMethod.GET, name= "Foo")
        val typeMappingGet = mappings["/foo"]!!.findParameterTypeMapping(getQuery, ParametersStep())!!

        typeMappingGet.sourceTypeName shouldBe "Foo"
        typeMappingGet.sourceTypeFormat.shouldBeNull()
        typeMappingGet.targetTypeName shouldBe "io.openapiprocessor.Foo2"
    }

    "read endpoint annotation type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |
           |map:
           |  paths:
           |    /foo:
           |      parameters:
           |        - type: Foo @ io.openapiprocessor.Foo
           |        - type: Foo @ io.openapiprocessor.Bar
           |
           |      get:
           |        parameters:
           |          - type: Foo @ io.openapiprocessor.Foo2
           |          - type: Foo @ io.openapiprocessor.Bar2
           |
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        val annotationMappings = mappings["/foo"]!!.findAnnotationParameterTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST, type = "Foo"), ParametersStep())

        annotationMappings shouldHaveSize 2
        annotationMappings[0].sourceTypeName shouldBe  "Foo"
        annotationMappings[0].sourceTypeFormat.shouldBeNull()
        annotationMappings[0].annotation.type shouldBe "io.openapiprocessor.Foo"
        annotationMappings[0].annotation.parameters.shouldBeEmpty()
        annotationMappings[1].sourceTypeName shouldBe  "Foo"
        annotationMappings[1].sourceTypeFormat.shouldBeNull()
        annotationMappings[1].annotation.type shouldBe "io.openapiprocessor.Bar"
        annotationMappings[1].annotation.parameters.shouldBeEmpty()

        val annotationMappingsGet = mappings["/foo"]!!.findAnnotationParameterTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET, type = "Foo"), ParametersStep())

        annotationMappingsGet shouldHaveSize 2
        annotationMappingsGet[0].sourceTypeName shouldBe  "Foo"
        annotationMappingsGet[0].sourceTypeFormat.shouldBeNull()
        annotationMappingsGet[0].annotation.type shouldBe "io.openapiprocessor.Foo2"
        annotationMappingsGet[0].annotation.parameters.shouldBeEmpty()
        annotationMappingsGet[1].sourceTypeName shouldBe  "Foo"
        annotationMappingsGet[1].sourceTypeFormat.shouldBeNull()
        annotationMappingsGet[1].annotation.type shouldBe "io.openapiprocessor.Bar2"
        annotationMappingsGet[1].annotation.parameters.shouldBeEmpty()
    }

    "reads endpoint parameter name mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      parameters:
           |        - name: foo => io.openapiprocessor.Foo
           |      
           |      get:
           |        parameters:
           |          - name: foo => io.openapiprocessor.Foo2
           |
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        // then:
        val typeMapping = mappings["/foo"]!!.findParameterNameTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST, name = "foo"), ParametersStep())!!

        typeMapping.parameterName shouldBe "foo"
        typeMapping.mapping.sourceTypeName.shouldBeNull()
        typeMapping.mapping.targetTypeName shouldBe "io.openapiprocessor.Foo"

        val typeMappingGet = mappings["/foo"]!!.findParameterNameTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET, name= "foo"), ParametersStep())!!

        typeMappingGet.parameterName shouldBe "foo"
        typeMappingGet.mapping.sourceTypeName.shouldBeNull()
        typeMappingGet.mapping.targetTypeName shouldBe "io.openapiprocessor.Foo2"
    }

    "read endpoint annotation parameter name type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           |
           |map:
           |  paths:
           |    /foo:
           |      parameters:
           |        - name: foo @ io.openapiprocessor.Foo
           |        - name: foo @ io.openapiprocessor.Bar
           |
           |      get:
           |        parameters:
           |          - name: foo @ io.openapiprocessor.Foo2
           |          - name: foo @ io.openapiprocessor.Bar2
           |
           """.trimMargin()

        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        val annotationMappings = mappings["/foo"]!!.findAnnotationParameterNameTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST, name = "foo"), ParametersStep())

        annotationMappings shouldHaveSize 2
        annotationMappings[0].name shouldBe  "foo"
        annotationMappings[0].annotation.type shouldBe "io.openapiprocessor.Foo"
        annotationMappings[1].name shouldBe  "foo"
        annotationMappings[1].annotation.type shouldBe "io.openapiprocessor.Bar"

        val annotationMappingsGet = mappings["/foo"]!!.findAnnotationParameterNameTypeMapping(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET, name = "foo"), ParametersStep())

        annotationMappingsGet shouldHaveSize 2
        annotationMappingsGet[0].name shouldBe  "foo"
        annotationMappingsGet[0].annotation.type shouldBe "io.openapiprocessor.Foo2"
        annotationMappingsGet[1].name shouldBe  "foo"
        annotationMappingsGet[1].annotation.type shouldBe "io.openapiprocessor.Bar2"
    }

    "reads endpoint add parameter type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |
           |options:
           |  package-name: io.openapiprocessor.somewhere
           | 
           |map:
           |  paths:
           |    /foo:
           |      parameters:
           |        - add: foo => io.openapiprocessor.Foo
           |        - add: bar => io.openapiprocessor.Bar
           |      
           |      get:
           |        parameters:
           |          - add: foo2 => io.openapiprocessor.Foo2
           |          - add: bar2 => io.openapiprocessor.Bar2
           |
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().endpointMappings

        // then:
        val addMappings = mappings["/foo"]!!.findAddParameterTypeMappings(
            MappingFinderQuery(path = "/foo", method = HttpMethod.POST), ParametersStep())

        addMappings shouldHaveSize 2
        addMappings[0].parameterName shouldBe "foo"
        addMappings[0].mapping.targetTypeName shouldBe "io.openapiprocessor.Foo"
        addMappings[1].parameterName shouldBe "bar"
        addMappings[1].mapping.targetTypeName shouldBe "io.openapiprocessor.Bar"

        val addMappingsGet = mappings["/foo"]!!.findAddParameterTypeMappings(
            MappingFinderQuery(path = "/foo", method = HttpMethod.GET), ParametersStep())

        addMappingsGet shouldHaveSize 2
        addMappingsGet[0].parameterName shouldBe "foo2"
        addMappingsGet[0].mapping.targetTypeName shouldBe "io.openapiprocessor.Foo2"
        addMappingsGet[1].parameterName shouldBe "bar2"
        addMappingsGet[1].mapping.targetTypeName shouldBe "io.openapiprocessor.Bar2"
    }
})
