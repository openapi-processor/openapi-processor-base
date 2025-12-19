/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.parser.Schema
import io.openapiprocessor.core.support.parseApi

class RefResolverSpec: StringSpec({

    $$"resolves $referenced schemas in API" {
        val openApi = parseApi(
            $$"""
            openapi: 3.0.2
            info:
              title: API
              version: 1.0.0
            
            paths:
              /ref:
                get:
                  responses:
                    '200':
                      description: none
                      content:
                        application/json:
                          schema:
                            $ref: '#/components/schemas/Schema'
            
              /ref2:
                get:
                  responses:
                    '200':
                      description: none
                      content:
                        application/json:
                          schema:
                            $ref: '#/components/schemas/OtherSchema'
            
            components:
              schemas:
            
                Schema:
                  description: none
                  type: object
                  properties:
                    value:
                      type: string
            
                OtherSchema:
                  description: none
                  type: object
                  properties:
                    value:
                      type: string
            """, ParserType.SWAGGER
        )

        val schemaA = mockk<Schema>()
        every { schemaA.getRef() } returns "#/components/schemas/Schema"

        val schemaB = mockk<Schema>()
        every { schemaB.getRef() } returns "#/components/schemas/OtherSchema"

        val resolver = openApi.getRefResolver()
        resolver.resolve(schemaA).shouldNotBeNull()
        resolver.resolve(schemaB).shouldNotBeNull()
    }
})
