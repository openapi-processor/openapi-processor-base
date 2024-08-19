/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.support.Empty

class OptionsConverterSpec: StringSpec({

    "produces default options if input options are empty" {
        val converter = OptionsConverter()

        val options = converter.convertOptions(emptyMap())

        options.targetDir shouldBe null
        options.clearTargetDir.shouldBeTrue()

        options.packageName shouldBe "io.openapiprocessor.generated"
        options.beanValidation shouldBe false
        options.javadoc shouldBe false
        options.modelType shouldBe "default"
        options.enumType shouldBe "default"
        options.modelNameSuffix shouldBe String.Empty
        options.formatCode.shouldBeFalse()

        options.globalMappings.shouldNotBeNull()
        options.endpointMappings.shouldNotBeNull()
        options.extensionMappings.shouldNotBeNull()

        options.beanValidationValidOnReactive.shouldBeTrue()
        options.identifierWordBreakFromDigitToLetter.shouldBeTrue()
    }

    "should set target dir" {
        val converter = OptionsConverter()

        val options = converter.convertOptions(mapOf(
            "targetDir" to "generated target dir"
        ))

        options.targetDir shouldBe "generated target dir"
    }

    "should accept deprecated packageName map option" {
        val converter = OptionsConverter(true)

        val options = converter.convertOptions(mapOf(
            "packageName" to "obsolete"
        ))

        options.packageName shouldBe "obsolete"
    }

    "should accept deprecated beanValidation map option" {
        val converter = OptionsConverter(true)

        val options = converter.convertOptions(mapOf(
            "beanValidation" to true
        ))

        options.beanValidation shouldBe true
    }

    "should accept deprecated typeMappings map option" {
        val converter = OptionsConverter(true)

        val options = converter.convertOptions(mapOf(
            "typeMappings" to """
                openapi-processor-mapping: v2
                options:
                  package-name: generated
            """.trimIndent()
        ))

        options.packageName shouldBe "generated"
    }

    "should read Mapping options (old, v1)" {
        val converter = OptionsConverter()
        val options = converter.convertOptions(mapOf(
            "mapping" to """
                options:
                  package-name: generated
                  bean-validation: true
            """.trimIndent()
        ))

        options.packageName shouldBe "generated"
        options.beanValidation shouldBe true
    }

    "should read Mapping options (new, v2)" {
        val converter = OptionsConverter()
        val options = converter.convertOptions(mapOf(
            "mapping" to """
                openapi-processor-mapping: v7
                options:
                  clear-target-dir: false
                  package-name: generated
                  model-name-suffix: Suffix
                  model-type: record
                  enum-type: string
                  bean-validation: true
                  javadoc: true
                  format-code: false
                compatibility:
                  bean-validation-valid-on-reactive: false
                  identifier-word-break-from-digit-to-letter: false
            """.trimIndent()
        ))

        options.clearTargetDir.shouldBeFalse()
        options.packageName shouldBe "generated"
        options.modelNameSuffix shouldBe "Suffix"
        options.modelType shouldBe "record"
        options.enumType shouldBe "string"
        options.beanValidation shouldBe true
        options.javadoc shouldBe true
        options.formatCode.shouldBeFalse()

        options.beanValidationValidOnReactive.shouldBeFalse()
        options.identifierWordBreakFromDigitToLetter.shouldBeFalse()
    }

    data class BeanData(val source: String, val enabled: Boolean, val format: String?)

    for (bd in listOf(
        BeanData("false", false, null),
        BeanData("true", true, "javax"),
        BeanData("javax", true, "javax"),
        BeanData("jakarta", true, "jakarta")
    )) {
        "should read bean validation & format: ${bd.source}" {
            val converter = OptionsConverter()

            val options = converter.convertOptions(mapOf(
                "mapping" to """
                    openapi-processor-mapping: v3
                    options:
                      bean-validation: ${bd.source}
                """.trimIndent()
            ))

            options.beanValidation shouldBe bd.enabled
            options.beanValidationFormat shouldBe bd.format
        }
    }
})
