/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.mapping.UnknownDataTypeException
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.support.TestLogger
import io.openapiprocessor.core.support.parse
import org.slf4j.event.Level

class ApiConverterErrorSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    "logs error when datatype conversion fails" {
        val converter = ApiConverter(ApiOptions(), mockk<Framework>())
        val logger = TestLogger()
        converter.log = logger

        val openApi = parse ("""
            openapi: 3.0.2
            info:
              title: unknown data type
              version: 1.0.0
            
            paths:
              /book:
                get:
                  responses:
                    '200':
                      description: none
                      content:
                        text/plain:
                          schema:
                            type: unknown
        """.trimIndent())

        shouldNotThrow<UnknownDataTypeException> {
            converter.convert (openApi)
        }

        val messages = logger.getMessages()
        messages.size shouldBe 1
        messages.first().level shouldBe Level.ERROR
    }
})
