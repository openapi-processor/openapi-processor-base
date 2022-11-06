/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GoogleFormatterSpec : StringSpec({

    "formats code" {
        val formatter = GoogleFormatter()

        formatter.format("    class   Foo   {   }    ") shouldBe """
            |class Foo {
            |}
            |
        """.trimMargin()
    }
})
