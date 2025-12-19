/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.parser.Schema
import spock.lang.Specification

import static io.openapiprocessor.core.support.OpenApiParser.parse

class RefResolverSpec extends Specification {

    void "resolves \$referenced schemas in API" () {
        def openApi = parse ("""\
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
                \$ref: '#/components/schemas/Schema'

  /ref2:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
                \$ref: '#/components/schemas/OtherSchema'

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
        
""", ParserType.SWAGGER)

        def schemaA = Stub (Schema) {
            getRef () >> '#/components/schemas/Schema'
        }

        def schemaB = Stub (Schema) {
            getRef () >> '#/components/schemas/OtherSchema'
        }

        when:
        def resolver = openApi.refResolver
        def schema = resolver.resolve (schemaA)
        def other = resolver.resolve (schemaB)

        then:
        schema
        other
    }
}
