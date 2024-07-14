/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.mockk
import io.openapiprocessor.core.support.apiConverter
import io.openapiprocessor.core.support.parseApi
import io.openapiprocessor.core.support.parseOptions

class ApiConverterRequestBodySpec: StringSpec({

    "converts request body parameter" {
        val openApi = parseApi(body =
            """
            |paths:
            |  /endpoint:
            |    get:
            |      tags:
            |        - endpoint
            |      requestBody:
            |        content:
            |          application/json:
            |            schema:
            |              type: object
            |              properties:
            |                foo:
            |                  type: string
            |      responses:
            |        '204':
            |          description: empty
            """)

        val api = apiConverter().convert(openApi)

        val itf = api.getInterfaces().first()
        val ep = itf.endpoints.first()
        val body = ep.requestBodies.first()

        body.contentType shouldBe "application/json"
        body.dataType.getName() shouldBe "EndpointGetRequestBody"
        body.required.shouldBeFalse()
    }

    "converts request body multipart/form-data object schema properties to request parameters" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /multipart/single-file:
            |      types:
            |        - type: string:binary => multipart.Multipart
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /multipart/single-file:
            |    post:
            |      requestBody:
            |        required: true
            |        content:
            |          multipart/form-data:
            |            schema:
            |              type: object
            |              properties:
            |                file:
            |                  type: string
            |                  format: binary
            |                other:
            |                  type: string
            |      responses:
            |        '204':
            |          description: empty
            """)

        val api = apiConverter(options).convert(openApi)

        val itf = api.getInterfaces().first()
        val ep = itf.endpoints.first()
        val file = ep.parameters[0]
        val other = ep.parameters[1]

        file.name shouldBe "file"
        file.required.shouldBeTrue()
        file.dataType.getName() shouldBe "Multipart"
        file.dataType.getImports() shouldContainExactly (setOf("multipart.Multipart"))

        other.name shouldBe "other"
        other.required.shouldBeTrue()
        other.dataType.getTypeName() shouldBe "String"
    }

    "throws when request body multipart/form-data schema is not an object schema" {
        val openApi = parseApi(body =
            """
            |paths:
            |  /multipart/broken:
            |    post:
            |      requestBody:
            |        required: true
            |        content:
            |          multipart/form-data:
            |            schema:
            |              type: string
            |      responses:
            |        '204':
            |          description: empty
            """)

        val e = shouldThrow<MultipartResponseBodyException> {
            apiConverter(framework = mockk()).convert(openApi)
        }

        e.message shouldContain "/multipart/broken"
    }

    "does not register the object data type of a request body multipart/form-data schema to avoid model creation" {
        val openApi = parseApi(body =
            """
            |paths:
            |  /multipart/single-file:
            |    post:
            |      requestBody:
            |        required: true
            |        content:
            |          multipart/form-data:
            |            schema:
            |              type: object
            |              properties:
            |                file:
            |                  type: string
            |                  format: binary
            |                other:
            |                  type: string
            |      responses:
            |        '204':
            |          description: empty
            """)

        val api = apiConverter(framework = mockk(relaxed = true)).convert(openApi)

        api.getDataTypes().getModelDataTypes().shouldBeEmpty()
    }

    "converts request body multipart/* object" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /multipart:
            |       types:
            |         - type: string:binary => multipart.Multipart
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /multipart:
            |    post:
            |      requestBody:
            |        required: true
            |        content:
            |          multipart/form-data:
            |            schema:
            |              type: object
            |              properties:
            |                file:
            |                  type: string
            |                  format: binary
            |                json:
            |                  type: object
            |                  properties:
            |                    foo:
            |                      type: string
            |                    bar:
            |                      type: string
            |            encoding:
            |              file:
            |                contentType: application/octet-stream
            |              json:
            |                contentType: application/json
            |      responses:
            |        '204':
            |          description: empty
            """)

        val api = apiConverter(options).convert(openApi)

        val itf = api.getInterfaces().first ()
        val ep = itf.endpoints.first ()
        val file = ep.parameters[0]
        val json = ep.parameters[1]

        file.name shouldBe "file"
        file.required.shouldBeTrue()
        file.dataType.getName() shouldBe "Multipart"
        file.dataType.getImports() shouldContainExactly setOf("multipart.Multipart")

        json.name shouldBe "json"
        json.required.shouldBeTrue()
        json.dataType.getName() shouldBe "MultipartPostRequestBodyJson"
        json.dataType.getImports() shouldContainExactly setOf("pkg.model.MultipartPostRequestBodyJson")
    }

    "add refs request body multipart/* objects" {
        val options = parseOptions(mapping =
            """
            |map:
            |  paths:
            |    /multipart:
            |       types:
            |         - type: string:binary => multipart.Multipart
            """)

        val openApi = parseApi(body =
            """
            |paths:
            |  /multipart:
            |    post:
            |      requestBody:
            |        required: true
            |        content:
            |          multipart/form-data:
            |            schema:
            |              type: object
            |              properties:
            |                file:
            |                  type: string
            |                  format: binary
            |                json:
            |                  type: object
            |                  properties:
            |                    foo:
            |                      ${'$'}ref: '#/components/schemas/Foo'
            |                    bar:
            |                      type: string
            |            encoding:
            |              file:
            |                contentType: application/octet-stream
            |              json:
            |                contentType: application/json
            |      responses:
            |        '204':
            |          description: empty
            |
            |components:
            |  schemas:
            |    Foo:
            |      type: object
            |      properties:
            |        foo:
            |          type: string
            """)

        val api = apiConverter(options).convert(openApi)

        val dts = api.getDataTypes()
        dts.find("Foo").shouldNotBeNull()
        dts.find("MultipartPostRequestBodyJson").shouldNotBeNull()
        dts.find("MultipartPostRequestBody").shouldBeNull()
    }
})
