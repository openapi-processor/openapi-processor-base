/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import io.openapiprocessor.core.support.parseApi

class ContentTypeResponseCollectorSpec: StringSpec({

    "single empty success response" {
        val openApi = parseApi(
            """
            |openapi: 3.1.0
            |info:
            |  title: responses
            |  version: 1.0.0
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '204':
            |          description: empty
        """.trimMargin()
        )

        val operation = openApi.getPaths()["/foo"]!!.getOperations().first()

        val collector = ContentTypeResponseCollector(operation.getResponses(), ResultStyle.SUCCESS)

        collector.contentTypeResponses[""]!!["204"].shouldNotBeNull()
    }

    "single success response" {
        val openApi = parseApi(
            """
            |openapi: 3.1.0
            |info:
            |  title: responses
            |  version: 1.0.0
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '200':
            |          description: responses
            |          content:
            |            application/json:
            |                schema:
            |                  type: string
        """.trimMargin()
        )

        val operation = openApi.getPaths()["/foo"]!!.getOperations().first()

        val collector = ContentTypeResponseCollector(operation.getResponses(), ResultStyle.SUCCESS)

        collector.contentTypeResponses["application/json"]!!["200"].shouldNotBeNull()
    }

    "multiple success responses without marker interface" {
        val openApi = parseApi(
            """
            |openapi: 3.1.0
            |info:
            |  title: responses
            |  version: 1.0.0
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '200':
            |          description: ok
            |          content:
            |            application/json:
            |                schema:
            |                  type: string
            |        '201':
            |          description: created
            |          content:
            |            application/json:
            |                schema:
            |                  type: string
        """.trimMargin()
        )

        val operation = openApi.getPaths()["/foo"]!!.getOperations().first()

        val collector = ContentTypeResponseCollector(operation.getResponses(), ResultStyle.SUCCESS)

        collector.contentTypeResponses["application/json"]!!["200"].shouldNotBeNull()
        collector.contentTypeResponses["application/json"]!!["201"].shouldNotBeNull()
    }

    "multiple success responses with marker interface" {
        val openApi = parseApi(
            """
            |openapi: 3.1.0
            |info:
            |  title: responses
            |  version: 1.0.0
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '200':
            |          description: ok
            |          content:
            |            application/json:
            |                schema:
            |                  type: string
            |                  format: one
            |        '201':
            |          description: created
            |          content:
            |            application/json:
            |                schema:
            |                  type: string
            |                  format: two
        """.trimMargin()
        )

        val operation = openApi.getPaths()["/foo"]!!.getOperations().first()

        val collector = ContentTypeResponseCollector(operation.getResponses(), ResultStyle.SUCCESS)

        collector.contentTypeResponses["application/json"]!!["200"].shouldNotBeNull()
        collector.contentTypeResponses["application/json"]!!["201"].shouldNotBeNull()
    }

    "multiple content type success responses with marker interface and error" {
        val openApi = parseApi(
            """
            |openapi: 3.1.0
            |info:
            |  title: responses
            |  version: 1.0.0
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '200':
            |          description: ok
            |          content:
            |            application/json:
            |                schema:
            |                  type: string
            |                  format: one
            |            text/plain:
            |                schema:
            |                  type: string
            |        '201':
            |          description: created
            |          content:
            |            application/json:
            |                schema:
            |                  type: string
            |                  format: two
            |        '400':
            |          content:
            |            application/json:
            |                schema:
            |                  type: string
            |                  format: error
            | 
        """.trimMargin()
        )

        val operation = openApi.getPaths()["/foo"]!!.getOperations().first()

        val collectorA = ContentTypeResponseCollector(operation.getResponses(), ResultStyle.ALL)
        collectorA.contentTypeResponses["application/json"]!!["200"].shouldNotBeNull()
        collectorA.contentTypeResponses["application/json"]!!["201"].shouldNotBeNull()
        collectorA.contentTypeResponses["application/json"]!!["400"].shouldNotBeNull()

        val collectorS = ContentTypeResponseCollector(operation.getResponses(), ResultStyle.SUCCESS)
        collectorS.contentTypeResponses["application/json"]!!["200"].shouldNotBeNull()
        collectorS.contentTypeResponses["application/json"]!!["201"].shouldNotBeNull()
        collectorS.contentTypeResponses["application/json"]!!["400"].shouldBeNull()
    }
})
