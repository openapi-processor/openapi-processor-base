/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter


import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.model.datatypes.LazyDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.PropertyDataType
import spock.lang.Specification

import static com.github.hauner.openapi.core.test.FactoryHelper.apiConverter
import static com.github.hauner.openapi.core.test.OpenApiParser.parse


class DataTypeConverterLoopSpec extends Specification {

    void "handles \$ref loops"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test \$ref loop
  version: 1.0.0

paths:

  /self-reference:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Self'

components:
  schemas:

    Self:
      type: object
      properties:
        self:
          \$ref: '#/components/schemas/Self'
""")

        when:
        def api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rp = ep.getFirstResponse ('200')
        def rt = rp.responseType as ObjectDataType
        def pt = rt.properties.self
        def sf = pt.dataType
        rt instanceof ObjectDataType
        pt instanceof PropertyDataType
        sf instanceof LazyDataType
        sf.name == 'Self'
        sf.packageName == 'io.openapiprocessor.generated.model'
        sf.imports == ['io.openapiprocessor.generated.model.Self'] as Set
        sf.referencedImports == ['io.openapiprocessor.generated.model.Self'] as Set
    }

}
