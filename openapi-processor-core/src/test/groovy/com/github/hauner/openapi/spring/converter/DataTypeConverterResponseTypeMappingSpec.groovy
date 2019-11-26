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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.model.Api
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse


class DataTypeConverterResponseTypeMappingSpec extends Specification {

    void "converts simple array response schema to Collection<> via endpoint response type array mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /array-string:
    get:
      responses:
        '200':
          content:
            application/vnd.any:
              schema:
                type: array
                items:
                  type: string
          description: none              
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new EndpointTypeMapping (path: '/array-string',
                    typeMappings: [
                        new ResponseTypeMapping (
                            contentType: 'application/vnd.any',
                            mapping: new TypeMapping (
                                sourceTypeName: 'object',
                                targetTypeName: 'pkg.TargetClass')
                        ),
                        new ResponseTypeMapping (
                            contentType: 'application/vnd.any',
                            mapping: new TypeMapping (
                                sourceTypeName: 'array',
                                targetTypeName: 'java.util.Collection')
                        )
                    ])
                ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.response.responseType.name == 'Collection<String>'
    }

    void "converts simple array response schema to Collection<> via global response type array mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /array-string:
    get:
      responses:
        '200':
          content:
            application/vnd.any:
              schema:
                type: array
                items:
                  type: string
          description: none              
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new ResponseTypeMapping (
                    contentType: 'application/vnd.any',
                    mapping: new TypeMapping(
                        sourceTypeName: 'array',
                        targetTypeName: 'java.util.Collection')
                )
            ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.response.responseType.name == 'Collection<String>'
    }

    void "converts object response schema to java type via endpoint mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /object:
    get:
      responses:
        '200':
          content:
            application/vnd.any:
              schema:
                type: object
                properties:
                  prop:
                    type: string
          description: none              
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new EndpointTypeMapping (path: '/object',
                    typeMappings: [
                         new ResponseTypeMapping (
                             contentType: 'application/vnd.any',
                             mapping: new TypeMapping(
                                 sourceTypeName: 'object',
                                 targetTypeName: 'pkg.TargetClass',
                                 genericTypeNames: ['java.lang.String'])
                         )
                    ])
                ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.response.responseType.name == 'TargetClass<String>'
        ep.response.responseType.imports == ['pkg.TargetClass', 'java.lang.String'] as Set
    }

    void "converts object response schema to java type via global response mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /object:
    get:
      responses:
        '200':
          content:
            application/vnd.any:
              schema:
                type: object
                properties:
                  prop:
                    type: string
          description: none              
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                 new ResponseTypeMapping (
                     contentType: 'application/vnd.any',
                     mapping: new TypeMapping(
                         sourceTypeName: 'object',
                         targetTypeName: 'pkg.TargetClass',
                         genericTypeNames: ['java.lang.String'])
                 )
            ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.response.responseType.name == 'TargetClass<String>'
        ep.response.responseType.imports == ['pkg.TargetClass', 'java.lang.String'] as Set
    }

}
