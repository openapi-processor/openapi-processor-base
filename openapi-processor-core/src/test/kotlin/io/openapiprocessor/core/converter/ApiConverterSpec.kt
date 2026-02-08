/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
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
