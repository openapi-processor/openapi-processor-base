/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.mapping.steps.SchemasStep
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.support.annotationTypeMatcher
import io.openapiprocessor.core.support.typeMatcher
import org.slf4j.Logger

class MappingConverterSchemaSpec: StringSpec({

    val reader = MappingReader()
    reader.log = mockk<Logger>(relaxed = true)

    "read global schema type mapping" {
        val yaml = """
           |openapi-processor-mapping: v11
           |map:
           |  schemas:
           |    - type: integer:year => java.time.Year
           """.trimMargin()

        // when:
        val mapping = reader.read (yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        // then:
        val typeMapping = mappings.findSchemaTypeMapping(
            typeMatcher(name = "integer", format = "year"), SchemasStep())!!

        typeMapping.sourceTypeName shouldBe "integer"
        typeMapping.sourceTypeFormat shouldBe "year"
        typeMapping.targetTypeName shouldBe "java.time.Year"
        typeMapping.genericTypes.shouldBeEmpty()
    }

    "read global annotation schema type mapping" {
        val yaml = """
            |openapi-processor-mapping: v11
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
            | 
            |map:
            |  schemas:
            |    - type: integer:year @ io.openapiprocessor.Annotation
            """.trimMargin()

        val mapping = reader.read(yaml) as Mapping
        val mappings = MappingConverter(mapping).convert().globalMappings

        val annotationMappings = mappings.findAnnotationSchemaTypeMapping(
            annotationTypeMatcher(type = "integer", format = "year"), SchemasStep())

         annotationMappings shouldHaveSize 1
         val annotationMapping = annotationMappings.first()
         annotationMapping.sourceTypeName shouldBe  "integer"
         annotationMapping.sourceTypeFormat shouldBe "year"
         annotationMapping.annotation.type shouldBe "io.openapiprocessor.Annotation"
         annotationMapping.annotation.parameters.shouldBeEmpty()
     }
})
