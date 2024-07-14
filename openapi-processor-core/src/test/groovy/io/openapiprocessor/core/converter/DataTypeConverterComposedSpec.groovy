/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter


import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import spock.lang.Specification

import static com.github.hauner.openapi.core.test.FactoryHelper.apiConverter
import static com.github.hauner.openapi.core.test.OpenApiParser.parse

class DataTypeConverterComposedSpec extends Specification {

    void "converts allOf composed schema object"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: composed schema
  version: 1.0.0

paths:

  /endpoint:
    get:
      responses:
        '200':
          description: allOf
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Foo'

components:
  schemas:
  
    Foo:
      allOf:
        - \$ref: '#/components/schemas/FooA'

    FooA:      
      type: object
      properties:
        fooA:
          type: string
  
""")

        when:
        def api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == 'FooA'
        rsp.responseType instanceof ObjectDataType

//        def cs = rsp.responseType as ComposedObjectDataType
//        cs.items.size () == 1
//        cs.items.first ().name == 'FooA'
    }

}
