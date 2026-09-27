/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.support.*
import io.openapiprocessor.core.writer.java.JavaIdentifier
import org.slf4j.Logger

class ApiConverterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    "groups endpoints into interfaces by first operation tag" {
        val openApi = parseApiBody("""
            paths:
              /a:
                get:
                  tags:
                    - ping
                  responses:
                    '204':
                      description: none
              /b:
                get:
                  tags:
                    - pong
                  responses:
                    '204':
                      description: none
              /c:
                get:
                  tags:
                    - ping
                    - pong
                  responses:
                    '204':
                      description: none
            """)

        val api: Api = ApiConverter (ApiOptions(), JavaIdentifier(), FrameworkBase())
            .convert(openApi)

        val interfaces = api.getInterfaces()
        interfaces shouldHaveSize 2
        interfaces.map { it.name } shouldContainExactly listOf("Ping", "Pong")

        api.getInterface("Ping").endpoints.map { it.path } shouldContainExactly listOf("/a", "/c")
        api.getInterface("Pong").endpoints.map { it.path } shouldContainExactly listOf("/b")
    }

    "groups endpoints into interfaces by valid tag identifier" {
        val openApi = parseApiBody ("""
            paths:
              /a:
                get:
                  tags:
                    - test_api
                  responses:
                    '204':
                      description: none
              /b:
                get:
                  tags:
                    - test-api
                  responses:
                    '204':
                      description: none
              """)

        val api: Api = ApiConverter (ApiOptions(), JavaIdentifier(), FrameworkBase())
            .convert(openApi)

        val actual = api.getInterfaces()
        actual shouldHaveSize 1
        actual.first().name shouldBe "TestApi"
    }

    "groups endpoints with method #method into interfaces" {
        forAll(
            row("get"),
            row("put"),
            row("post"),
            row("delete"),
            row("options"),
            row("head"),
            row("patch"),
            row("trace")
        ) { method ->
            val openApi = parseApiBody("""
                paths:
                  /a:
                    $method:
                      tags:
                        - $method
                      responses:
                        '204':
                          description: no content
                """)

            val api: Api = ApiConverter (ApiOptions(), JavaIdentifier(), FrameworkBase())
                .convert(openApi)


            val actual = api.getInterfaces()
            actual shouldHaveSize 1
            actual.map { it.name } shouldBe listOf(method.capitalizeFirstChar())

            val itf = actual[0]
            itf.endpoints.map { it.path } shouldContainExactly listOf("/a")
        }
    }

    "generates model if it is only referenced in the mapping" {
        val options = parseOptionsMapping("""
            |mapping:
            |  types:
            |    - type: WrappedFoo => io.openapiprocessor.test.Wrapped<io.openapiprocessor.generated.model.Foo>
            """)

        val openApi = parseApiBody ($$"""
            paths:
              /foo:
                get:
                  responses:
                    '200':
                      description: ...
                      content:
                        application/json:
                          schema:
                            $ref: '#/components/schemas/WrappedFoo'
            
            components:
              schemas:
                        
                WrappedFoo:
                  description: ...
                  type: array
                  items: 
                    $ref: '#/components/schemas/Foo'
            
                Foo:
                  description: a Foo
                  type: object
                  properties:
                    foo:
                      type: string
            """)

        val api: Api = ApiConverter (options, JavaIdentifier(), FrameworkBase())
            .convert(openApi)

        api.getDataTypes().getModelDataTypes().size shouldBe 1
     }

    "generates model if it is only referenced in the mapping of a composed type" {
        val options = parseOptionsMapping("""
            |mapping:
            |  types:
            |    - type: ComposedFoo => io.openapiprocessor.test.Wrapped<io.openapiprocessor.generated.model.Foo>
            """)

        val openApi = parseApiBody($$"""
            paths:
              /foo:
                get:
                  responses:
                    '200':
                      description: ...
                      content:
                        application/json:
                          schema:
                            $ref: '#/components/schemas/ComposedFoo'
            
            components:
              schemas:
                        
                ComposedFoo:
                  description: ...
                  allOf:
                    - $ref: '#/components/schemas/Foo'
            
                Foo:
                  description: a Foo
                  type: object
                  properties:
                    foo:
                      type: string
            """)

        val api: Api = ApiConverter (options, JavaIdentifier(), FrameworkBase())
            .convert(openApi)

        api.getDataTypes().getModelDataTypes().size shouldBe 1
     }

    "creates 'Excluded' interface when an endpoint should be skipped" {
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
            |      exclude: true
            """.trimMargin())

        val openApi = parseApiBody("""
           paths:
             /foo:
               get:
                 responses:
                   '204':
                     description: no content
           
             /bar:
               get:
                 responses:
                   '204':
                     description: no content
           """)

        // act
        val api = apiConverter(options).convert(openApi)

        // assert
        val itfs = api.getInterfaces()
        itfs.shouldHaveSize(2)
        itfs[0].getInterfaceName() shouldBe "Api"
        itfs[1].getInterfaceName() shouldBe "ExcludedApi"
    }

    "warns if endpoint path has no success response" {
        val options = parseOptions()
        val openApi = parseApiBody("""
           paths:
             /foo:
               get:
                 responses:
                   '400':
                     description: error 400
                     content:
                       plain/text:
                         schema:
                           type: string
                   '401':
                     description: error 401
                     content:
                       plain/text:
                         schema:
                           type: string
            """)

        val converter = apiConverter(options)
        val log: Logger = mockk(relaxed = true)
        converter.log = log

        converter.convert(openApi)

        verify { log.warn("endpoint '/foo' has no success 1xx/2xx/3xx response.") }
    }

    "generates unreferenced models" {
        val openApi = parseApiBody("""
            paths: {}
            
            components:
              schemas:
                Foo:
                  description: unreferenced
                  type: object
                  properties:
                    foo:
                      type: string
            """)

        val options = parseOptions(
            """
            |openapi-processor-mapping: v15
            |
            |options:
            |  package-name: pkg
            |  model-unreferenced: true  
            """.trimMargin())

        // act
        val api = apiConverter(options).convert(openApi)

        // assert
        api.getDataTypes().getModelDataTypes() shouldHaveSize 1
    }
})
