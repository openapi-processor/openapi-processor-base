/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.AmbiguousTypeMappingException
import io.openapiprocessor.core.converter.mapping.AnnotationNameMapping
import io.openapiprocessor.core.converter.mapping.NameTypeMapping
import io.openapiprocessor.core.converter.mapping.matcher.TypeMatcher
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.MappingSchema

class MappingConverterParameterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reader = MappingReader()
    val converter = MappingConverter()

    // obsolete
    "read global parameter name type mapping" {
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
    "read global parameter name annotation mapping" {
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
        val typeMapping = mappings.findGlobalParameterTypeMapping(
            TypeMatcher(MappingSchema(name = "Foo")))!!

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
        val typeMapping = mappings.findGlobalParameterTypeMapping(
            TypeMatcher(MappingSchema(name = "Foo")))

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
            mappings.findGlobalParameterTypeMapping(
                TypeMatcher(MappingSchema(name = "Foo")))
        }
    }
})


// should throw when multiple
/*
"read global parameter name annotation mapping 2" {
    val yaml = """
       |openapi-processor-mapping: v8
       |map:
       |  parameters:
       |    - name: foo @ annotation.Foo
       |    - name: foo @ annotation.Bar
       """.trimMargin()

    val mapping = reader.read (yaml) as Mapping
    val mappings = MappingConverter(mapping).convertX()

    val annotationMappings = mappings.findGlobalParameterAnnotationMappings()

    mappings.size.shouldBe(1)
    val annotation = mappings.first() as AnnotationNameMapping
    annotation.name shouldBe "foo"
    annotation.annotation.type shouldBe "annotation.Foo"
}*/

/*
        val typeMapping = mappings.findGlobalTypeMapping(
            TypeMatcher(MappingSchema(name = "Foo")))!!

    // type & name => invalid
    // type => 1
    // name => 1
    // add => many
    // type and name => @ many
    //

 */
