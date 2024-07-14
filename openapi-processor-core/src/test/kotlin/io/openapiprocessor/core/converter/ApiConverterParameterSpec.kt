/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.support.apiConverter
import io.openapiprocessor.core.support.getEndpoint
import io.openapiprocessor.core.support.parseApi
import io.openapiprocessor.core.support.parseOptions

class ApiConverterParameterSpec: StringSpec({

    "adds additional request parameter from endpoint mapping" {
        val openApi = parseApi(
            """
            |openapi: 3.1.0
            |info:
            |  title: test additional parameters
            |  version: 1.0.0
            |paths:
            |  /foo:
            |    get:
            |      parameters:
            |        - name: foo
            |          description: query, required
            |          in: query
            |          required: true
            |          schema:
            |            type: string
            |      responses:
            |        '204':
            |          description: empty
        """.trimMargin()
        )

        val options = parseOptions(
            """
            |openapi-processor-mapping: v8
            |
            |options:
            |  package-name: pkg
            | 
            |map:
            |  paths:
            |    /foo:
            |      parameters:
            |      - add: request => javax.servlet.http.HttpServletRequest
            """.trimMargin()
        )

        // act
        val api = apiConverter(options).convert(openApi)

        // assert
        val ep = api.getEndpoint("/foo")
        ep.parameters[0].name shouldBe "foo"
        val request = ep.parameters[1]
        request.name shouldBe "request"
        request.required.shouldBeFalse()
        request.dataType.getName() shouldBe "HttpServletRequest"
        request.dataType.getPackageName() shouldBe "javax.servlet.http"
        !request.withAnnotation
    }
})
