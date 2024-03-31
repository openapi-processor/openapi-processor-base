/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import com.google.googlejavaformat.java.Formatter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GoogleFormatterSpec : StringSpec({

    "formats code" {
        val formatter = GoogleFormatter()

        formatter.format("    class   Foo   {   }    ") shouldBe """
            |class Foo {
            |}
            |
        """.trimMargin()
    }

    "formatter catches IllegalAccessError and re-throws with link to add-export note" {
        val f: Formatter = mockk<Formatter>()
        every { f.formatSource(any()) } throws IllegalAccessError("fake illegal access error")

        val formatter = object: GoogleFormatter() {
            init {
                formatter = f
            }
        }

        shouldThrow<FormattingException> {
            formatter.format("....")
        }
    }
})
