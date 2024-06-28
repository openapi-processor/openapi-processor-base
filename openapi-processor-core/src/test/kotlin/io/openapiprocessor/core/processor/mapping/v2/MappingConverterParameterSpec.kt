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
import io.openapiprocessor.core.converter.mapping.AmbiguousTypeMappingException
import io.openapiprocessor.core.converter.mapping.AnnotationNameMapping
import io.openapiprocessor.core.converter.mapping.NameTypeMapping
import io.openapiprocessor.core.converter.mapping.matcher.*
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.MappingSchema

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
        val typeMapping = mappings.findGlobalParameterTypeMapping(MappingSchema(name = "Foo"))!!

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
        val typeMapping = mappings.findGlobalParameterTypeMapping(MappingSchema(name = "Foo"))

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
            mappings.findGlobalParameterTypeMapping(MappingSchema(name = "Foo"))
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
        val nameTypeMapping = mappings.findGlobalParameterNameTypeMapping(MappingSchema(name = "foo"))!!

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
        val nameTypeMapping = mappings.findGlobalParameterNameTypeMapping(MappingSchema(name = "foo"))

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
            mappings.findGlobalParameterNameTypeMapping(MappingSchema(name = "foo"))
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

         val mapping = reader.read (yaml) as Mapping
         val mappings = MappingConverter(mapping).convertX()

         val annotationMappings = mappings.findGlobalAnnotationParameterTypeMapping(MappingSchema(name = "Foo"))

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
         val mappings = MappingConverter(mapping).convertX()

         val annotationMappings = mappings.findGlobalAnnotationParameterTypeMapping(MappingSchema(name = "Foo"))

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
         val annotationMappings = mappings.findGlobalAnnotationParameterTypeMapping(MappingSchema(name = "Foo"))

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

         val annotationMappings = mappings.findGlobalAnnotationParameterNameTypeMapping(MappingSchema(name = "foo"))

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

        val annotationMappings = mappings.findGlobalAnnotationParameterNameTypeMapping(MappingSchema(name = "foo"))

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
        val annotationMappings = mappings.findGlobalAnnotationParameterNameTypeMapping(MappingSchema(name = "foo"))

        annotationMappings.shouldBeEmpty()
    }
})
