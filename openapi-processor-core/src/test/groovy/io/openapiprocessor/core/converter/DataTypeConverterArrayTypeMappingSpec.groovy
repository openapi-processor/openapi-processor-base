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

class DataTypeConverterArrayTypeMappingSpec extends Specification {

    @Unroll
    void "maps array schema to #responseTypeName via global type mapping" () {
        def options = parseOptionsMapping("""
            |map:
            |  types:
            |    - type: array => $targetTypeName
            """)

        def openApi = parseApiBody ("""
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
        Api api = apiConverter (options, Stub (Framework)).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.typeName == responseTypeName

        where:
        targetTypeName         | responseTypeName
        'java.util.Collection' | 'Collection<String>'
        'java.util.List'       | 'List<String>'
        'java.util.Set'        | 'Set<String>'
    }

    void "throws when there are multiple global mappings for the array type" () {
        def options = parseOptionsMapping("""
            |map:
            |  types:
            |    - type: array => java.util.Collection
            |    - type: array => java.util.Collection
            """)

        def openApi = parseApiBody ("""
            paths:
              /page:
                get:
                  parameters:
                    - in: query
                      name: date
                      required: false
                      schema:
                        type: array
                        items: 
                          type: string
                  responses:
                    '204':
                      description: none
            """)

        when:
        apiConverter (options, Stub (Framework)).convert (openApi)

        then:
        def e = thrown (AmbiguousTypeMappingException)
        e.typeMappings.size() == 2
    }

    @Unroll
    void "throws when there are multiple mappings on the same level: #type" () {
        def options = parseOptionsMapping(mappings)

        def openApi = parseApiBody ("""
            paths:
              /foo:
                get:
                  parameters:
                    - in: query
                      name: date
                      required: false
                      schema:
                        type: array
                        items: 
                          type: string
                  responses:
                    '204':
                      description: none
            """)

        when:
        apiConverter (options, Stub (Framework)).convert (openApi)

        then:
        thrown (AmbiguousTypeMappingException)

        where:
        type << [
            'global type mappings',
            'global parameter/response mappings',
            'endpoint type mappings'
        ]

        mappings << [
            """
            |map:
            |  types:
            |    - type: array => java.util.Collection
            |    - type: array => java.util.Collection
            """,
            """
            |map:
            |  parameters:
            |    - type: array => java.util.Collection
            |    - type: array => java.util.Collection
            """,
            """
            |map:
            |  paths:
            |    /foo:
            |      types:
            |        - type: array => java.util.Collection
            |        - type: array => java.util.Collection
            """
        ]
    }

    void "converts array response schema to #responseTypeName via endpoint type mapping" () {
        def options = parseOptionsMapping("""
            |map:
            |  paths:
            |    /foo:
            |      types:
            |        - type: array => $targetTypeName
            """)

        def openApi = parseApiBody ("""
            paths:
              /foo:
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
        Api api = apiConverter(options, Stub (Framework)).convert (openApi)

        then:
        def itf = api.interfaces.first()
        def ep = itf.endpoints.first()
        def rsp = ep.getFirstResponse('200')
        rsp.responseType.typeName == responseTypeName
        rsp.responseType.packageName == 'java.util'

        where:
        targetTypeName         | responseTypeName
        'java.util.Collection' | 'Collection<String>'
        'java.util.List'       | 'List<String>'
        'java.util.Set'        | 'Set<String>'
    }

    @Unroll
    void "converts array parameter schema to java type via #type" () {
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
                        type: array
                        items:
                          type: string
                  responses:
                    '204':
                      description: empty
            """)

        when:
        Api api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first()
        def ep = itf.endpoints.first()
        def p = ep.parameters.first()
        p.dataType.typeName == 'Collection<String>'
        p.dataType.packageName == 'java.util'

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
            |        - name: foobar => java.util.Collection
            """,
            """
            |map:
            |  parameters:
            |    - name: foobar => java.util.Collection
            """
        ]
    }

    @Unroll
    void "converts array response schema to Collection<> via #type" () {
        def options = parseOptionsMapping(mappings)

        def openApi = parseApiBody ("""
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
        Api api = apiConverter (options, Stub (Framework)).convert (openApi)

        then:
        def itf = api.interfaces.first()
        def ep = itf.endpoints.first()
        def rsp = ep.getFirstResponse('200')
        rsp.responseType.typeName == 'Collection<String>'
        rsp.responseType.imports == ['java.util.Collection', 'java.lang.String'] as Set

        where:
        type << [
            'endpoint response mapping',
            'global response mapping',
            'endpoint response mapping over endpoint type mapping',
            'endpoint type mapping'
        ]

        mappings << [
            """
            |map:
            |  paths:
            |    /array-string:
            |      responses:
            |        - content: application/vnd.any => java.util.Collection
            """,
            """
            |map:
            |  responses:
            |    - content: application/vnd.any => java.util.Collection
            """,
            """
            |map:
            |  types:
            |    - type: array => java.util.Set
            |  paths:
            |    /array-string:
            |      responses:
            |        - content: application/vnd.any => java.util.Collection
            """,
            """
            |map:
            |  types:
            |    - type: array => java.util.Collection
            """
        ]
    }
}
