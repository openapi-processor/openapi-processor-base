/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class QualifiedTypeSpec : StringSpec({

    "extracts import & type from qualified class" {
        val type = QualifiedType("io.openapiprocessor.Something")

        type.import shouldBe "io.openapiprocessor.Something"
        type.type shouldBe "Something"
    }

    "extracts import & type from inner class" {
        val type = QualifiedType("io.openapiprocessor.Something.Inner")

        type.import shouldBe "io.openapiprocessor.Something"
        type.type shouldBe "Something.Inner"
    }

    "extracts import & type from inner enum value" {
        val type = QualifiedType("io.openapiprocessor.Something.Enum.VALUE")

        type.import shouldBe "io.openapiprocessor.Something"
        type.type shouldBe "Something.Enum.VALUE"
    }
})
