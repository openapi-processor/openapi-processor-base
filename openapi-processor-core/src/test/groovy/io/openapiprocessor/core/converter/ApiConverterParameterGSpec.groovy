/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.UnknownParameterTypeException
import spock.lang.Ignore
import spock.lang.Specification

import static io.openapiprocessor.core.support.FactoryHelper.apiConverter
import static io.openapiprocessor.core.support.OpenApiParserKt.parseApiBody

class ApiConverterParameterGSpec extends Specification {

    void "converts simple query parameter"() {
        def openApi = parseApiBody (
"""\
paths:
  /endpoint:

    get:
      tags:
        - endpoint
      parameters:
        - name: foo
          description: query, required, string
          in: query
          required: true
          schema:
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
        def param = ep.parameters.first ()
        param.name == 'foo'
        param.required
        param.dataType.typeName == 'String'
    }

    void "converts simple path parameter"() {
        def openApi = parseApiBody (
"""\
paths:
  /endpoint/{foo}:

    get:
      tags:
        - endpoint
      parameters:
        - name: foo
          description: path, required, string
          in: path
          required: true
          schema:
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
        def param = ep.parameters.first ()
        param.name == 'foo'
        param.required
        param.dataType.typeName == 'String'
    }

    void "converts simple header parameter"() {
        def openApi = parseApiBody (
"""\
paths:
  /endpoint:

    get:
      tags:
        - endpoint
      parameters:
        - name: x-foo
          description: header, required, string
          in: header
          required: true
          schema:
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
        def param = ep.parameters.first ()
        param.name == 'x-foo'
        param.required
        param.dataType.typeName == 'String'
    }

    void "converts simple cookie parameter"() {
        def openApi = parseApiBody ("""
            paths:
              /endpoint:
            
                get:
                  tags:
                    - endpoint
                  parameters:
                    - name: foo
                      description: cookie, required, string
                      in: cookie
                      required: true
                      schema:
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
        def param = ep.parameters.first ()
        param.name == 'foo'
        param.required
        param.dataType.typeName == 'String'
    }

    @Ignore("the openapi parser ignores parameters with unknown types")
    void "throws on unknown parameter"() {
        def openApi = parseApiBody ("""
            paths:
              /endpoint:
            
                get:
                  tags:
                    - endpoint
                  parameters:
                    - name: foo
                      description: unknown, required, string
                      in: unknown
                      schema:
                        type: string
                  responses:
                    '204':
                      description: empty
            """)

        when:
        apiConverter ()
            .convert (openApi)

        then:
        def e = thrown (UnknownParameterTypeException)
        e.name == 'foo'
        e.type == 'unknown'
    }

    void "converts deprecated parameter"() {
        def openApi = parseApiBody ("""
            paths:
              /endpoint:
            
                get:
                  parameters:
                    - name: foo
                      description: deprecated parameter
                      in: query
                      deprecated: true
                      schema:
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
        def param = ep.parameters.first ()
        param.deprecated
    }
}
