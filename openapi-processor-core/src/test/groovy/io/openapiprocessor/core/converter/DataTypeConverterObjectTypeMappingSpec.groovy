/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter


import io.openapiprocessor.core.converter.mapping.AmbiguousTypeMappingException
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.Api
import spock.lang.Specification
import spock.lang.Unroll

import static io.openapiprocessor.core.support.FactoryHelper.apiConverter
import static io.openapiprocessor.core.support.ApiOptionsKt.parseOptionsMapping
import static io.openapiprocessor.core.support.OpenApiParserKt.parseApiBody

class DataTypeConverterObjectTypeMappingSpec extends Specification {

    void "converts named schemas to java type via global type mapping" () {
        def options = parseOptionsMapping("""
            |map:
            |  types:
            |    - type: Pageable => org.springframework.data.domain.Pageable
            |    - type: StringPage => org.springframework.data.domain.Page<java.lang.String>
            """)

        def openApi = parseApiBody ("""
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
        Api api = apiConverter (options).convert (openApi)

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
        def options = parseOptionsMapping("""
            |map:
            |  types:
            |    - type: Pageable => org.springframework.data.domain.Pageable
            |    - type: Pageable => org.springframework.data.domain.Pageable
            """)

        def openApi = parseApiBody ("""
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
        apiConverter (options, Stub (Framework)).convert (openApi)

        then:
        def e = thrown (AmbiguousTypeMappingException)
        e.typeMappings.size() == 2
    }

    void "converts named schemas to java type via endpoint type mapping" () {
        def options = parseOptionsMapping("""
            |map:
            |  paths:
            |    /foobar:
            |      types:
            |        - type: Foo => someA.ObjectA
            |        - type: Bar => someB.ObjectB
            """)

        def openApi = parseApiBody ("""
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
        Api api = apiConverter (options).convert (openApi)

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
        def options = parseOptionsMapping(mappings)

        def openApi = parseApiBody ("""
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
        Api api = apiConverter (options).convert (openApi)

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
            """
            |map:
            |  paths:
            |    /foobar:
            |      parameters:
            |        - name: foobar => pkg.TargetClass
            """,
            """
            |map:
            |  parameters:
            |    - name: foobar => pkg.TargetClass
            """
        ]
    }

    @Unroll
    void "converts object response schema to java type via #type" () {
        def options = parseOptionsMapping(mappings)

        def openApi = parseApiBody ("""
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
        Api api = apiConverter (options, Stub (Framework)).convert (openApi)

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
            'endpoint response mapping',
            'endpoint type mapping'
        ]

        mappings << [
            """
            |map:
            |  paths:
            |    /object:
            |      responses:
            |        - content: application/vnd.any => pkg.TargetClass<java.lang.String>
            """,
            """
            |map:
            |  responses:
            |    - content: application/vnd.any => pkg.TargetClass<java.lang.String>
            """,
            """
            |map:
            |  paths:
            |    /object:
            |      types:
            |        - type: ObjectGetResponse200 => pkg.TargetClassType<java.lang.String>
            |      responses:
            |        - content: application/vnd.any => pkg.TargetClass<java.lang.String>
            """,
            """
            |map:
            |  paths:
            |    /object:
            |      types:
            |        - type: ObjectGetResponse200 => pkg.TargetClass<java.lang.String>
            """
        ]
    }

    void "converts query param object schema to Map<> set via mapping" () {
        def options = parseOptionsMapping("""
            |map:
            |  paths:
            |    /endpoint-map:
            |      types:
            |        - type: Props => java.util.Map<java.lang.String, java.lang.String>
            """)

        def openApi = parseApiBody ("""
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
        Api api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def p = ep.parameters.first ()
        p.dataType.name == 'Map<String, String>'
    }

    void "converts query param object schema to MultiValueMap<> set via mapping" () {
        def options = parseOptionsMapping("""
            |map:
            |  paths:
            |    /endpoint-map:
            |      types:
            |        - type: Props => org.springframework.util.MultiValueMap<java.lang.String, java.lang.String>
            """)

        def openApi = parseApiBody ("""
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
        Api api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def p = ep.parameters.first ()
        p.dataType.name == 'MultiValueMap<String, String>'
    }
}
