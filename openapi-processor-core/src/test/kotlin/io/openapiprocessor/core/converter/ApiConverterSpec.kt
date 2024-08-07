/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.model.Api
import io.openapiprocessor.core.support.apiConverter
import io.openapiprocessor.core.support.parse
import io.openapiprocessor.core.support.parseApi
import io.openapiprocessor.core.support.parseOptions
import io.openapiprocessor.core.writer.java.JavaIdentifier

class ApiConverterSpec: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    "generates model if it is only referenced in the mapping" {
         val openApi = parse ("""
            openapi: 3.0.2
            info:
              title: API
              version: 1.0.0
            
            paths:
              /foo:
                get:
                  responses:
                    '200':
                      description: ...
                      content:
                        application/json:
                          schema:
                            ${'$'}ref: '#/components/schemas/WrappedFoo'
            
            components:
              schemas:
                        
                WrappedFoo:
                  description: ...
                  type: array
                  items: 
                    ${'$'}ref: '#/components/schemas/Foo'
            
                Foo:
                  description: a Foo
                  type: object
                  properties:
                    foo:
                      type: string
                  
         """.trimIndent())

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping(
                "WrappedFoo",
                null,
                "io.openapiprocessor.test.Wrapped",
                listOf(TargetType ("io.openapiprocessor.generated.model.Foo"))
            )
        )

        val api: Api = ApiConverter (options, JavaIdentifier(), FrameworkBase())
            .convert(openApi)

        api.getDataTypes().getModelDataTypes().size shouldBe 1
     }

    "generates model if it is only referenced in the mapping of a composed type" {
         val openApi = parse ("""
            openapi: 3.0.2
            info:
              title: API
              version: 1.0.0
            
            paths:
              /foo:
                get:
                  responses:
                    '200':
                      description: ...
                      content:
                        application/json:
                          schema:
                            ${'$'}ref: '#/components/schemas/ComposedFoo'
            
            components:
              schemas:
                        
                ComposedFoo:
                  description: ...
                  allOf:
                    - ${'$'}ref: '#/components/schemas/Foo'
            
                Foo:
                  description: a Foo
                  type: object
                  properties:
                    foo:
                      type: string
                  
         """.trimIndent())

        val options = ApiOptions()
        options.typeMappings = listOf(
            TypeMapping(
                "ComposedFoo",
                null,
                "io.openapiprocessor.test.Wrapped",
                listOf(TargetType("io.openapiprocessor.generated.model.Foo"))
            )
        )

        val api: Api = ApiConverter (options, JavaIdentifier(), FrameworkBase())
            .convert(openApi)

        api.getDataTypes().getModelDataTypes().size shouldBe 1
     }

    "creates 'Excluded' interface when an endpoint should be skipped" {
        val openApi = parseApi(
            """
            |openapi: 3.1.0
            |info:
            |  title: API
            |  version: 1.0.0
            |
            |paths:
            |  /foo:
            |    get:
            |      responses:
            |        '204':
            |          description: no content
            |
            |  /bar:
            |    get:
            |      responses:
            |        '204':
            |          description: no content
            """.trimMargin())

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

        // act
        val api = apiConverter(options).convert(openApi)

        // assert
        val itfs = api.getInterfaces()
        itfs.shouldHaveSize(2)
        itfs[0].getInterfaceName() shouldBe "Api"
        itfs[1].getInterfaceName() shouldBe "ExcludedApi"
    }
})
