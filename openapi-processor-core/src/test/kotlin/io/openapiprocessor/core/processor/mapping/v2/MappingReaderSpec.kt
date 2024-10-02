/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */
package io.openapiprocessor.core.processor.mapping.v2

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.processor.MappingValidator
import io.openapiprocessor.jsonschema.ouput.OutputUnitFlag
import org.slf4j.Logger

class MappingReaderSpec: StringSpec ({
    isolationMode = IsolationMode.InstancePerTest

    "warns use of old mapping version key" {
        val yaml = """
            |openapi-processor-spring: v2
        """.trimMargin()

        val validator = mockk<MappingValidator>()
        every { validator.validate(any(), any()) } returns OutputUnitFlag(true)

        val log = mockk<Logger>(relaxed = true)

        val reader = MappingReader(validator)
        reader.log = log

        // when:
        reader.read(yaml)

        // then:
        verify(exactly = 1) { log.warn(any()) }
    }

    "validates mapping.yaml" {
        val yaml = """
            |openapi-processor-mapping: v2
            |
            |options:
            |  package-name: io.openapiprocessor.somewhere
        """.trimMargin()

        val validator = mockk<MappingValidator>()
        every { validator.validate(any(), any()) } returns OutputUnitFlag(true)

        MappingReader(validator).read(yaml)

        verify { validator.validate(yaml, "v2") }
    }

//    "validates mapping.yaml with version" {
//        val yaml = """
//            |openapi-processor-mapping: v2.1
//            |
//            |options:
//            |  package-name: io.openapiprocessor.somewhere
//        """.trimMargin()
//
//        val validator = mockk<MappingValidator>()
//        every { validator.validate(any()) } returns emptySet()
//
//        MappingReader(validator).read(yaml)
//
//        verify { validator.validate(yaml) }
//    }

    "logs mapping.yaml validation errors" {
        val yaml = """
            |openapi-processor-mapping: v2
        """.trimMargin()

        val log = mockk<Logger>(relaxed = true)

        val reader = MappingReader()
        reader.log = log

        // when:
        reader.read (yaml)

        // then:  // don't know how to check,
        verifySequence {
            log.warn("mapping is not valid!")
            log.warn("{} at {}", "should have a property 'options'", "/")
        }
    }

    "reads model-name-suffix" {
        val yaml = """
            |openapi-processor-mapping: v2
            |options:
            |  model-name-suffix: Suffix
        """.trimMargin()

        val reader = MappingReader()

        // when:
        val mapping = reader.read (yaml) as Mapping

        // then:
        mapping.options.modelNameSuffix shouldBe "Suffix"
    }

    "reads format-code" {
        val yaml = """
            |openapi-processor-mapping: v2
            |options:
            |  format-code: false
        """.trimMargin()

        val reader = MappingReader()

        // when:
        val mapping = reader.read (yaml) as Mapping

        // then:
        mapping.options.formatCode shouldBe false
    }

    "reads generated-annotation" {
        val yaml = """
            |openapi-processor-mapping: v8
            |options:
            |  package-name: no.warning
            |  generated-annotation: false
        """.trimMargin()

        val reader = MappingReader()

        // when:
        val mapping = reader.read (yaml) as Mapping

        // then:
        mapping.options.generatedAnnotation shouldBe false
    }

    "reads generated-date" {
        val yaml = """
            |openapi-processor-mapping: v8
            |options:
            |  package-name: no.warning
            |  generated-date: false
        """.trimMargin()

        val reader = MappingReader()

        // when:
        val mapping = reader.read (yaml) as Mapping

        // then:
        mapping.options.generatedDate shouldBe false
    }

    "reads enum-type" {
        val yaml = """
            |openapi-processor-mapping: v5
            |options:
            |  package-name: no.warning
            |  enum-type: string
        """.trimMargin()

        val reader = MappingReader()

        // when:
        val mapping = reader.read (yaml) as Mapping

        // then:
        mapping.options.enumType shouldBe "string"
    }

    "reads extensions mappings" {
        val yaml = """
            |openapi-processor-mapping: v6
            |options:
            |  package-name: no.warning
            |map:
            |  extensions:
            |    x-single: foo @ custom.Annotation
            |    x-list:
            |      - fooA @ custom.Annotation
            |      - fooB @ custom.Annotation
        """.trimMargin()

        val reader = MappingReader()

        val mapping = reader.read (yaml) as Mapping

        mapping.map.extensions shouldHaveSize 2
        val extSingle = mapping.map.extensions["x-single"] as List<Type>
        extSingle shouldHaveSize 1
        extSingle[0].type shouldBe "foo @ custom.Annotation"

        val extList = mapping.map.extensions["x-list"] as List<Type>
        extList shouldHaveSize 2
        extList[0].type shouldBe "fooA @ custom.Annotation"
        extList[1].type shouldBe "fooB @ custom.Annotation"
    }

    "reads json property annotation" {
        val yaml = """
            |openapi-processor-mapping: v8
            |options:
            |  package-name: no.warning
            |  json-property-annotation: auto
        """.trimMargin()

        val reader = MappingReader()

        // when:
        val mapping = reader.read (yaml) as Mapping

        // then:
        mapping.options.jsonPropertyAnnotation shouldBe "auto"
    }

    "reads target-dir options" {
        val yaml = """
            |openapi-processor-mapping: v9
            |options:
            |  package-name: no.warning
            |  target-dir:
            |    clear: true
            |    layout: standard
        """.trimMargin()

        val reader = MappingReader()

        // when:
        val mapping = reader.read (yaml) as Mapping

        // then:
        mapping.options.targetDir.clear shouldBe true
        mapping.options.targetDir.layout shouldBe "standard"
    }

    "reads base-path options" {
        forAll(
            row(true),
            row(false),
            row(0),
            row(1)
        ) { server ->
            val yaml =
                """
                |openapi-processor-mapping: v9
                |options:
                |  package-name: no.warning
                |  base-path:
                |   server-url: $server
                |   properties-name: base-path.properties
                """.trimMargin()

            val mapping = MappingReader().read(yaml) as Mapping

            mapping.options.basePath.serverUrl shouldBe "$server"
            mapping.options.basePath.propertiesName shouldBe "base-path.properties"
        }
    }
})
