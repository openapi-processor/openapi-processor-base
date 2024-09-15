/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.parser.ParserException
import org.slf4j.Logger

class ParserSpec : StringSpec ({

    "catch parser resource exception" {
        val parser = Parser()
        parser.log = mockk<Logger>(relaxed = true)

        shouldThrow<ParserException> {
            parser.parse("openapi.yaml")
        }
    }

    "has warning messages" {
        val parser = Parser()

        val api = parser.parseString("""
            openapi: 3.0.3
            info:
              title: missing path
              version: '1.0'
              
              """.trimIndent())

        api.hasWarnings() shouldBe true
    }

})
