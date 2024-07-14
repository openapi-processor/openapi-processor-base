/*
 * Copyright 2019 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.parser.Schema
import spock.lang.Specification

import static com.github.hauner.openapi.core.test.OpenApiParser.parse

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
