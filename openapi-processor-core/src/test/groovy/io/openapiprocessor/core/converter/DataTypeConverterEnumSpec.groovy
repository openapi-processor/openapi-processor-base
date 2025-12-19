/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import spock.lang.Specification

import static io.openapiprocessor.core.support.FactoryHelper.apiConverter
import static io.openapiprocessor.core.support.OpenApiParserKt.parseApiBody

class DataTypeConverterEnumSpec extends Specification {

    void "converts enum query parameter"() {
        def openApi = parseApiBody ("""
            paths:
            
              /endpoint:
                get:
                  tags:
                    - enum
                  parameters:
                    - name: foo
                      description: enum parameter
                      in: query
                      schema:
                        type: string
                        enum:
                          - foo
                          - bar
                          - foo-bar
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
        param.dataType.name == 'Foo'
        (param.dataType as StringEnumDataType).values == ['foo', 'bar', 'foo-bar']
    }

    void "remembers inline & named enum schema data types"() {
        def openApi = parseApiBody ("""
            paths:
            
              /endpoint:
                get:
                  tags:
                    - enum
                  parameters:
                    - name: foo
                      description: enum parameter
                      in: query
                      schema:
                        type: string
                        enum:
                          - foo
                          - foo-2
                          - foo-foo
                    - name: bar
                      description: enum parameter
                      in: query
                      schema:
                        \$ref: '#/components/schemas/Bar'
                  responses:
                    '204':
                      description: empty
            
            components:
              schemas:
            
                Bar:
                  type: string
                  enum:
                    - bar
                    - bar-2
                    - bar-bar
            """)

        when:
        def api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        api.dataTypes.dataTypes.size () == 2
        api.dataTypes.dataTypes[0].name == 'Foo'
        api.dataTypes.dataTypes[1].name == 'Bar'
    }

    void "converts enum query parameter to string"() {
        def openApi = parseApiBody ("""
            paths:
            
              /endpoint:
                get:
                  tags:
                    - enum
                  parameters:
                    - name: foo
                      description: enum parameter
                      in: query
                      schema:
                        type: string
                        enum:
                          - foo
                          - bar
                          - foo-bar
                  responses:
                    '204':
                      description: empty
            """)

        when:
        def api = apiConverter (new ApiOptions (enumType: "string"))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.name == 'foo'
        param.dataType.typeName == 'String'
        param.dataType instanceof StringDataType
    }
}
