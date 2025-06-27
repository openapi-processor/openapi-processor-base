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
import io.mockk.mockk
import io.openapiprocessor.core.converter.options.TargetDirLayout
import io.openapiprocessor.core.support.Empty
import org.slf4j.Logger

class OptionsConverterSpec: StringSpec({

    "produces default options if input options are empty" {
        val converter = OptionsConverter()
        converter.log = mockk<Logger>(relaxed = true)

        val options = converter.convertOptions(emptyMap())

        options.targetDir shouldBe null
        options.targetDirOptions.clear.shouldBeTrue()
        options.targetDirOptions.layout shouldBe TargetDirLayout.CLASSIC

        options.packageName shouldBe "io.openapiprocessor.generated"
        options.packageOptions.base shouldBe null
        options.packageOptions.location shouldBe null
        options.beanValidation shouldBe false
        options.javadoc shouldBe false
        options.modelType shouldBe "default"
        options.modelAccessors.shouldBeTrue()
        options.enumType shouldBe "default"
        options.modelNameSuffix shouldBe String.Empty
        options.formatCode.shouldBeFalse()
        options.basePathOptions.enabled shouldBe false
        options.basePathOptions.serverUrl shouldBe null
        options.basePathOptions.propertiesName shouldBe "api.properties"
        options.oneOfInterface.shouldBeFalse()
        options.responseInterface.shouldBeFalse()

        options.globalMappings.shouldNotBeNull()
        options.endpointMappings.shouldNotBeNull()
        options.extensionMappings.shouldNotBeNull()

        options.beanValidationValidOnReactive.shouldBeTrue()
        options.identifierWordBreakFromDigitToLetter.shouldBeTrue()
        options.identifierPrefixInvalidEnumStart.shouldBeTrue()
    }

    "should set target dir" {
        val converter = OptionsConverter()
        converter.log = mockk<Logger>(relaxed = true)

        val options = converter.convertOptions(mapOf(
            "targetDir" to "generated target dir"
        ))

        options.targetDir shouldBe "generated target dir"
    }

    "should accept deprecated packageName map option" {
        val converter = OptionsConverter(true)
        converter.log = mockk<Logger>(relaxed = true)

        val options = converter.convertOptions(mapOf(
            "packageName" to "obsolete"
        ))

        options.packageName shouldBe "obsolete"
    }

    "should accept deprecated beanValidation map option" {
        val converter = OptionsConverter(true)
        converter.log = mockk<Logger>(relaxed = true)

        val options = converter.convertOptions(mapOf(
            "beanValidation" to true
        ))

        options.beanValidation shouldBe true
    }

    "should accept deprecated typeMappings map option" {
        val converter = OptionsConverter(true)
        converter.log = mockk<Logger>(relaxed = true)

        val options = converter.convertOptions(mapOf(
            "typeMappings" to """
                openapi-processor-mapping: v2
                options:
                  package-name: generated
            """.trimIndent()
        ))

        options.packageName shouldBe "generated"
    }

    "should read mapping options" {
        val converter = OptionsConverter()
        converter.log = mockk<Logger>(relaxed = true)

        val options = converter.convertOptions(mapOf(
            "mapping" to """
                openapi-processor-mapping: v13
                options:
                  clear-target-dir: false
                  target-dir:
                    layout: standard
                  package-name: generated
                  package-names:
                    location: production
                  package-name-base: production
                  package-name-from-path: true
                  model-name-suffix: Suffix
                  model-type: record
                  model-accessors: false
                  enum-type: string
                  bean-validation: true
                  javadoc: true
                  format-code: false
                  base-path:
                    server-url: 0
                    properties-name: openapi.properties
                  one-of-interface: true
                  response-interface: true
                compatibility:
                  bean-validation-valid-on-reactive: false
                  identifier-word-break-from-digit-to-letter: false
                  identifier-prefix-invalid-enum-start: false
            """.trimIndent()
        ))

        options.targetDirOptions.clear.shouldBeFalse()
        options.targetDirOptions.layout.isStandard().shouldBeTrue()
        options.packageName shouldBe "generated"
        options.packageOptions.base shouldBe "generated"
        options.packageOptions.location shouldBe "production"
        options.modelNameSuffix shouldBe "Suffix"
        options.modelType shouldBe "record"
        options.modelAccessors shouldBe false
        options.enumType shouldBe "string"
        options.beanValidation shouldBe true
        options.javadoc shouldBe true
        options.formatCode.shouldBeFalse()
        options.basePathOptions.enabled shouldBe true
        options.basePathOptions.serverUrl shouldBe 0
        options.basePathOptions.propertiesName shouldBe "openapi.properties"
        options.oneOfInterface.shouldBeTrue()
        options.responseInterface.shouldBeTrue()

        options.beanValidationValidOnReactive.shouldBeFalse()
        options.identifierWordBreakFromDigitToLetter.shouldBeFalse()
        options.identifierPrefixInvalidEnumStart.shouldBeFalse()
    }

    "overrides old target-dir mapping options" {
        val converter = OptionsConverter()
        converter.log = mockk<Logger>(relaxed = true)

        val options = converter.convertOptions(mapOf(
            "mapping" to """
                openapi-processor-mapping: v9
                options:
                  package-name: pkg
                  clear-target-dir: true
                  target-dir:
                    clear: false
            """.trimIndent()
        ))

        options.targetDirOptions.clear.shouldBeFalse()
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
            converter.log = mockk<Logger>(relaxed = true)

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

    data class ServerUrlData(val source: String, val enabled: Boolean, val index: Int?)

    for (su in listOf(
        ServerUrlData("false", false, null),
        ServerUrlData("true", true, 0),
        ServerUrlData("0", true, 0),
        ServerUrlData("1", true, 1)
    )) {
        "should read bean server-url: ${su.source}" {
            val converter = OptionsConverter()
            converter.log = mockk<Logger>(relaxed = true)

            val options = converter.convertOptions(mapOf(
                "mapping" to """
                    openapi-processor-mapping: v9
                    options:
                      package-name: no.warning
                      base-path:
                        server-url: ${su.source}
                        properties-name: openapi.properties
                """.trimIndent()
            ))

            options.basePathOptions.enabled shouldBe su.enabled
            options.basePathOptions.serverUrl shouldBe su.index
            options.basePathOptions.propertiesName shouldBe "openapi.properties"
        }
    }

    "enabling base path resource will automatically enable target-dir layout standard" {
        val converter = OptionsConverter()
        converter.log = mockk<Logger>(relaxed = true)

        val options = converter.convertOptions(mapOf(
            "mapping" to """
                openapi-processor-mapping: v9
                options:
                  package-name: pkg
                  base-path:
                    server-url: true
                    properties-name: openapi.properties
            """.trimIndent()
        ))

        options.basePathOptions.enabled shouldBe true
        options.targetDirOptions.layout.isStandard() shouldBe true
    }

    data class FormatterData(val source: String, val enabled: Boolean, val formatter: String?)

    for (fd in listOf(
        FormatterData("false", false, null),
        FormatterData("true", true, "google"),
        FormatterData("google", true, "google"),
        FormatterData("eclipse", true, "eclipse")
    )) {
        "should read formatter option: ${fd.source}" {
            val converter = OptionsConverter()
            converter.log = mockk<Logger>(relaxed = true)

            val options = converter.convertOptions(mapOf(
                "mapping" to """
                    openapi-processor-mapping: v11
                    options:
                      format-code: ${fd.source}
                """.trimIndent()
            ))

            options.formatCode shouldBe fd.enabled
            options.formatCodeFormatter shouldBe fd.formatter
        }
    }
})
