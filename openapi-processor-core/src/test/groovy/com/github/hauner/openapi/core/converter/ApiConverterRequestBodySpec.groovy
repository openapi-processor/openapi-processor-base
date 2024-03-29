/*
 * Copyright 2019-2020 the original authors
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

package com.github.hauner.openapi.core.converter


import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.MultipartResponseBodyException
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.parser.ParserType
import spock.lang.Specification

import static com.github.hauner.openapi.core.test.FactoryHelper.apiConverter
import static com.github.hauner.openapi.core.test.OpenApiParser.parse

class ApiConverterRequestBodySpec extends Specification {

    void "converts request body parameter"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test request body parameter
  version: 1.0.0

paths:
  /endpoint:

    get:
      tags:
        - endpoint
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                foo:
                  type: string
      responses:
        '204':
          description: empty
""")

        when:
        def api = apiConverter ()
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def body = ep.requestBodies.first ()
        body.contentType == 'application/json'
        body.dataType.name == 'EndpointGetRequestBody'
        !body.required
    }

    void "converts request body multipart/form-data object schema properties to request parameters" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: params-request-body-multipart-form-data
  version: 1.0.0

paths:
  /multipart/single-file:
    post:
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                other:
                  type: string
      responses:
        '204':
          description: empty
"""
        )

        def options = new ApiOptions(packageName: 'pkg', typeMappings: [
            new EndpointTypeMapping('/multipart/single-file', null, [
                new TypeMapping (
                    'string',
                    'binary',
                    'multipart.Multipart')
            ])
        ])

        when:
        def api = apiConverter (options)
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def file = ep.parameters[0]
        def other = ep.parameters[1]

        file.name == 'file'
        file.required
        file.dataType.name == 'Multipart'
        file.dataType.imports == ['multipart.Multipart'] as Set

        other.name == 'other'
        other.required
        other.dataType.typeName == 'String'
    }

    void "throws when request body multipart/form-data schema is not an object schema" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: params-request-body-multipart-form-data
  version: 1.0.0

paths:
  /multipart/broken:
    post:
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: string
      responses:
        '204':
          description: empty
"""
        )

        when:
        apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        def e = thrown(MultipartResponseBodyException)
        e.path == '/multipart/broken'
    }

    void "does not register the object data type of a request body multipart/form-data schema to avoid model creation" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: params-request-body-multipart-form-data
  version: 1.0.0

paths:
  /multipart/single-file:
    post:
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                other:
                  type: string
      responses:
        '204':
          description: empty
"""
        )

        when:
        def api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        api.dataTypes.modelDataTypes.empty
    }

    void "converts request body multipart/* object" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: params-request-body-multipart
  version: 1.0.0

paths:
  /multipart:
    post:
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                json:
                  type: object
                  properties:
                    foo:
                      type: string
                    bar:
                      type: string
            encoding:
              file:
                contentType: application/octet-stream
              json:
                contentType: application/json          
      responses:
        '204':
          description: empty
""", ParserType.OPENAPI4J
        )

        def options = new ApiOptions(packageName: 'pkg', typeMappings: [
            new EndpointTypeMapping('/multipart', null, [
                new TypeMapping (
                    'string',
                    'binary',
                    'multipart.Multipart')
            ])
        ])

        when:
        def api = apiConverter (options)
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def file = ep.parameters[0]
        def json = ep.parameters[1]

        file.name == 'file'
        file.required
        file.dataType.name == 'Multipart'
        file.dataType.imports == ['multipart.Multipart'] as Set

        json.name == 'json'
        json.required
        json.dataType.name == 'MultipartPostRequestBodyJson'
        json.dataType.imports == ['pkg.model.MultipartPostRequestBodyJson'] as Set
    }

    void "add refs request body multipart/* objects" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: params-request-body-multipart
  version: 1.0.0

paths:
  /multipart:
    post:
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                json:
                  type: object
                  properties:
                    foo:
                      \$ref: '#/components/schemas/Foo'
                    bar:
                      type: string
            encoding:
              file:
                contentType: application/octet-stream
              json:
                contentType: application/json          
      responses:
        '204':
          description: empty
          
components:
  schemas:
    Foo:
      type: object
      properties:
        foo:
          type: string
""", ParserType.OPENAPI4J
        )

        def options = new ApiOptions(packageName: 'pkg', typeMappings: [
            new EndpointTypeMapping('/multipart', null, [
                new TypeMapping (
                    'string',
                    'binary',
                    'multipart.Multipart')
            ])
        ])

        when:
        def api = apiConverter (options)
            .convert (openApi)

        then:
        def dts = api.dataTypes
        dts.find ("Foo")
        dts.find ("MultipartPostRequestBodyJson")
        dts.find ("MultipartPostRequestBody") == null
    }
}
