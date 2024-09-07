/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.wrapper.NullDataTypeWrapper
import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.writer.java.JavaIdentifier
import io.openapiprocessor.core.writer.java.TestSchema
import spock.lang.Specification
import spock.lang.Unroll

import static io.openapiprocessor.core.support.ApiOptionsKt.parseOptionsMapping
import static io.openapiprocessor.core.support.FactoryHelper.apiConverter
import static io.openapiprocessor.core.support.OpenApiParser.parse
import static io.openapiprocessor.core.support.OpenApiParserKt.parseApiBody

class DataTypeConverterDeprecatedSpec extends Specification {

    @Unroll
    void "converts primitive deprecated schema(#type, #format) to datatype" () {
        def options = new ApiOptions()

        def converter = new DataTypeConverter(
                options,
                new JavaIdentifier(),
                new MappingFinder(options),
                Stub(NullDataTypeWrapper))

        def schema = new TestSchema (type: type, format: format, deprecated: deprecated)

        when:
        def datatype = converter.convert (
            new SchemaInfo (
                new SchemaInfo.Endpoint ("", HttpMethod.GET),
                "",
                "",
                schema,
                Stub(RefResolver),
                ""),
            new DataTypes())

        then:
        datatype.deprecated == result

        where:
        type      | format      | deprecated | result
        'string'  | null        | false      | false
        'string'  | null        | true       | true

        'string'  | 'date'      | false      | false
        'string'  | 'date'      | true       | true

        'string'  | 'date-time' | false      | false
        'string'  | 'date-time' | true       | true

        'integer' | null        | false      | false
        'integer' | null        | true       | true

        'integer' | 'int32'     | false      | false
        'integer' | 'int32'     | true       | true

        'integer' | 'int64'     | false      | false
        'integer' | 'int64'     | true       | true

        'number'  | null        | false      | false
        'number'  | null        | true       | true

        'number'  | 'float'     | false      | false
        'number'  | 'float'     | true       | true

        'number'  | 'double'    | false      | false
        'number'  | 'double'    | true       | true

        'boolean' | null        | false      | false
        'boolean' | null        | true       | true
    }


    void "converts deprecated schema object"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: deprecated schema
  version: 1.0.0

paths:

  /foo:
    get:
      responses:
        '200':
          description: deprecated
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Bar'

components:
  schemas:

    Bar:      
      type: object
      deprecated: true
      properties:
        foo:
          type: string
  
""")

        when:
        def api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == 'Bar'
        rsp.responseType.deprecated
    }

    void "converts deprecated schema object property"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: deprecated schema property
  version: 1.0.0

paths:

  /foo:
    get:
      responses:
        '200':
          description: deprecated
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Bar'

components:
  schemas:

    Bar:      
      type: object
      properties:
        foo:
          type: string
          deprecated: true
  
""")

        when:
        def api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == 'Bar'
        rsp.responseType.properties.foo.deprecated
    }


    void "converts deprecated enum schema"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: deprecated enum
  version: 1.0.0

paths:

  /endpoint:
    get:
      parameters:
        - name: foo
          description: deprecated enum
          in: query
          schema:
            type: string
            deprecated: true
            enum:
              - foo
              - bar
      responses:
        '204':
          description: empty
""")

        when:
        def api = apiConverter().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.dataType.deprecated
    }

    void "converts deprecated array type schema" () {
        def options = parseOptionsMapping("")

        def openApi = parseApiBody ("""
            |paths:
            |  /foo:
            |    get:
            |      parameters:
            |        - in: query
            |          name: foo
            |          schema:
            |            type: array
            |            deprecated: true
            |            items: 
            |              type: string
            |      responses:
            |        '204':
            |          description: none    
            """)

        when:
        def api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.dataType.deprecated
    }

    void "converts deprecated mapped array type schema" () {
        def options = parseOptionsMapping("""
            |mapping:
            |  types:
            |    - type: array => java.util.Collection
            """)

        def openApi = parseApiBody ("""
            |paths:
            |  /foo:
            |    get:
            |      parameters:
            |        - in: query
            |          name: foo
            |          schema:
            |            type: array
            |            deprecated: true
            |            items: 
            |              type: string
            |      responses:
            |        '204':
            |          description: none
            """)

        when:
        def api = apiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.dataType.deprecated
    }
}
