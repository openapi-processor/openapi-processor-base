/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.openapiprocessor.core.converter.mapping.UnknownDataTypeException
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.support.parse
import io.openapiprocessor.core.writer.java.JavaIdentifier

class ApiConverterErrorSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    fun addAppender(converter: ApiConverter): ListAppender<ILoggingEvent> {
        val appender = ListAppender<ILoggingEvent>()
        (converter.log as Logger).detachAndStopAllAppenders()
        (converter.log as Logger).setAdditive(false)
        (converter.log as Logger).addAppender(appender)
        appender.start()
        return appender
    }

    "logs error when datatype conversion fails" {
        val converter = ApiConverter(ApiOptions(), JavaIdentifier(), mockk<Framework>())
        val appender = addAppender(converter)

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

        appender.list.size shouldBe 1
        appender.list.first().level shouldBe Level.ERROR
    }

})
