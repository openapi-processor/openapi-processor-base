/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.MappingQueryX
import io.openapiprocessor.core.converter.mapping.matcher.AnnotationTypeMatcher
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader


class MappingConverterParameterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()
    val converter = MappingConverter()

    // obsolete
    "read global parameter name type mapping, old" {
        val yaml = """
           |openapi-processor-mapping: v5
           |map:
           |  parameters:
           |    - name: foo => mapping.Foo
           """.trimMargin()

        val mappings = converter.convert(reader.read(yaml))

        // then:
        val parameter = mappings.first() as NameTypeMapping
        parameter.parameterName shouldBe "foo"
        parameter.mapping.sourceTypeName.shouldBeNull()
        parameter.mapping.sourceTypeFormat.shouldBeNull()
        parameter.mapping.targetTypeName shouldBe "mapping.Foo"
        parameter.mapping.genericTypes.shouldBeEmpty()
    }

    // obsolete
    "read global parameter name annotation mapping, old" {
        val yaml = """
           |openapi-processor-mapping: v5
           |map:
           |  parameters:
           |    - name: foo @ annotation.Foo
           """.trimMargin()

        val mappings = converter.convert(reader.read(yaml))

        mappings.size.shouldBe(1)
        val annotation = mappings.first() as AnnotationNameMapping
        annotation.name shouldBe "foo"
        annotation.annotation.type shouldBe "annotation.Foo"
    }

    "read global parameter type mapping" {
        val yaml = """
           |openapi-processor-mapping: v8
           |map:
           |  parameters:
           |    - type: Foo => mapping.Foo
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val typeMapping = mappings.findGlobalParameterTypeMapping(MappingQueryValues(name = "Foo"))!!

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val typeMapping = mappings.findGlobalParameterTypeMapping(MappingQueryValues(name = "Foo"))

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
        val mappings = MappingConverter(mapping).convertX()

        shouldThrow<AmbiguousTypeMappingException> {
            mappings.findGlobalParameterTypeMapping(MappingQueryValues(name = "Foo"))
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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val nameTypeMapping = mappings.findGlobalParameterNameTypeMapping(MappingQueryValues(name = "foo"))!!

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val nameTypeMapping = mappings.findGlobalParameterNameTypeMapping(MappingQueryValues(name = "foo"))

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
        val mappings = MappingConverter(mapping).convertX()

        shouldThrow<AmbiguousTypeMappingException> {
            mappings.findGlobalParameterNameTypeMapping(MappingQueryValues(name = "foo"))
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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val addMappings = mappings.findGlobalAddParameterTypeMappings()

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val addMappings = mappings.findGlobalAddParameterTypeMappings()

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val addMappings = mappings.findGlobalAddParameterTypeMappings()

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
        val mappings = MappingConverter(mapping).convertX2()

        val annotationMappings = mappings.globalMappings.findAnnotationParameterTypeMapping(
            AnnotationTypeMatcher(MappingQueryX(type = "Foo"))
        )

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
         val mappings = MappingConverter(mapping).convertX2()

         val annotationMappings = mappings.globalMappings.findAnnotationParameterTypeMapping(
             AnnotationTypeMatcher(MappingQueryX(type = "Foo")))

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
         val mappings = MappingConverter(mapping).convertX()

         // then:
         val annotationMappings = mappings.findGlobalAnnotationParameterTypeMapping(MappingQueryValues(name = "Foo"))

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
         val mappings = MappingConverter(mapping).convertX()

         val annotationMappings = mappings.findGlobalAnnotationParameterNameTypeMapping(MappingQueryValues(name = "foo"))

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
        val mappings = MappingConverter(mapping).convertX()

        val annotationMappings = mappings.findGlobalAnnotationParameterNameTypeMapping(MappingQueryValues(name = "foo"))

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val annotationMappings = mappings.findGlobalAnnotationParameterNameTypeMapping(MappingQueryValues(name = "foo"))

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val typeMapping = mappings.findEndpointParameterTypeMapping(
            MappingQueryValues(path = "/foo", method = HttpMethod.POST, name = "Foo"))!!

        typeMapping.sourceTypeName shouldBe "Foo"
        typeMapping.sourceTypeFormat.shouldBeNull()
        typeMapping.targetTypeName shouldBe "io.openapiprocessor.Foo"

        val typeMappingGet = mappings.findEndpointParameterTypeMapping(
            MappingQueryValues(path = "/foo", method = HttpMethod.GET, name= "Foo"))!!

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
        val mappings = MappingConverter(mapping).convertX2()
        val repository = MappingRepository(endpointMappings = mappings.endpointMappings)

        val annotationMappings = repository.findEndpointAnnotationParameterTypeMapping(
            MappingQueryX(path = "/foo", method = HttpMethod.POST, type = "Foo"))

        annotationMappings shouldHaveSize 2
        annotationMappings[0].sourceTypeName shouldBe  "Foo"
        annotationMappings[0].sourceTypeFormat.shouldBeNull()
        annotationMappings[0].annotation.type shouldBe "io.openapiprocessor.Foo"
        annotationMappings[0].annotation.parameters.shouldBeEmpty()
        annotationMappings[1].sourceTypeName shouldBe  "Foo"
        annotationMappings[1].sourceTypeFormat.shouldBeNull()
        annotationMappings[1].annotation.type shouldBe "io.openapiprocessor.Bar"
        annotationMappings[1].annotation.parameters.shouldBeEmpty()

        val annotationMappingsGet = repository.findEndpointAnnotationParameterTypeMapping(
            MappingQueryX(path = "/foo", method = HttpMethod.GET, type = "Foo"))

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val typeMapping = mappings.findEndpointParameterNameTypeMapping(
            MappingQueryValues(path = "/foo", method = HttpMethod.POST, name = "foo"))!!

        typeMapping.parameterName shouldBe "foo"
        typeMapping.mapping.sourceTypeName.shouldBeNull()
        typeMapping.mapping.targetTypeName shouldBe "io.openapiprocessor.Foo"

        val typeMappingGet = mappings.findEndpointParameterNameTypeMapping(
            MappingQueryValues(path = "/foo", method = HttpMethod.GET, name= "foo"))!!

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
        val mappings = MappingConverter(mapping).convertX()

        val annotationMappings = mappings.findEndpointAnnotationParameterNameTypeMapping(
            MappingQueryValues(path = "/foo", method = HttpMethod.POST, name = "foo"))

        annotationMappings shouldHaveSize 2
        annotationMappings[0].name shouldBe  "foo"
        annotationMappings[0].annotation.type shouldBe "io.openapiprocessor.Foo"
        annotationMappings[1].name shouldBe  "foo"
        annotationMappings[1].annotation.type shouldBe "io.openapiprocessor.Bar"

        val annotationMappingsGet = mappings.findEndpointAnnotationParameterNameTypeMapping(
            MappingQueryValues(path = "/foo", method = HttpMethod.GET, name = "foo"))

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
        val mappings = MappingConverter(mapping).convertX()

        // then:
        val addMappings = mappings.findEndpointAddParameterTypeMappings(
            MappingQueryValues(path = "/foo", method = HttpMethod.POST))

        addMappings shouldHaveSize 2
        addMappings[0].parameterName shouldBe "foo"
        addMappings[0].mapping.targetTypeName shouldBe "io.openapiprocessor.Foo"
        addMappings[1].parameterName shouldBe "bar"
        addMappings[1].mapping.targetTypeName shouldBe "io.openapiprocessor.Bar"

        val addMappingsGet = mappings.findEndpointAddParameterTypeMappings(
            MappingQueryValues(path = "/foo", method = HttpMethod.GET))

        addMappingsGet shouldHaveSize 2
        addMappingsGet[0].parameterName shouldBe "foo2"
        addMappingsGet[0].mapping.targetTypeName shouldBe "io.openapiprocessor.Foo2"
        addMappingsGet[1].parameterName shouldBe "bar2"
        addMappingsGet[1].mapping.targetTypeName shouldBe "io.openapiprocessor.Bar2"
    }
})
