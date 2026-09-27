/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.framework.Framework
import io.openapiprocessor.core.support.ModelAsserts
import spock.lang.Specification

import static io.openapiprocessor.core.support.FactoryHelper.apiConverter
import static io.openapiprocessor.core.support.OpenApiParserKt.parseApiBody

class ApiConverterGSpec extends Specification implements ModelAsserts {

    void "sets interface package from processor options with 'api' sub package" () {
        def openApi = parseApiBody (
"""\
paths:
  /foo:
    get:
      responses:
        '204':
          description: no content
""")

        def options = new ApiOptions()
        options.packageName = 'a.package.name'
        options.packageOptions.base = options.packageName

        when:
        api = apiConverter (options, Stub(Framework))
            .convert (openApi)

        then:
        api.interfaces.first ().packageName == [options.packageName, 'api'].join ('.')
    }

    void "sets empty interface name when no interface name tag was provided" () {
        def openApi = parseApiBody (
"""\
paths:
  /foo:
    get:
      responses:
        '204':
          description: no content
""")

        when:
        api = apiConverter (Stub (Framework))
            .convert (openApi)

        then:
        api.interfaces.first ().interfaceName == 'Api'
    }
}
