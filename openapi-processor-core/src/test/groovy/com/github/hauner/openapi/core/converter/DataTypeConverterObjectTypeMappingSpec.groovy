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

package com.github.hauner.openapi.core.converter

import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.mapping.AmbiguousTypeMappingException
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.converter.mapping.NameTypeMapping
import io.openapiprocessor.core.converter.mapping.ContentTypeMapping
import io.openapiprocessor.core.converter.mapping.TargetType
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.model.Api
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.core.test.FactoryHelper.apiConverter
import static com.github.hauner.openapi.core.test.OpenApiParser.parse

class DataTypeConverterObjectTypeMappingSpec extends Specification {

    void "converts named schemas to java type via global type mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /page:
    get:
      parameters:
        - in: query
          name: pageable
          required: false
          schema:
            \$ref: '#/components/schemas/Pageable'
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
                \$ref: '#/components/schemas/StringPage'

components:
  schemas:

    Pageable:
      description: minimal Pageable query parameters
      type: object
      properties:
        page:
          type: integer
        size:
          type: integer

    Page:
      description: minimal Page response without content property
      type: object
      properties:
        number:
          type: integer
        size:
          type: integer

    StringContent:
      description: specific content List of the Page response
      type: object
      properties:
        content:
          type: array
          items:
            type: string

    StringPage:
      description: typed Page
      type: object
      allOf:
        - \$ref: '#/components/schemas/Page'
        - \$ref: '#/components/schemas/StringContent'
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new TypeMapping (
                    'Pageable',
                    'org.springframework.data.domain.Pageable'),
                new TypeMapping (
                    'StringPage',
                    null,
                    'org.springframework.data.domain.Page',
                    [new TargetType ('java.lang.String', [])],
                    false, false)
            ])

        Api api = apiConverter (options)
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        def response = ep.getFirstResponse ('200')
        parameter.dataType.packageName == 'org.springframework.data.domain'
        parameter.dataType.name == 'Pageable'
        response.responseType.packageName == 'org.springframework.data.domain'
        response.responseType.name == 'Page<String>'
    }

    void "throws when there are multiple global mappings for a named schema" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /page:
    get:
      parameters:
        - in: query
          name: pageable
          required: false
          schema:
            \$ref: '#/components/schemas/Pageable'
      responses:
        '204':
          description: none

components:
  schemas:

    Pageable:
      description: minimal Pageable query parameters
      type: object
      properties:
        page:
          type: integer
        size:
          type: integer
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new TypeMapping (
                    'Pageable',
                    'org.springframework.data.domain.Pageable'),
                new TypeMapping (
                    'Pageable',
                    'org.springframework.data.domain.Pageable')
            ])
        apiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        def e = thrown (AmbiguousTypeMappingException)
        e.typeMappings == options.typeMappings
    }

    void "converts named schemas to java type via endpoint type mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foobar:
    get:
      parameters:
        - in: query
          name: foo
          required: false
          schema:
            \$ref: '#/components/schemas/Foo'
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
                \$ref: '#/components/schemas/Bar'

components:
  schemas:

    Foo:
      description: minimal query parameter object
      type: object
      properties:
        foo:
          type: string

    Bar:
      description: minimal response object
      type: object
      properties:
        bar:
          type: string

""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new EndpointTypeMapping ('/foobar', null, [
                        new TypeMapping (
                            'Foo',
                            'someA.ObjectA'),
                        new TypeMapping (
                            'Bar',
                            'someB.ObjectB')])
            ])

        Api api = apiConverter (options)
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        def response = ep.getFirstResponse ('200')
        parameter.dataType.packageName == 'someA'
        parameter.dataType.name == 'ObjectA'
        response.responseType.packageName == 'someB'
        response.responseType.name == 'ObjectB'
    }

    @Unroll
    void "converts object parameter schema to java type via #type" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foobar:
    get:
      parameters:
        - in: query
          name: foobar
          required: false
          schema:
            type: object
            properties:
              foo:
                type: integer
              bar:
                type: integer
      responses:
        '204':
          description: empty
""")

        when:
        def options = new ApiOptions(packageName: 'pkg', typeMappings: mappings)

        Api api = apiConverter (options)
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.parameters.first ().dataType.name == 'TargetClass'

        where:
        type << [
            'endpoint parameter mapping',
            'global parameter mapping'
        ]

        mappings << [
            [
                new EndpointTypeMapping ('/foobar', null, [
                        new NameTypeMapping (
                            'foobar', new TypeMapping (
                                null,
                                'pkg.TargetClass')
                        )
                    ])
            ], [
                new NameTypeMapping (
                    'foobar', new TypeMapping (
                        null,
                        'pkg.TargetClass')
                )
            ]
        ]
    }

    @Unroll
    void "converts object response schema to java type via #type" () {
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
            typeMappings: mappings)

        Api api = apiConverter (options, Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == 'TargetClass<String>'
        rsp.responseType.imports == ['pkg.TargetClass', 'java.lang.String'] as Set

        where:
        type << [
            'endpoint response mapping',
            'global response mapping',
            'endpoint response mapping over endpoint type mapping',
            'endpoint type mapping'
        ]

        mappings << [
            [
                new EndpointTypeMapping ('/object', null, [
                    new ContentTypeMapping (
                        'application/vnd.any', new TypeMapping (
                        'object',
                        null,
                        'pkg.TargetClass',
                        [new TargetType ('java.lang.String', [])],
                        false, false)
                    )]
                )
            ], [
                new ContentTypeMapping (
                    'application/vnd.any', new TypeMapping (
                        'object',
                        null,
                        'pkg.TargetClass',
                        [new TargetType ('java.lang.String', [])],
                        false, false)
                )
            ], [
                new EndpointTypeMapping ('/object', null, [
                    new ContentTypeMapping (
                        'application/vnd.any', new TypeMapping (
                        'object',
                        null,
                        'pkg.TargetClass',
                        [new TargetType ('java.lang.String', [])],
                        false, false)),
                    new TypeMapping (
                        'ObjectGetResponse200',
                        null,
                        'pkg.TargetClassType',
                        [new TargetType ('java.lang.StringType', [])],
                        false, false)
                ])
            ], [
                new EndpointTypeMapping ('/object', null, [
                    new TypeMapping (
                        'ObjectGetResponse200',
                        null,
                        'pkg.TargetClass',
                        [new TargetType ('java.lang.String', [])],
                        false, false)
                    ]
                )
            ]
        ]
    }

    void "converts query param object schema to Map<> set via mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /endpoint-map:
    get:
      parameters:
        - name: props
          description: query, map from single property
          in: query
          required: false
          schema:
            \$ref: '#/components/schemas/Props'
      responses:
        '204':
          description: empty
          
components:

  schemas:

    Props:
      type: object
      properties:
        prop1:
          type: string
        prop2:
          type: string
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new EndpointTypeMapping ('/endpoint-map', null, [
                    new TypeMapping (
                        'Props',
                        null,
                        'java.util.Map',
                        [
                            new TargetType('java.lang.String', []),
                            new TargetType('java.lang.String', [])
                        ],
                        false,
                        false)
                ])
            ])

        Api api = apiConverter (options)
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def p = ep.parameters.first ()
        p.dataType.name == 'Map<String, String>'
    }

    void "converts query param object schema to MultiValueMap<> set via mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /endpoint-map:
    get:
      parameters:
        - name: props
          description: query, map from single property
          in: query
          required: false
          schema:
            \$ref: '#/components/schemas/Props'
      responses:
        '204':
          description: empty
          
components:

  schemas:

    Props:
      type: object
      properties:
        prop1:
          type: string
        prop2:
          type: string
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new EndpointTypeMapping ('/endpoint-map', null, [
                    new TypeMapping (
                        'Props',
                        null,
                        'org.springframework.util.MultiValueMap',
                    [
                        new TargetType('java.lang.String', []),
                        new TargetType('java.lang.String', [])
                    ],
                    false,
                    false)
                ])
            ])

        Api api = apiConverter (options)
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def p = ep.parameters.first ()
        p.dataType.name == 'MultiValueMap<String, String>'
    }

}
